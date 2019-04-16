package com.zzy.minibo.WBListener;

public interface HttpCallBack {

    void onSuccess(String response);

    void onError(Exception e);
}
