package com.cms.checkprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.cms.checkprint.databinding.ActivityViewBinding;

public class ViewActivity extends AppCompatActivity {
    ActivityViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_view);
    }
}