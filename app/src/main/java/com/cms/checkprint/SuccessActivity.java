package com.cms.checkprint;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivitySuccessBinding;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SuccessActivity extends AppCompatActivity {

    private PrintManager printManager;
    private String pdfUrl;
    private  long timeStamp=0;

    ActivityResultLauncher<String[]> requestPermissionForStorageLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if (permissions != null) {
                    if (permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downloadPdf();
                    } else
                        Toast.makeText(SuccessActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySuccessBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_success);

        timeStamp = new Date().getTime();

        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pdfUrl = bundle.getString("pdf");
            Log.e("pdfUrl", pdfUrl + " k");

            if (ContextCompat.checkSelfPermission(SuccessActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissionForStorageLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
            else {
                downloadPdf();
            }
        }

        binding.back.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void downloadPdf() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("Check Print");
        request.setMimeType("applcation/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "check_print_"+timeStamp+".pdf");
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
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SuccessActivity.this, "Downloaded successfully.", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "check_print_"+timeStamp+".pdf");
                                    Log.e("path", file.getPath() + " k");

                                    if (file.exists()) {//File Exists
                                        Log.e("file", "exist");
                                    }

                                    PrintAttributes attributes = new PrintAttributes.Builder()
                                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                                            .setResolution(new PrintAttributes.Resolution("id", PRINT_SERVICE, 300, 300))
                                            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                                            .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                                            .build();
                                    PageRange[] ranges = new PageRange[]{new PageRange(1, 1)};

                                    String jobName = getString(R.string.app_name) + " Document";
                                    PrintJob printJob = printManager.print(jobName, new PDFPrintDocumentAdapter(SuccessActivity.this, "check_print_"+timeStamp, file.getPath()), attributes);
                                    Toast.makeText(SuccessActivity.this, "Unable to print", Toast.LENGTH_SHORT).show();
                                },900);

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
                        timer.cancel();
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
        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        finish();
    }
}