package com.cms.checkprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivityConfigurationBinding;

import java.util.Objects;

public class ConfigurationActivity extends ParentActivity {

    ActivityConfigurationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_configuration);
        binding.clientApiInput.setText("https://checkprintapicommon.azurewebsites.net/api");

        if (preferences.getString("appUrl",null) != null) {
            startActivity(new Intent(ConfigurationActivity.this, CameraActivity.class));
            finish();
        }

        binding.save.setOnClickListener(view -> {
            String clientId = Objects.requireNonNull(binding.clientIdInput.getText()).toString().trim();
            String clientName = Objects.requireNonNull(binding.clientNameInput.getText()).toString().trim();
            String apiUrl = Objects.requireNonNull(binding.clientApiInput.getText()).toString().trim();

           /* if (clientId.isEmpty()) {
                Toast.makeText(this, "Provide client id", Toast.LENGTH_SHORT).show();
                return;
            }

            if (clientName.isEmpty()) {
                Toast.makeText(this, "Provide client name", Toast.LENGTH_SHORT).show();
                return;
            }*/

            if (apiUrl.isEmpty()) {
                Toast.makeText(this, "Provide api url", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("appUrl", apiUrl);
            editor.commit();
            startActivity(new Intent(ConfigurationActivity.this, CameraActivity.class));
            finish();

        });
    }
}