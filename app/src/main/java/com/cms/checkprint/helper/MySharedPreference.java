package com.cms.checkprint.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class MySharedPreference {
    /*************************************************************/
    /**************** PREFERENCE CONFIGURATION *******************/
    /*************************************************************/
    private static final String PREFERENCE_NAME = "FacePrint";
    private static final int PREFERENCE_MODE = Context.MODE_PRIVATE;

    public static final String KEY_CONFIGURATION = "configuration";
    private Context mContext;

    public MySharedPreference(Context mContext) {
        this.mContext = mContext;
    }

    public SharedPreferences getSharedPreference() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, PREFERENCE_MODE);
    }

    public SharedPreferences.Editor getSharedPreferenceEditor() {
        return getSharedPreference().edit();
    }

    public void saveSharedPreference(String key, Object object) {
        SharedPreferences.Editor preferenceEditor = getSharedPreferenceEditor();
        Gson gson = new Gson();
        if (key.equals(KEY_CONFIGURATION)) {
            String json = gson.toJson(object);
            preferenceEditor.putString(KEY_CONFIGURATION, json);
        }
        preferenceEditor.commit();
    }

    /*public Object getSharedPreference(String key) {
        try {
            SharedPreferences sharedPreference = getSharedPreference();
            Gson gson = new Gson();
            if (key.equals(KEY_CONFIGURATION)) {
                String json = sharedPreference.getString(KEY_CONFIGURATION, "");
                Configuration configuration = gson.fromJson(json, Configuration.class);
                return configuration;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
