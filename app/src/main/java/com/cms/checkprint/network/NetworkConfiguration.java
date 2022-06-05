package com.cms.checkprint.network;
/**
 * Created by chandra Mohan on 20/4/18.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkConfiguration {

    ////////////////////////////////////////////////////////////////////////////////////
    //*************************  SERVER CONFIGURATION ********************************//
    ////////////////////////////////////////////////////////////////////////////////////


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
