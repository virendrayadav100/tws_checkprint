package com.cms.checkprint.network;

public interface ApiCallBack {
    void onSuccess(String tokenId);

    void onFailure(String error);
}
