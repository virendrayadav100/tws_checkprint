package com.cms.checkprint;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.http.Header;

public class SuccessActivity extends AppCompatActivity {

    private PrintManager printManager;
    private String pdfUrl;

    ActivityResultLauncher<String[]> requestPermissionForStorageLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                if(permissions!=null) {
                    if (permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downloadPdf();
                    } else
                        Toast.makeText(SuccessActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pdfUrl = bundle.getString("pdf");
            Log.e("pdfUrl", pdfUrl + " k");

            if (ContextCompat.checkSelfPermission(SuccessActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
                requestPermissionForStorageLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            else{
                downloadPdf();
            }

        }
    }

    private void downloadPdf(){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("temp");
        request.setMimeType("applcation/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "temp.pdf");
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


                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "temp.pdf");
                                Log.e("path",file.getPath()+" k");

                                if(file.exists()){//File Exists
                                     };
                                Toast.makeText(SuccessActivity.this, "Downloaded successfully.", Toast.LENGTH_SHORT).show();

                                String jobName = getString(R.string.app_name) + " Document";
                                PrintJob printJob = printManager.print(jobName, new PDFPrintDocumentAdapter(SuccessActivity.this, "Test", file.getPath()), null);
                            }
                        });
                        timer.cancel();
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SuccessActivity.this, "STATUS_FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });
                        timer.cancel();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SuccessActivity.this, "STATUS_FAILED111", Toast.LENGTH_SHORT).show();
                            }
                        });
                        run();
                    }
                }
            }
        }, 1000);
    }


    private boolean isFileExists(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/temp.pdf";
        File file = new File(path);
        return file.exists();
    }

    public boolean deleteFile(){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/temp.pdf";
        File file = new File(path);
        Log.e("delete","deleted");
        Log.e("delete",file.getPath()+" kk");
        return file.delete();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        finish();
    }
}