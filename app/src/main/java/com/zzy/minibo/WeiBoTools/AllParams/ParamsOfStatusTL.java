package com.zzy.minibo.WeiBoTools.AllParams;

import com.zzy.minibo.WeiBoTools.Constants;

public class ParamsOfStatusTL {
    //Appkey
    private String source = Constants.APP_KEY;
    //access_token
    private String access_token;
    //若指定参数，则返回ID比since_id大的微博，默认0
    private String since_id;
    //若指定参数，则返回ID比max_id小的微博，默认0
    private String max_id;
    //单页返回的记录条数，最多100，默认20
    private int count;
    //返回结果的页码
    private int page;
    //是否只获取当前应用的数据。0为否（返回所有数据），1为是（仅当前应用），默认0
    private int base_app;
    //返回值中user字段的开关，0：返回完整的user数据，1：只返回user_id
    private int feature;

    private ParamsOfStatusTL(Builder builder){

        this.access_token = builder.access_token;
        this.since_id = builder.since_id;
        this.max_id = builder.max_id;
        this.count = builder.count;
        this.page = builder.page;
        this.base_app = builder.base_app;
        this.feature = builder.feature;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
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

    public static class Builder{
        //默认值
        String access_token = null;
        String since_id = null;
        String max_id = null;
        int count = 20;
        int page = 1;
        int base_app = 0;
        int feature = 0;

        public Builder(){

        }

        public Builder access_token(String access_token){
            this.access_token = access_token;
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

        public ParamsOfStatusTL build(){
            return new ParamsOfStatusTL(this);
        }


    }
}
