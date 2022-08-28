package com.cms.checkprint;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivitySuccessNewBinding;

public class SuccessNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySuccessNewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_success_new);

        binding.back.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                finish();
            }
        }, 20000);

        binding.back.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            finish();
        });
    }
}