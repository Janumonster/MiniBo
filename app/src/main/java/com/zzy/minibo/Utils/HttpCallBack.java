package com.zzy.minibo.Utils;

public interface HttpCallBack {

    void onSuccess(String response);

    void onError(Exception e);
}
