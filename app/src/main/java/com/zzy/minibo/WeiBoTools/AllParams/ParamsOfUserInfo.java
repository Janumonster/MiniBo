package com.zzy.minibo.WeiBoTools.AllParams;

public class ParamsOfUserInfo {

    private String access_token;

    private String uid;

    private String screen_name;

    private ParamsOfUserInfo(Builder builder) {
        this.access_token = builder.access_token;
        this.uid = builder.uid;
        this.screen_name = builder.screen_name;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public static class Builder{

        String access_token = null;

        String uid = null;

        String screen_name = null;

        public Builder() {

        }

        public Builder access_token(String access_token){
            this.access_token = access_token;
            return this;
        }

        public Builder uid(String uid){
            this.uid = uid;
            return this;
        }

        public Builder screen_name(String screen_name){
            this.screen_name = screen_name;
            return this;
        }

        public ParamsOfUserInfo build(){return new ParamsOfUserInfo(this);}
    }
}
