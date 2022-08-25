package com.cms.checkprint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivityOtpVerificationBinding;
import com.cms.checkprint.network.NetworkConfiguration;
import com.cms.checkprint.network.retrofit.apis.data.DataApiCallback;
import com.cms.checkprint.network.retrofit.apis.data.DataApiService;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Objects;

public class OtpVerificationActivity extends AppCompatActivity {
    ActivityOtpVerificationBinding binding;
    String otp;
    String ssn;
    String pdfUrl;
    private long associateId;
    private long chequeId;
    private boolean isfinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        Bundle bundle = getIntent().getExtras();

        binding.back.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.otpText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isfinish) {
                    onBackPressed();
                }
            }
        }, 120000);

        if (bundle != null) {
            String MobileNo = bundle.getString("MobileNo");
            otp = bundle.getString("OTPText");
            ssn = bundle.getString("SSNLastFiveDigit");
            pdfUrl = bundle.getString("pdf");
            String message = bundle.getString("message");
            boolean status = bundle.getBoolean("status");

            associateId = bundle.getLong("associateId");
            chequeId = bundle.getLong("chequeId");

        }

        binding.verify.setOnClickListener(view -> {
            String enteredOtp = Objects.requireNonNull(binding.textOTP.getText()).toString().trim();
            String enteredSSN = Objects.requireNonNull(binding.textSSN.getText()).toString().trim();
           /* if (enteredOtp.isEmpty()) {
                showMessage("Provide Security Key");
                return;
            }*/

            if (enteredSSN.isEmpty()) {
                showMessage("Provide first 3 digits of SSN");
                return;
            }

            if (!NetworkConfiguration.isNetworkAvailable(OtpVerificationActivity.this)) {
                showMessage("No internet connection available.");
                return;
            }
            validatePrintRequest("123", enteredSSN);
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void validatePrintRequest(String enteredOtp, String enteredSSn) {
        DataApiService.validatePrintRequest(associateId, chequeId, enteredOtp, enteredSSn, new DataApiCallback() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                Log.e("print request validate", jsonObject + " kk");
                try {
                    if (jsonObject != null) {
                        boolean status = jsonObject.get("Success").getAsBoolean();
                        String message = "";
                        if (!jsonObject.get("Message").isJsonNull())
                            message = jsonObject.get("Message").getAsString();

                        if (status) {
                            isfinish = false;
                            Bundle bundle = new Bundle();
                            bundle.putString("pdf", pdfUrl);
                            Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            showMessage(message);
                        }
                    } else
                        showMessage("Some thing went wrong.");
                } catch (Exception e) {
                    showMessage("Something went wrong");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showMessage("Some thing went wrong.");
            }
        });

    }
}