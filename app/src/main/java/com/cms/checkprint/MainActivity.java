package com.cms.checkprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.cms.checkprint.databinding.ActivityMainBinding;
import com.cms.checkprint.selfie.SelfieActivity;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static int SELFIE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            String path = bundle.getString("image_path");
            Bitmap bmImg = BitmapFactory.decodeFile(path);
            binding.profileImage.setImageBitmap(bmImg);
        }

       /* binding.btnOpenCamera.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, SelfieActivity.class),SELFIE_REQUEST_CODE);
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELFIE_REQUEST_CODE && resultCode== Activity.RESULT_OK) {
            String imagePath = data.getStringExtra(SelfieActivity.KEY_IMAGE_PATH);
            Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath);
            binding.profileImage.setImageBitmap(bmImg);
        }
    } //onActivityResult
}
