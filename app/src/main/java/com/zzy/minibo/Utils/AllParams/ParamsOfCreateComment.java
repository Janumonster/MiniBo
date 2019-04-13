package com.zzy.minibo.Utils.AllParams;

import com.zzy.minibo.Utils.Constants;

public class ParamsOfCreateComment {

    //Appkey
    private String source = Constants.APP_KEY;
    //access_token
    private String access_token;
    private String comment;
    private String idstr;
    private int comment_ori;

    public ParamsOfCreateComment(Builder builder) {
        this.access_token = builder.access_token;
        this.comment = builder.comment;
        this.idstr = builder.idstr;
        this.comment_ori = builder.comment_ori;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public int getComment_ori() {
        return comment_ori;
    }

    public void setComment_ori(int comment_ori) {
        this.comment_ori = comment_ori;
    }

    public static class Builder{
        //access_token
        String access_token = null;
        String comment = null;
        String idstr = null;
        int comment_ori = 0;

        public Builder access_token(String access_token){
            this.access_token = access_token;
            return this;
        }

        public Builder comment(String comment){
            this.comment = comment;
            return this;
        }

        public Builder idstr(String idstr){
            this.idstr = idstr;
            return this;
        }

        public Builder comment_ori(int comment_ori){
            this.comment_ori = comment_ori;
            return this;
        }

        public ParamsOfCreateComment build(){
            return new ParamsOfCreateComment(this);
        }
    }
}
