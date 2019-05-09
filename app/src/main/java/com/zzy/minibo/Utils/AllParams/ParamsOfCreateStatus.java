package com.zzy.minibo.Utils.AllParams;

import com.sina.weibo.sdk.api.MultiImageObject;

public class ParamsOfCreateStatus {

    private String access_token;

    private String status;

    private MultiImageObject pics;

    public MultiImageObject getPics() {
        return pics;
    }

    public void setPics(MultiImageObject pics) {
        this.pics = pics;
    }



    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
