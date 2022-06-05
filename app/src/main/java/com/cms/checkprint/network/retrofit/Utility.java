package com.cms.checkprint.network.retrofit;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;


public class Utility {

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static String byteArrayToBase64(byte[] bytes) {
      return  Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
