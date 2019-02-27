package com.zzy.minibo.Members;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable{

    private static String TAG = User.class.getName();

    private String UID = null;//用户UID
    private long id = 0;//用户UID，int型
    private String screen_name = null;//用户昵称
    private String name = null;//用户昵称

    private String description;//用户简介
    private String domain = null;//用户的个性域名
    private String weihao = null;//用户的微号

    private boolean verified = false;//是否是微博认证用户
    private String verified_reason  = null;//认证原因

    private String city = null;//用户所在城市
    private String province = null;//用户所在省份
    private String location = null;//用户所在地址

    private String profile_image_url = null;//用户头像地址（中）50*50
    private String avatar_large = null;//用户头像地址（大）180*180
    private String avatar_hd = null;//用户头像地址（高清/原图）

    private String gender = null;//m：男，f：女,n:未知

    private int followers_count  = 0;//粉丝数
    private int friends_count = 0;//关注数
    private int statuses_count = 0; //微博数

    private String created_at = null;//注册时间

    private boolean follow_me = false;//该用户是否关注当前账户

    private String lastStatus = null;

    //用户权限相关
    private boolean allow_all_act_msg = false;//是否允许别人给我发私信
    private boolean allow_all_comment = false;//是否允许别人评论
    private boolean geo_enabled = false;//是否允许标识用户地理位置


    /**********************************************************************************************/
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerified_reason() {
        return verified_reason;
    }

    public void setVerified_reason(String verified_reason) {
        this.verified_reason = verified_reason;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getAvatar_large() {
        return avatar_large;
    }

    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }

    public String getAvatar_hd() {
        return avatar_hd;
    }

    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isFollow_me() {
        return follow_me;
    }

    public void setFollow_me(boolean follow_me) {
        this.follow_me = follow_me;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public boolean isAllow_all_act_msg() {
        return allow_all_act_msg;
    }

    public void setAllow_all_act_msg(boolean allow_all_act_msg) {
        this.allow_all_act_msg = allow_all_act_msg;
    }

    public boolean isAllow_all_comment() {
        return allow_all_comment;
    }

    public void setAllow_all_comment(boolean allow_all_comment) {
        this.allow_all_comment = allow_all_comment;
    }

    public boolean isGeo_enabled() {
        return geo_enabled;
    }

    public void setGeo_enabled(boolean geo_enabled) {
        this.geo_enabled = geo_enabled;
    }

    /**********************************************************************************************/



    public User(){

    }

    private User(Parcel in) {
        UID = in.readString();
        id = in.readLong();
        screen_name = in.readString();
        name = in.readString();
        description = in.readString();
        domain = in.readString();
        weihao = in.readString();
        verified = in.readByte() != 0;
        verified_reason = in.readString();
        city = in.readString();
        province = in.readString();
        location = in.readString();
        profile_image_url = in.readString();
        avatar_large = in.readString();
        avatar_hd = in.readString();
        gender = in.readString();
        followers_count = in.readInt();
        friends_count = in.readInt();
        statuses_count = in.readInt();
        created_at = in.readString();
        follow_me = in.readByte() != 0;
        lastStatus = in.readString();
        allow_all_act_msg = in.readByte() != 0;
        allow_all_comment = in.readByte() != 0;
        geo_enabled = in.readByte() != 0;

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public String toString() {
        return "User{" + '\n' +
                "UID = " + UID + '\n' +
                "id = " + id + '\n' +
                "screen_name = " + screen_name + '\n' +
                "name = " + name + '\n' +
                "description = " + description + '\n'+
                "domain = " + domain + '\n'+
                "weihao = " + weihao + '\n'+
                "verified = " + verified + '\n' +
                "verified_reason = " + verified_reason + '\n' +
                "city = " + city + '\n' +
                "province = " + province + '\n' +
                "location = " + location + '\n' +
                "profile_image_url = " + profile_image_url + '\n' +
                "avatar_large = " + avatar_large + '\n' +
                "avatar_hd = " + avatar_hd + '\n' +
                "gender = " + gender + '\n' +
                "followers_count = " + followers_count + '\n' +
                "friends_count = " + friends_count + '\n' +
                "statuses_count = " + statuses_count + '\n' +
                "created_at = " + created_at + '\n' +
                "follow_me = " + follow_me +'\n'+
                "lastStatus" + lastStatus + '\n'+
                "allow_all_act_msg = " + allow_all_act_msg +'\n'+
                "allow_all_comment = " + allow_all_comment +
                "geo_enabled = " + geo_enabled + '\n'+
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UID);
        parcel.writeLong(id);
        parcel.writeString(screen_name);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(domain);
        parcel.writeString(weihao);
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeString(verified_reason);
        parcel.writeString(city);
        parcel.writeString(province);
        parcel.writeString(location);
        parcel.writeString(profile_image_url);
        parcel.writeString(avatar_large);
        parcel.writeString(avatar_hd);
        parcel.writeString(gender);
        parcel.writeInt(followers_count);
        parcel.writeInt(friends_count);
        parcel.writeInt(statuses_count);
        parcel.writeString(created_at);
        parcel.writeByte((byte) (follow_me ? 1 : 0));
        parcel.writeString(lastStatus);
        parcel.writeByte((byte) (allow_all_act_msg ? 1 : 0));
        parcel.writeByte((byte) (allow_all_comment ? 1 : 0));
        parcel.writeByte((byte) (geo_enabled ? 1 :0));
    }


    public static User makeJsonToUser(String json){
        if (null != json) {
            User mUser = new User();
            try {
                JSONObject jsonObject = new JSONObject(json);
                mUser = makeJsonObjectToUser(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mUser;
        }else {
            return null;
        }
    }


    public static User makeJsonObjectToUser(JSONObject jsonObject){
        if (null != jsonObject) {
            User mUser = new User();
            try {
                mUser.setUID(jsonObject.getString("idstr"));
                mUser.setId(jsonObject.getLong("id"));
                mUser.setDescription(jsonObject.getString("description"));
                mUser.setScreen_name(jsonObject.getString("screen_name"));
                mUser.setName(jsonObject.getString("name"));
                mUser.setProvince(jsonObject.getString("province"));
                mUser.setDomain(jsonObject.getString("domain"));
                mUser.setWeihao(jsonObject.getString("weihao"));
                mUser.setCity(jsonObject.getString("city"));
                mUser.setLocation(jsonObject.getString("location"));
                mUser.setVerified(jsonObject.getBoolean("verified"));
                mUser.setVerified_reason(jsonObject.getString("verified_reason"));
                mUser.setProfile_image_url(jsonObject.getString("profile_image_url"));
                mUser.setAvatar_large(jsonObject.getString("avatar_large"));
                mUser.setAvatar_hd(jsonObject.getString("avatar_hd"));
                mUser.setGender(jsonObject.getString("gender"));
                mUser.setFollowers_count(jsonObject.getInt("followers_count"));
                mUser.setFriends_count(jsonObject.getInt("friends_count"));
                mUser.setStatuses_count(jsonObject.getInt("statuses_count"));
                mUser.setCreated_at(jsonObject.getString("created_at"));
                mUser.setFollow_me(jsonObject.getBoolean("follow_me"));
//                mUser.setLastStatus(jsonObject.getString("status"));
                mUser.setAllow_all_act_msg(jsonObject.getBoolean("allow_all_act_msg"));
                mUser.setAllow_all_comment(jsonObject.getBoolean("allow_all_comment"));
                mUser.setGeo_enabled(jsonObject.getBoolean("geo_enabled"));
                Log.d(TAG, "getUserInfoFromJSON: " + mUser.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mUser;
        }else {
            return null;
        }
    }
}
