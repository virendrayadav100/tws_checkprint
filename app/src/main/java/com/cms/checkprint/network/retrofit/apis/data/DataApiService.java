package com.cms.checkprint.network.retrofit.apis.data;


import com.cms.checkprint.network.retrofit.core.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataApiService {


    /*public static void validateAssociate(String associateCode, final DataApiCallback dataApiCallback) {
        DataApis dataApis = RetrofitClient.getRetrofitClient(DataApis.class);

        Call<JsonObject> call = dataApis.validateUser(associateCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dataApiCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dataApiCallback.onFailure(t);
            }
        });
    }*/

    public static void identifyFace(String base64Image, final DataApiCallback dataApiCallback) {
        DataApis dataApis = RetrofitClient.getRetrofitClient(DataApis.class);
        JsonObject dataRequest = new JsonObject();
        dataRequest.addProperty("ImageBase64",base64Image);

        Call<JsonObject> call = dataApis.identifyFace(dataRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dataApiCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dataApiCallback.onFailure(t);
            }
        });
    }

    public static void printRequest(long associateId,long chequeId, final DataApiCallback dataApiCallback) {
        DataApis dataApis = RetrofitClient.getRetrofitClient(DataApis.class);
        JsonObject dataRequest = new JsonObject();
        dataRequest.addProperty("AssociateId",associateId);
        dataRequest.addProperty("CheckId",chequeId);

        Call<JsonObject> call = dataApis.printRequest(dataRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dataApiCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dataApiCallback.onFailure(t);
            }
        });
    }


    public static void validatePrintRequest(long associateId,long chequeId,String otp,String ssn, final DataApiCallback dataApiCallback) {
        DataApis dataApis = RetrofitClient.getRetrofitClient(DataApis.class);
        JsonObject dataRequest = new JsonObject();
        dataRequest.addProperty("AssociateId",associateId);
        dataRequest.addProperty("CheckId",chequeId);
        dataRequest.addProperty("OTP",otp);
        dataRequest.addProperty("SSN",ssn);

        Call<JsonObject> call = dataApis.validatePrintRequest(dataRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dataApiCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dataApiCallback.onFailure(t);
            }
        });
    }



}

