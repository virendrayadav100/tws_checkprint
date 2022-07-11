package com.cms.checkprint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PDFPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private String filePath;
    private String fileName;

    /**
     * @param context
     * @param fileName - for Print Document Info
     * @param filePath - PDF file to be printed, stored in external storage
     */
    public PDFPrintDocumentAdapter(Context context, String fileName, String filePath) {

        this.context = context;
        this.fileName = fileName;
        this.filePath = filePath;

    }

    @Override
    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
        layoutResultCallback.onLayoutFinished(pdi, true);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
        InputStream input = null;
        OutputStream output = null;

        try {

            input = new FileInputStream(filePath);
            output = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee) {
            Log.e("error1",ee.getMessage()+" kkk");
            //Catch exception
        } catch (Exception e) {
            Log.e("error2",e.getMessage()+" kkk");
            //Catch exception
        } finally {
            try {
                if (null != input) {
                    input.close();
                }
                if (null != output) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error3",e.getMessage()+" kkk");
            }
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //Toast.makeText(context, "Printing is completed", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, CameraActivity.class));
        ((Activity)context).finishAffinity();
    }
}
