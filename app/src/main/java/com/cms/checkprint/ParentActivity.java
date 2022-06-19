package com.cms.checkprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cms.checkprint.helper.MySharedPreference;

public class ParentActivity extends AppCompatActivity {

    protected MySharedPreference mySharedPreference;
    protected Context mContent;
    protected AppCompatActivity appCompatActivity;
    protected SharedPreferences preferences;
    protected String appUrl =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = ParentActivity.this;
        mContent = getApplicationContext();
        mySharedPreference = new MySharedPreference(mContent);
        preferences =  mySharedPreference.getSharedPreference();
        preferences.getString("appUrl",null);
    }
}