package com.cms.checkprint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivityPrintingProcessBinding;
import com.dynamixsoftware.intentapi.IJob;
import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.IServiceCallback;
import com.dynamixsoftware.intentapi.IntentAPI;
import com.dynamixsoftware.intentapi.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PrintingProcessActivity extends AppCompatActivity {

    ActivityPrintingProcessBinding binding;
    //private PrintManager printManager;
    private String pdfUrl;
    private long timeStamp = 0;
    private IntentAPI intentApi;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    ActivityResultLauncher<String[]> requestPermissionForStorageLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (permissions != null) {
                    if (permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downloadPdf();
                    } else
                        Toast.makeText(PrintingProcessActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_printing_process);

        timeStamp = new Date().getTime();
        // printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        Bundle bundle = getIntent().getExtras();

        intentApi = new IntentAPI(this); // some features not worked if initialized without activity
        Context appContext = getApplicationContext();

        try {
            Log.e("run service", "reached here k");
            intentApi.runService(new IServiceCallback.Stub() {
                @Override
                public void onServiceDisconnected() {
                    toastInMainThread(appContext, "Print service disconnected");
                }

                @Override
                public void onServiceConnected() {
                    toastInMainThread(appContext, "Print service connected");
                    try {
                        intentApi.setPrintCallback(new IPrintCallback.Stub() {
                            @Override
                            public void startingPrintJob() {
                                toastInMainThread(appContext, "Start printing...");
                            }

                            @Override
                            public void start() {
                                toastInMainThread(appContext, "start");
                            }

                            @Override
                            public void sendingPage(int pageNum, int progress) {
                                toastInMainThread(appContext, "Sending page number " + pageNum + ", progress " + progress);
                            }

                            @Override
                            public void preparePage(int pageNum) {
                                toastInMainThread(appContext, "Prepare page number " + pageNum);
                            }

                            @Override
                            public boolean needCancel() {
                                toastInMainThread(appContext, "Need Cancel");
                                // If you need to cancel printing send true
                                return false;
                            }

                            @Override
                            public void finishingPrintJob() {
                                toastInMainThread(appContext, "Finishing Print Job");
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("print", "finish");
                                        startActivity(new Intent(PrintingProcessActivity.this, SuccessNewActivity.class));
                                        PrintingProcessActivity.this.finish();
                                    }
                                });
                            }

                            @Override
                            public void finish(Result result, int pagesPrinted) {
                                toastInMainThread(appContext, "finish, Result " + result + "; Result type " + result.getType() + "; Result message " + result.getType().getMessage() + "; pages printed " + pagesPrinted);
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        toastInMainThread(appContext, e.getMessage());
                        onBackPressed();
                    }
                }

                @Override
                public void onFileOpen(int progress, int finished) {
                    toastInMainThread(appContext, "onFileOpen progress " + progress + "; finished " + (finished == 1));
                }

                @Override
                public void onLibraryDownload(int progress) {
                    toastInMainThread(appContext, "onLibraryDownload progress " + progress);
                }

                @Override
                public boolean onRenderLibraryCheck(boolean renderLibrary, boolean fontLibrary) {
                    toastInMainThread(appContext, "onRenderLibraryCheck render library " + renderLibrary + "; fonts library " + fontLibrary);
                    return true;
                }

                @Override
                public String onPasswordRequired() {
                    toastInMainThread(appContext, "onPasswordRequired");
                    return "password";
                }

                @Override
                public void onError(Result result) {
                    toastInMainThread(appContext, "error, Result " + result + "; Result type " + result.getType());
                    onBackPressed();
                }
            });

            try {
                IPrinterInfo printer = intentApi.getCurrentPrinter();
                binding.printerName.setText((printer != null ? printer.getName() : "Printer not available"));
                if (printer == null)
                    onBackPressed();
            } catch (Exception ex) {
                binding.printerName.setText("Printer not available");
                onBackPressed();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            toastInMainThread(appContext, e.getMessage());
            onBackPressed();
        }

        if (bundle != null) {
            pdfUrl = bundle.getString("pdf");
            Log.e("pdfUrl", pdfUrl + " k");
            if (ContextCompat.checkSelfPermission(PrintingProcessActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissionForStorageLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            else {
                downloadPdf();
            }
        }
    }

    private void downloadPdf() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("Check Print");
        request.setMimeType("image/png");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "check_print_" + timeStamp + ".png");
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  subPath);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = dm.query(query);
        // Thread.sleep(10000);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (c != null && c.moveToNext()) {
                    @SuppressLint("Range") int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PrintingProcessActivity.this, "Downloaded successfully.", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "check_print_" + timeStamp + ".png");
                                    Log.e("path", file.getPath() + " k");

                                    if (file.exists()) {//File Exists
                                        Log.e("file", "exist");
                                    }

                                  /*  String jobName = "";
                                    try {
                                        jobName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
                                        jobName = jobName.substring(0, jobName.lastIndexOf("."));
                                        Log.e("name", jobName);
                                    } catch (Exception ignored) {
                                    }

                                    if (jobName.isEmpty())
                                        jobName = "Check Print Document";*/

                                    try {
                                        Uri u = Uri.fromFile(file);
                                        Log.e("url", u.toString());
                                        //intentApi.print("PrintingSample", "image/png", FilesUtils.getFileUriWithPermission(getApplicationContext(), FilesUtils.FILE_PDF));
                                        //intentApi.print(jobName, "image/png", u);
                                        //intentApi.print("PrintingSample", "image/png", FilesUtils.getFileUriWithPermission(getApplicationContext(), file));

                                        try {
                                            IJob.Stub job = new IJob.Stub() {
                                                @Override
                                                public Bitmap renderPageFragment(int num, Rect fragment) throws RemoteException {
                                                    IPrinterInfo printer = intentApi.getCurrentPrinter();
                                                    if (printer != null) {
                                                        Log.e("width", String.valueOf(fragment.width()));
                                                        Log.e("height", String.valueOf(fragment.height()));
                                                        Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Bitmap.Config.ARGB_8888);
                                                        for (int i = 0; i < 3; i++)
                                                            try {
                                                                BitmapFactory.Options options = new BitmapFactory.Options();
                                                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                                                options.inDither = false;
                                                                //if (i > 0) {
                                                                options.inSampleSize = 1;
                                                                //}
                                                                Bitmap imageBMP = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                                                                Paint p = new Paint();
                                                                int imageWidth = 0;
                                                                int imageHeight = 0;
                                                                if (imageBMP != null) {
                                                                    imageWidth = imageBMP.getWidth();
                                                                    imageHeight = imageBMP.getHeight();
                                                                }

                                                                Log.e("img width", String.valueOf(imageWidth));
                                                                Log.e("img height", String.valueOf(imageHeight));

                                                                int xDpi = printer.getPrinterContext().getHResolution();
                                                                int yDpi = printer.getPrinterContext().getVResolution();

                                                                Log.e("xDpi ", String.valueOf(xDpi));
                                                                Log.e("yDpi ", String.valueOf(yDpi));

                                                                // in dots
                                                                int paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
                                                                int paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;

                                                                Log.e("paperWidth ", String.valueOf(paperWidth));
                                                                Log.e("paperHeight ", String.valueOf(paperHeight));


                                                                float aspectH = (float) imageHeight / (float) paperHeight;
                                                                float aspectW = (float) imageWidth / (float) paperWidth;
                                                                RectF dst = new RectF(0, 0, fragment.width() * aspectW, fragment.height() * aspectH);

                                                                Log.e("rectwitdth ", String.valueOf(fragment.width() * aspectW));
                                                                Log.e("rectheight ", String.valueOf(fragment.height() * aspectH));


                                                                float sLeft = 0;
                                                                float sTop = fragment.top * aspectH;
                                                                float sRight = imageWidth;
                                                                float sBottom = fragment.top * aspectH + fragment.bottom * aspectH;
                                                                RectF source = new RectF(sLeft, sTop, sRight, sBottom);
                                                                Canvas canvas = new Canvas(bitmap);
                                                                canvas.drawColor(Color.WHITE);
                                                                // move image to actual printing area
                                                                dst.offsetTo(dst.left - fragment.left, dst.top - fragment.top);
                                                                Matrix matrix = new Matrix();
                                                                matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
                                                                canvas.drawBitmap(imageBMP, matrix, p);
                                                                break;
                                                            } catch (IOException ex) {
                                                                ex.printStackTrace();
                                                                break;
                                                            } catch (OutOfMemoryError ex) {
                                                                if (bitmap != null) {
                                                                    bitmap.recycle();
                                                                    bitmap = null;
                                                                }
                                                                continue;
                                                            }
                                                        return bitmap;
                                                    } else {
                                                        return null;
                                                    }
                                                }

                                                @Override
                                                public int getTotalPages() {
                                                    return 1;
                                                }
                                            };
                                            intentApi.print(job, 1);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                            toastInMainThread(PrintingProcessActivity.this, e.getMessage());
                                            onBackPressed();
                                        }

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        toastInMainThread(PrintingProcessActivity.this, ex.getMessage());
                                        onBackPressed();
                                    }
                                    //intentApi.print(jobName, "application/pdf", u);

                                }, 900);

                            }
                        });
                        timer.cancel();
                    } else if (status == DownloadManager.STATUS_FAILED) {
                       /* runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SuccessActivity.this, "STATUS_FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                        Toast.makeText(PrintingProcessActivity.this, "STATUS_FAILED", Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        onBackPressed();
                    } else {
                       /* runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SuccessActivity.this, "STATUS_FAILED111", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                        run();
                    }
                }
            }
        }, 1000);
    }


    private boolean isFileExists() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/temp.pdf";
        File file = new File(path);
        return file.exists();
    }

    public boolean deleteFile() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/temp.pdf";
        File file = new File(path);
        return file.delete();
    }


    @Override
    public void onBackPressed() {
        if (intentApi != null) {
            intentApi.stopService(null);
            try {
                intentApi.setServiceCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                intentApi.setPrintCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            intentApi = null;
        }

        binding.printerName.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                finish();
            }
        }, 4000);
    }


    private void toastInMainThread(final Context appContext, final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                binding.message.setText(message);
                Log.e("message", message);
            }
        });
    }
}