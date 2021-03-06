package com.zzy.minibo.Members;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Comment {

    private String create_at;
    private long id;
    private String text;
    private User user;
    private String mid;
    private String idstr;
    private Status status;
    private Comment reply_comment;

    public static Comment getCommentsFromJson(String json){
        if (json == null)return null;
        Comment comment = new Comment();
        try {
            JSONObject jsonObject = new JSONObject(json);
            comment.user = User.makeJsonToUser(jsonObject.getString("user"));
            comment.status = Status.getStatusFromJson(jsonObject.getString("status"));
            comment.create_at = jsonObject.getString("created_at");
            comment.id = jsonObject.getLong("id");
            comment.text = jsonObject.getString("text");
            comment.mid = jsonObject.getString("mid");
            comment.idstr = jsonObject.getString("idstr");
            if (!LitePal.isExist(LP_COMMENTS.class,"idstr = ?",String.valueOf(comment.id))){
                LP_COMMENTS lp_comments = new LP_COMMENTS();
                lp_comments.setIdstr(comment.idstr);
                lp_comments.setUser_id(comment.getUser().getUID());
                lp_comments.setStatus_id(comment.getIdstr());
                lp_comments.setJson(json);
                lp_comments.save();
            }
            comment.reply_comment = Comment.getCommentsFromJson(jsonObject.getString("reply_comment"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Comment getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(Comment reply_comment) {
        this.reply_comment = reply_comment;
    }
}
