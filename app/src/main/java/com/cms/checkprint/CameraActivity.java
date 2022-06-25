package com.cms.checkprint;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cms.checkprint.camera.CameraSource;
import com.cms.checkprint.databinding.ActivityCameraBinding;
import com.cms.checkprint.facedetection.FaceContourDetectorProcessor;
import com.cms.checkprint.helper.Utility;
import com.cms.checkprint.network.NetworkConfiguration;
import com.cms.checkprint.network.retrofit.apis.data.DataApiCallback;
import com.cms.checkprint.network.retrofit.apis.data.DataApiService;
import com.cms.checkprint.util.MyPermissionUtil;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity implements FaceContourDetectorProcessor.FaceContourDetectorListener {

    private static String KEY_IMAGE_PATH = "image_path";
    private static String KEY_TEXT_BACK = "text_back";
    private static String KEY_TEXT_DESCRIPTION = "text_description";
    private static int PERMISSION_CAMERA_REQUEST_CODE = 2;
    ActivityCameraBinding binding;
    private CameraSource mCameraSource = null;
    private Bitmap mCapturedBitmap = null;
    private FaceContourDetectorProcessor mFaceContourDetectorProcessor;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        if (new MyPermissionUtil().isHavePermission(this, PERMISSION_CAMERA_REQUEST_CODE, Manifest.permission.CAMERA)) {
            createCameraSource();
        }
        startCameraSource();
    }

    @Override
    public void onCapturedFace(@NonNull Bitmap originalCameraImage) {
        try {
            if (originalCameraImage != null) {
                mCapturedBitmap = originalCameraImage;
                //createSelfiePictureAndReturn();
                if (!isProcessing) {
                    isProcessing = true;
                    if (NetworkConfiguration.isNetworkAvailable(CameraActivity.this))
                        identifyFace(originalCameraImage);
                    else showMessage("No internet connection available.");
                }
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage() + " kk1");
        }
    }

    @Override
    public void onNoFaceDetected() {
        mCapturedBitmap = null;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createSelfiePictureAndReturn() {
        try {
            File file = new File(getCacheDir(), "selfie.jpg");
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mCapturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Log.e("error", e.getMessage() + " kk");
        }
    }

    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (mCameraSource == null) {
            mCameraSource = new CameraSource(this, binding.faceOverlay);
        }

        //Rose.error("Using Face Contour Detector Processor");
        mFaceContourDetectorProcessor = new FaceContourDetectorProcessor(this, false);
        mCameraSource.setMachineLearningFrameProcessor(mFaceContourDetectorProcessor);
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        try {
            binding.cameraPreview.start(mCameraSource, binding.faceOverlay);
        } catch (Exception e) {

        }
       /* mCameraSource?.let {
            try {
                camera_preview.start(mCameraSource, face_overlay)
            } catch (e: IOException) {
                Rose.error("Unable to start camera source.  $e")
                mCameraSource?.release()
                mCameraSource = null
            }
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.cameraPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mCameraSource.release();
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (new MyPermissionUtil().isPermissionGranted(requestCode, PERMISSION_CAMERA_REQUEST_CODE, grantResults))
                createCameraSource();
            else
                onBackPressed();
        }
    }

    private void identifyFace(Bitmap bitmap) {
        String base64String = Utility.getBase64FromBytes(bitmap);
        if (base64String != null) {
            DataApiService.identifyFace(base64String, new DataApiCallback() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    Log.e("identify", jsonObject + " kk");
                    try {
                        if (jsonObject != null) {
                            boolean status = jsonObject.get("SuccessStatus").getAsBoolean();
                            String message = "";
                            if (!jsonObject.get("Message").isJsonNull())
                                message = jsonObject.get("Message").getAsString();
                            String pic = null;

                            JsonObject jsonDataObject = null;
                            if (!jsonObject.get("ResponseModel").isJsonNull()) {
                                jsonDataObject = jsonObject.getAsJsonObject("ResponseModel");
                                if (jsonDataObject != null) {
                                    long empId = 0;
                                    String empCode = "";
                                    String empName = "";
                                    String clientName ="";
                                    String chequeUrl = "";
                                    String PayWeekEnding = "";
                                    boolean chequePrinted=false;
                                    long chequeId =0;
                                    empId = jsonDataObject.get("AssociateId").getAsLong();
                                    empCode = jsonDataObject.get("AssociateCode").getAsString();
                                    empName = jsonDataObject.get("AssociateName").getAsString();
                                    clientName =jsonDataObject.get("ClientName").getAsString();
                                    if (!jsonDataObject.get("CheckURL").isJsonNull())
                                        chequeUrl =jsonDataObject.get("CheckURL").getAsString();
                                    if (!jsonDataObject.get("PayWeekEnding").isJsonNull())
                                    PayWeekEnding =jsonDataObject.get("PayWeekEnding").getAsString();
                                    chequePrinted =jsonDataObject.get("CheckPrinted").getAsBoolean();
                                    chequeId = jsonDataObject.get("CheckId").getAsLong();

                                    if (!jsonDataObject.get("AssociateImage").isJsonNull()) {
                                        pic = jsonDataObject.get("AssociateImage").getAsString();
                                        if (pic.trim().length() > 0) {
                                            pic = "https://tempworkstaffing.com/profilepicture/" + pic;
                                        }
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putLong("id", empId);
                                    bundle.putString("code", empCode);
                                    bundle.putString("name", empName);
                                    bundle.putString("pic", pic);
                                    bundle.putString("message", message);
                                    bundle.putBoolean("status", status);
                                    bundle.putString("clientName", clientName);
                                    bundle.putString("chequeUrl", chequeUrl);
                                    bundle.putString("PayWeekEnding", PayWeekEnding);
                                    bundle.putBoolean("chequePrinted", chequePrinted);
                                    bundle.putLong("chequeId", chequeId);
                                    Intent intent = new Intent(CameraActivity.this, ViewActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    isProcessing = false;
                                    //showMessage(message);
                                }
                            } else {
                                isProcessing = false;
                                //showMessage(message);
                            }

                        } else {
                            isProcessing = false;
                            showMessage("Some thing went wrong.");
                            // refreshActivity();
                        }
                    } catch (Exception e) {
                        isProcessing = false;
                        showMessage("Something went wrong");
                        // refreshActivity();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    isProcessing = false;
                    showMessage("Some thing went wrong.");
                    // refreshActivity();
                }
            });
        } else {
            isProcessing = false;
            // refreshActivity();
        }
    }

    private void refreshActivity() {
        startActivity(new Intent(this, CameraActivity.class));

    }
}