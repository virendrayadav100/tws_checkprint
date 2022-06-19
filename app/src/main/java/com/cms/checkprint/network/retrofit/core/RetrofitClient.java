package com.cms.checkprint.network.retrofit.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.cms.checkprint.helper.MyApplication;
import com.cms.checkprint.helper.MySharedPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static Context context;
    private static String baseUrl ="";
    //public static String baseUrl = "https://wtspinappapicommon.azurewebsites.net/api/";

    public static <S> S getRetrofitClient(Class<S> serviceClass) {

        context = MyApplication.getAppContext();
        MySharedPreference mySharedPreference = new MySharedPreference(context);
       SharedPreferences  sharedPreferences = mySharedPreference.getSharedPreference();
        baseUrl = sharedPreferences.getString("appUrl","");
        //Configuration configuration = (Configuration) mySharedPreference.getSharedPreference(MySharedPreference.KEY_CONFIGURATION);

        Gson gson = new GsonBuilder()
                //.registerTypeAdapter(Date.class,new UtcDateTypeAdapter())
                //.registerTypeAdapter(Date.class,new GsonDateSerializer())
                //.registerTypeAdapter(Date.class,new GsonDateDeserializer())
                .create();

        if (retrofit == null) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl+"/")
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }catch ( Exception e){

            }
        }
        return retrofit.create(serviceClass);
    }
}
