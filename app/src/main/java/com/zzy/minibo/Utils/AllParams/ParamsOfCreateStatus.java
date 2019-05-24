package com.zzy.minibo.Utils.AllParams;

import com.sina.weibo.sdk.api.MultiImageObject;
import com.zzy.minibo.Members.ImageBean;

import java.util.List;

public class ParamsOfCreateStatus {

    private String access_token;

    private String status;

    private List<ImageBean> paths;

    public List<ImageBean> getPaths() {
        return paths;
    }

    public void setPaths(List<ImageBean> paths) {
        this.paths = paths;
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
