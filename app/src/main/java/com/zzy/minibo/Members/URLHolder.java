package com.zzy.minibo.Members;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class URLHolder {

    private String url_short;
    private String url_long;
    private int type;//0、普通网页  1、视频  2、音乐  3、活动  5、投票
    private boolean result;

    public String getUrl_short() {
        return url_short;
    }

    public void setUrl_short(String url_short) {
        this.url_short = url_short;
    }

    public String getUrl_long() {
        return url_long;
    }

    public void setUrl_long(String url_long) {
        this.url_long = url_long;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public static URLHolder getInstanceFromJSON(String json){
        if (json != null){
            URLHolder urlHolder = new URLHolder();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("urls");
                for (int i = 0 ;i < jsonArray.length();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    urlHolder.setUrl_short(jsonObject1.getString("url_short"));
                    urlHolder.setUrl_long(jsonObject1.getString("url_long"));
                    urlHolder.setType(jsonObject1.getInt("type"));
                    urlHolder.setResult(jsonObject1.getBoolean("result"));
                }
                return urlHolder;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
