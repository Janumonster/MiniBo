package com.zzy.minibo.WeiBoTools;

public interface HttpCallBack {

    void onFinish(String response);

    void onError(Exception e);
}
