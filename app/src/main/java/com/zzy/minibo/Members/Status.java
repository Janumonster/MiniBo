package com.zzy.minibo.Members;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class Status implements Parcelable{

    /**
     * -------------自己的Field---------------------------------------------------------------------
     */

    private static String TAG = Status.class.getName();

    private int type = Type.TYPE_NORMAL;

    private URLHolder statusUrl = null;

    private int isLike = 0;

    private boolean isLocal = false;

    /**
     * -------根据接口给出的Field--------------------------------------------------------------------
     */
    //创建时间 "Thu Sep 20 11:15:36 +0800 2018"
    private String create_at;
    //微博ID，long（int64）
    private long id;
    //微博ID，String型
    private String idstr;
    //微博文字内容
    private String text;
    //微博文字的长度
    private int textLength;
    //微博来源
    private String source;
    //微博是否已收藏
    private boolean favroited;
    //微博是否被截断
    private boolean truncated = false;
    //微博配图连接
    private List<String> pic_urls;
    //小图地址
    private String thumbnail_pic = "http://wx4.sinaimg.cn/thumbnail/";
    //中图地址
    private String bmiddle_pic = "http://wx4.sinaimg.cn/bmiddle/";
    //原图地址
    private String original_pic = "http://wx4.sinaimg.cn/large/";
    //地理位置信息
    private String geo;
    //微博作者
    private User user;
    //转发微博内容
    private Status retweeted_status = null;
    //转发数
    private String reposts_count;
    //评论数
    private String comments_count;
    //表态数
    private String attitudes_count;
    //是否是长微博
    private boolean isLongText;

    /**
     * ----------构造函数---------------------------------------------------------------------------
     */
    public Status(){

    }



    /**
     * 从json中获取数据，一定有的数据放前面，防止有些字段没有导致没有读取数据---------------------------
     * @param json
     * @return
     */

    public static Status getStatusFromJson(String json){
        if (null != json){
            final Status status = new Status();
            try {
                JSONObject jsonObject = new JSONObject(json);
                User user = User.makeJsonToUser(jsonObject.getString("user"));
                status.setUser(user);
                status.setCreated_at(jsonObject.getString("created_at"));
                status.setId(jsonObject.getLong("id"));
                status.setIdstr(jsonObject.getString("idstr"));
                status.setText(jsonObject.getString("text"));
                List<String> urls = new ArrayList<>();
                JSONArray jsonArray =jsonObject.getJSONArray("pic_urls");
                for (int i = 0;i<jsonArray.length();i++){
                    String str = jsonArray.getJSONObject(i).getString("thumbnail_pic");
                    str = str.substring(32);
                    urls.add(str);
                }
                status.setPic_urls(urls);
                status.setReposts_count(jsonObject.getString("reposts_count"));
                status.setComments_count(jsonObject.getString("comments_count"));
                status.setAttitudes_count(jsonObject.getString("attitudes_count"));
                if (!LitePal.isExist(LP_STATUS.class,"idstr = "+status.idstr)){
                    LP_STATUS LPSTATUS = new LP_STATUS();
                    LPSTATUS.setIdstr(status.idstr);
                    LPSTATUS.setJson(json);
                    LPSTATUS.save();
                }
                status.setRetweeted_status(Status.getStatusFromJson(jsonObject.getString("retweeted_status")));
                status.setGeo(jsonObject.getString("geo"));
                status.setSource(jsonObject.getString("source"));
                status.setLongText(jsonObject.getBoolean("isLongText"));
                status.setFavroited(jsonObject.getBoolean("favorited"));
                status.setTextLength(jsonObject.getInt("textLength"));
                //判断是否是图片原创类型
                if (urls.size() > 0){
                    status.setType(Type.TYPE_IMAGE);
                    if (urls.size() >=1){
                        status.setType(Type.TYPE_IMAGES);
                    }
                }
                //判断是否为转发
                if (status.getRetweeted_status() != null){
                    status.setType(Type.TYPE_REPOST);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }else {
            return null;
        }
    }



    /**
     * ---------------序列化------------------------------------------------------------------------
     */

    protected Status(Parcel in) {
        type = in.readInt();
        isLike = in.readInt();
        create_at = in.readString();
        id = in.readLong();
        idstr = in.readString();
        text = in.readString();
        textLength = in.readInt();
        source = in.readString();
        favroited = in.readByte() != 0;
        truncated = in.readByte() != 0;
        pic_urls = in.createStringArrayList();
        thumbnail_pic = in.readString();
        bmiddle_pic = in.readString();
        original_pic = in.readString();
        geo = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        retweeted_status = in.readParcelable(Status.class.getClassLoader());
        reposts_count = in.readString();
        comments_count = in.readString();
        attitudes_count = in.readString();
        isLongText = in.readByte() != 0;
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(isLike);
        dest.writeString(create_at);
        dest.writeLong(id);
        dest.writeString(idstr);
        dest.writeString(text);
        dest.writeInt(textLength);
        dest.writeString(source);
        dest.writeByte((byte) (favroited ? 1 : 0));
        dest.writeByte((byte) (truncated ? 1 : 0));
        dest.writeStringList(pic_urls);
        dest.writeString(thumbnail_pic);
        dest.writeString(bmiddle_pic);
        dest.writeString(original_pic);
        dest.writeString(geo);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(retweeted_status, flags);
        dest.writeString(reposts_count);
        dest.writeString(comments_count);
        dest.writeString(attitudes_count);
        dest.writeByte((byte) (isLongText ? 1 : 0));
    }

    /**
     * -------------------------用于描述微博的类型---------------------------------------------------
     */
    public static class Type{
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_IMAGE = 1;
        public static final int TYPE_IMAGES = 2;
        public static final int TYPE_VIDEO = 3;
        public static final int TYPE_REPOST = 4;
    }

    /**
     * --------------------------getter 和 setter---------------------------------------------------
     */

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }


    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int like) {
        isLike = like;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public URLHolder getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(URLHolder statusUrl) {
        this.statusUrl = statusUrl;
    }

    public String getCreated_at() {
        return create_at;
    }

    public void setCreated_at(String create_at) {
        this.create_at = create_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFavroited() {
        return favroited;
    }

    public void setFavroited(boolean favroited) {
        this.favroited = favroited;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public List<String> getPic_urls() {
        return pic_urls;
    }

    public void setPic_urls(List<String> pic_urls) {
        this.pic_urls = pic_urls;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Status retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public String getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(String reposts_count) {
        this.reposts_count = reposts_count;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(String attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public boolean isLongText() {
        return isLongText;
    }

    public void setLongText(boolean longText) {
        isLongText = longText;
    }
}
