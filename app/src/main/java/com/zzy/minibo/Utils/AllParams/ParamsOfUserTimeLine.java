package com.zzy.minibo.Utils.AllParams;

import com.zzy.minibo.Utils.Constants;

public class ParamsOfUserTimeLine {

    private String source = Constants.APP_KEY;

    private String access_token;
    private String uid;
    private String screen_name;
    private String since_id;
    private String max_id;
    private int count;
    private int page;
    private int base_app;//是否获取当前应用的数据 0否 1是
    private int feature;//0、全部  1、原创 2、图片  3、视频  4、音乐
    private int trim_user;//返回字段中user字段的开关，0开，1关

    private ParamsOfUserTimeLine(Builder builder){
        this.access_token = builder.access_token;
        this.uid = builder.uid;
        this.since_id = builder.since_id;
        this.max_id = builder.max_id;
        this.count = builder.count;
        this.page = builder.page;
        this.base_app = builder.base_app;
        this.feature = builder.feature;
        this.trim_user = builder.trim_user;
    }
    public String getSource() {
        return source;
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

    public String getSince_id() {
        return since_id;
    }

    public void setSince_id(String since_id) {
        this.since_id = since_id;
    }

    public String getMax_id() {
        return max_id;
    }

    public void setMax_id(String max_id) {
        this.max_id = max_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getBase_app() {
        return base_app;
    }

    public void setBase_app(int base_app) {
        this.base_app = base_app;
    }

    public int getFeature() {
        return feature;
    }

    public void setFeature(int feature) {
        this.feature = feature;
    }

    public int getTrim_user() {
        return trim_user;
    }

    public void setTrim_user(int trim_user) {
        this.trim_user = trim_user;
    }

    public static class Builder{
        //默认值
        String access_token = null;
        String uid = null;
        String screen_name = null;
        String since_id = null;
        String max_id = null;
        int count = 20;
        int page = 1;
        int base_app = 0;
        int feature = 0;
        int trim_user = 0;

        public Builder(){

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

        public Builder since_id(String since_id){
            this.since_id = since_id;
            return this;
        }

        public Builder max_id(String max_id){
            this.max_id = max_id;
            return this;
        }

        public Builder count(int count){
            this.count = count;
            return this;
        }

        public Builder page(int page){
            this.page = page;
            return this;
        }

        public Builder base_app(int base_app){
            this.base_app = base_app;
            return this;
        }

        public Builder feature(int feature){
            this.feature = feature;
            return this;
        }

        public Builder trim_user(int trim_user){
            this.trim_user = trim_user;
            return this;
        }

        public ParamsOfUserTimeLine build(){
            return new ParamsOfUserTimeLine(this);
        }

    }
}
