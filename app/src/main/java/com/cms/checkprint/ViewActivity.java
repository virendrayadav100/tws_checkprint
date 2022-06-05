package com.cms.checkprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.cms.checkprint.databinding.ActivityViewBinding;

public class ViewActivity extends AppCompatActivity {
    ActivityViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            long id = bundle.getLong("id");
            String code = bundle.getString("code");
            String name = bundle.getString("name");
            String pic = bundle.getString("pic");
            String message = bundle.getString("message");

            binding.empName.setText("Name : " + name);
            binding.empNo.setText("Number : " + code);
            binding.message.setText(Html.fromHtml(message));
           /* if (status)
                binding.message.setTextColor(getResources().getColor(R.color.green));
            } else {
                binding.statusImage.setImageResource(R.drawable.ic_cancel_red1);
                binding.status.setTextColor(getResources().getColor(R.color.red));
                binding.message.setTextColor(getResources().getColor(R.color.red));
            }*/

            Log.e("pic", pic + " kkkk");
            if (pic == null || pic.isEmpty()) {
                Glide
                        .with(getApplicationContext())
                        .load(R.drawable.ic_user_profile)
                        .into(binding.profilePic);
            } else {
                Glide
                        .with(getApplicationContext())
                        .load(pic)
                        .into(binding.profilePic);
            }
        } else finish();
    }
}