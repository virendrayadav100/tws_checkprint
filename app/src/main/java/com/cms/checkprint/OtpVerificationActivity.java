package com.cms.checkprint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.databinding.ActivityOtpVerificationBinding;

import java.util.Objects;

public class OtpVerificationActivity extends AppCompatActivity {
    ActivityOtpVerificationBinding binding;
    String otp;
    String ssn;
    String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification);
        Bundle bundle = getIntent().getExtras();

        binding.otpText.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  if(isfinish) {
                onBackPressed();
                // }
            }
        }, 20000);

        if (bundle != null) {
            String MobileNo = bundle.getString("MobileNo");
            otp = bundle.getString("OTPText");
            ssn = bundle.getString("SSNLastFiveDigit");
            pdfUrl = bundle.getString("pdf");
            String message = bundle.getString("message");
            boolean status = bundle.getBoolean("status");
        }

        binding.verify.setOnClickListener(view -> {
            String enteredOtp = Objects.requireNonNull(binding.textOTP.getText()).toString().trim();
            String enteredSSN = Objects.requireNonNull(binding.textSSN.getText()).toString().trim();
            if (enteredOtp.isEmpty()) {
                showMessage("Provide OTP");
                return;
            }

            if (enteredSSN.isEmpty()) {
                showMessage("Provide SSN last five digit");
                return;
            }
            if (!enteredOtp.equals(otp)) {
                showMessage("Otp not match");
                return;
            }

            if (!enteredSSN.equals(ssn)) {
                showMessage("SSN last five digit not match");
                return;
            }

            showMessage("Printing success");
            startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            finish();
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
}