package com.cms.checkprint.helper;

import android.app.Application;
import android.content.Context;

public class MyApplication  extends Application {

    private static MyApplication applicationInstance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
        MyApplication.context = getApplicationContext();
    }

    public static synchronized MyApplication getInstance() {
        return applicationInstance;
    }


    public static Context getAppContext() {
        return MyApplication.context;
    }

}
