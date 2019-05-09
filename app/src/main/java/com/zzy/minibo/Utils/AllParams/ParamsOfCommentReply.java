package com.zzy.minibo.Utils.AllParams;

public class ParamsOfCommentReply {

    private String access_token;
    private String cid;
    private String id;
    private String comment;
    private int without_mentions;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getWithout_mentions() {
        return without_mentions;
    }

    public void setWithout_mentions(int without_mentions) {
        this.without_mentions = without_mentions;
    }
}
