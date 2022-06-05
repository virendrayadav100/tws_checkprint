package com.cms.checkprint.helper;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String capitalizeWord(String str) {
        String capitalizeWord = "";
        try {
            if (str != null) {
                if (!str.isEmpty()) {
                    str = str.trim();
                    String words[] = str.split("\\s");
                    for (String w : words) {

                        String first;
                        String afterfirst;
                        if (w.length() == 0)
                            capitalizeWord += " ";
                        else if (w.length() == 1) {
                            first = w.substring(0, 1);
                            capitalizeWord += first.toUpperCase() + " ";
                        } else {
                            first = w.substring(0, 1);
                            afterfirst = w.substring(1);
                            capitalizeWord += first.toUpperCase() + afterfirst + " ";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return capitalizeWord.trim();
    }

    public static String getCurrentDateTime(){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMMM dd, yyyy hh:mm a");
           return dateFormat.format(new Date());
        }catch (Exception e){
            return "Today";
        }
    }

    private static byte[] getBytesFromBitmap(Bitmap bitmap){
        if(bitmap==null)
            return null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return byteArray;
        }catch (Exception e){
           return null;
        }
    }

    public static String getBase64FromBytes(Bitmap bitmap){
        byte[] bytes = getBytesFromBitmap(bitmap);
        if(bytes==null)
            return null;
        try {
            //String imageString =  Base64.encodeToString(bytes,Base64.NO_PADDING);
            String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
            System.out.println(" base64 : " + imageString);
            return imageString;
        }catch (Exception e){
           return null;
        }
    }
}
