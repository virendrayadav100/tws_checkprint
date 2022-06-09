package com.cms.checkprint.network.retrofit.apis.data;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DataApis {

    @GET("validateassociatecode")
    Call<JsonObject> validateUser(@Query("associatecode") String associateCode);

    @POST("checkprint/IdentifyFace")
    Call<JsonObject> identifyFace(@Body JsonObject requestBodyData);

    @POST("checkprint/PrintRequest")
    Call<JsonObject> printRequest(@Body JsonObject requestBodyData);

}
