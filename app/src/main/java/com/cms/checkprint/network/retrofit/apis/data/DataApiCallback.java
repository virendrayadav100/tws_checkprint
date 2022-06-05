package com.cms.checkprint.network.retrofit.apis.data;

import com.google.gson.JsonObject;

public interface DataApiCallback {
    void onSuccess(JsonObject jsonObject);
    void onFailure(Throwable t);
}
