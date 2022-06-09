package com.cms.checkprint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.cms.checkprint.databinding.ActivityViewBinding;
import com.cms.checkprint.network.retrofit.apis.data.DataApiCallback;
import com.cms.checkprint.network.retrofit.apis.data.DataApiService;
import com.google.gson.JsonObject;

public class ViewActivity extends AppCompatActivity {
    ActivityViewBinding binding;
    private long associateId, chequeId;
    private String removePdfTopIcon = "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            associateId = bundle.getLong("id");
            String code = bundle.getString("code");
            String name = bundle.getString("name");
            String pic = bundle.getString("pic");
            String message = bundle.getString("message");
            boolean status = bundle.getBoolean("status");
            String clientName = bundle.getString("clientName");
            String chequeUrl = bundle.getString("chequeUrl");
            String PayWeekEnding = bundle.getString("PayWeekEnding");
            boolean chequePrinted = bundle.getBoolean("chequePrinted");
            chequeId = bundle.getLong("chequeId");


            Log.e("check url", chequeUrl + " kk");
            //chequeUrl = "http://www.africau.edu/images/default/sample.pdf";
            binding.clientName.setText(clientName);
            binding.empName.setText("Name : " + name);
            binding.empNo.setText("Number : " + code);
            binding.payWeekEnding.setText("Pay Week Ending :" + (PayWeekEnding.isEmpty() ? " NA" : " " + PayWeekEnding));
            binding.message.setText(Html.fromHtml(message));

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

            if (status && !chequePrinted) {
                binding.print.setVisibility(View.VISIBLE);
                if (chequeUrl != null && !chequeUrl.isEmpty()) {
                    binding.pdfView.setVisibility(View.VISIBLE);
                    binding.pdfHeading.setVisibility(View.VISIBLE);
                    //binding.pdfView.getSettings().setJavaScriptEnabled(true);
                   // binding.pdfView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + chequeUrl);
                    //binding.pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + chequeUrl);
                    showPdfFile(chequeUrl);
                }
                binding.print.setOnClickListener(view -> {
                    printRequest();
                });
            } else {
                binding.empName.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        printRequest();
                    }
                }, 2500);
            }
        } else finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        finish();
    }

    private void printRequest() {
        DataApiService.printRequest(associateId, chequeId, new DataApiCallback() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                Log.e("print request", jsonObject + " kk");
                try {
                    if (jsonObject != null) {
                        boolean status = jsonObject.get("Success").getAsBoolean();
                        String message = "";
                        if (!jsonObject.get("Message").isJsonNull())
                            message = jsonObject.get("Message").getAsString();


                        String mobileNo = "";
                        String OTPText = "";
                        int Id = 0;
                        ;
                        String SSNLastFiveDigit = "";
                        if (!jsonObject.get("MobileNo").isJsonNull())
                            mobileNo = jsonObject.get("MobileNo").getAsString();
                        if (!jsonObject.get("OTPText").isJsonNull())
                            OTPText = jsonObject.get("OTPText").getAsString();
                        if (!jsonObject.get("SSNLastFiveDigit").isJsonNull())
                            SSNLastFiveDigit = jsonObject.get("SSNLastFiveDigit").getAsString();
                        if (jsonObject.has("Id"))
                            Id = jsonObject.get("Id").getAsInt();

                        if (status) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", Id);
                            bundle.putString("MobileNo", mobileNo);
                            bundle.putString("OTPText", OTPText);
                            bundle.putString("SSNLastFiveDigit", SSNLastFiveDigit);
                            bundle.putString("message", message);
                            bundle.putBoolean("status", true);
                            Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            showMessage(message);
                        }
                    } else {
                        showMessage("Some thing went wrong.");
                    }
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

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }




    private void showPdfFile(final String imageString) {
        showProgress();
        binding.pdfView.invalidate();
        binding.pdfView.getSettings().setJavaScriptEnabled(true);
        binding.pdfView.getSettings().setSupportZoom(true);
        binding.pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + imageString);
        binding.pdfView.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
                    binding.pdfView.loadUrl(removePdfTopIcon);
                    hideProgress();
                } else {
                    showPdfFile(imageString);
                }
            }
        });
    }

    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }
}