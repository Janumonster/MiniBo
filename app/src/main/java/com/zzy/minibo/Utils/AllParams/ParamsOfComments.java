package com.zzy.minibo.Utils.AllParams;

import com.zzy.minibo.Utils.Constants;

public class ParamsOfComments {

    //Appkey
    private String source = Constants.APP_KEY;
    //access_token
    private String access_token;
    private String statusId;
    private String since_id;
    private String max_id;
    private int count;
    private int page;
    private int fliter_by_author;

    public ParamsOfComments(Builder builder) {
        this.access_token = builder.access_token;
        this.statusId = builder.statusId;
        this.since_id = builder.since_id;
        this.max_id = builder.max_id;
        this.count = builder.count;
        this.page = builder.page;
        this.fliter_by_author = builder.fliter_by_author;
    }


    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
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

    public int getFliter_by_author() {
        return fliter_by_author;
    }

    public void setFliter_by_author(int fliter_by_author) {
        this.fliter_by_author = fliter_by_author;
    }

    public static class Builder{

        String access_token = null;
        String statusId = null;
        String since_id = null;
        String max_id = null;
        int count = 50;
        int page = 1;
        int fliter_by_author = 0;

        public Builder(){

        }

        public Builder access_token(String access_token){
            this.access_token = access_token;
            return this;
        }

        public Builder statusId(String id){
            this.statusId = id;
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

        public Builder fliter_by_author(int fliter_by_author){
            this.fliter_by_author = fliter_by_author;
            return this;
        }

        public ParamsOfComments build(){
            return new ParamsOfComments(this);
        }
    }
}
