package com.zzy.minibo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Utils.AllParams.ParamsOfComments;
import com.zzy.minibo.Utils.AllParams.ParamsOfStatusTL;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserInfo;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserTimeLine;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class WBApiConnector {

    private static String TAG = "WBApiConnector";

    //获取用户信息的地址
    private static final String USER_INFO = "https://api.weibo.com/2/users/show.json";
    //获取用户及其所关注的人的微博，默认20条
    private static final String STATUSES_HOME_TIMELINE = "https://api.weibo.com/2/statuses/home_timeline.json";

    private static final String EMOTIONS = "https://api.weibo.com/2/emotions.json";

    private static final String STATUS_USER_TIMELINE = "https://api.weibo.com/2/statuses/user_timeline.json";

    private static final String SHORT_URL_EXPEND = "https://api.weibo.com/2/short_url/expand.json";

    private static final String STATUS_COMMENTS = "https://api.weibo.com/2/comments/show.json";


    /**
     * 构建get的含参地址
     * @param baseURL 最初的请求地址
     * @param parms 请求所需的参数
     * @return 含参地址
     */
    private static String buildURLWithParams(String baseURL, Map<String,String> parms){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseURL);
        stringBuilder.append("?");
        Set<String> keys = parms.keySet();
        int count = 0;
        for (String key:keys){
            if (parms.get(key) == null){
                continue;
            }
            stringBuilder.append(key).append("=").append(parms.get(key));
            count++;
            if (count < parms.size()){
                stringBuilder.append("&");
            }
        }
        Log.d(TAG, "buildURLWithParams: "+stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 请求用户信息
     * @param callBack 回调
     */
    public static void getUserInfo(ParamsOfUserInfo paramsOfUserInfo, final HttpCallBack callBack){
        final Map<String,String> parms = new ArrayMap<>();
        parms.put("access_token",paramsOfUserInfo.getAccess_token());
        if (paramsOfUserInfo.getUid() != null){
            parms.put("uid",paramsOfUserInfo.getUid());
        }else {
            parms.put("screen_name",paramsOfUserInfo.getScreen_name());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(USER_INFO,parms))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String str = response.body().string();
                        Log.d(TAG, "run: "+str);
                        callBack.onSuccess(str);
                    }
                } catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取最新用户及用户所关注用户的信息
     * @param params 请求参数
     * @param callBack 回调
     */
    public static void getStatusesHomeTimeline(ParamsOfStatusTL params, final HttpCallBack callBack){
        final Map<String,String> map = new ArrayMap<>();
        map.put("source",params.getSource());
        map.put("access_token",params.getAccess_token());
        map.put("since_id",String.valueOf(params.getSince_id()));
        map.put("max_id",String.valueOf(params.getMax_id()));
        map.put("count",String.valueOf(params.getCount()));
        map.put("page",String.valueOf(params.getPage()));
        map.put("base_app",String.valueOf(params.getBase_app()));
        map.put("feature",String.valueOf(params.getFeature()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(STATUSES_HOME_TIMELINE,map))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null){
                        String str = response.body().string();
//                        Log.d(TAG, "run: "+str);
                        callBack.onSuccess(str);
                    }
                } catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取指定用户的微博时间线
     * @param params 参数
     * @param callBack 回调监听
     */
    public static void getStatusUserTimeLine(ParamsOfUserTimeLine params, final HttpCallBack callBack){
        final Map<String,String> map = new ArrayMap<>();
        map.put("source",params.getSource());
        map.put("access_token",params.getAccess_token());
        map.put("uid",params.getUid());
        map.put("since_id",String.valueOf(params.getSince_id()));
        map.put("max_id",String.valueOf(params.getMax_id()));
        map.put("count",String.valueOf(params.getCount()));
        map.put("page",String.valueOf(params.getPage()));
        map.put("base_app",String.valueOf(params.getBase_app()));
        map.put("feature",String.valueOf(params.getFeature()));
        map.put("trim_user",String.valueOf(params.getTrim_user()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(STATUS_USER_TIMELINE,map))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null){
                        String str = response.body().string();
                        callBack.onSuccess(str);
                    }
                } catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public static void getStatusComments(ParamsOfComments params,final HttpCallBack callBack){
        final Map<String,String> map = new ArrayMap<>();
        map.put("source",params.getSource());
        map.put("access_token",params.getAccess_token());
        map.put("id",params.getStatusId());
        map.put("since_id",String.valueOf(params.getSince_id()));
        map.put("max_id",String.valueOf(params.getMax_id()));
        map.put("count",String.valueOf(params.getCount()));
        map.put("page",String.valueOf(params.getPage()));
        map.put("fliter_by_author",String.valueOf(params.getFliter_by_author()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(STATUS_COMMENTS,map))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null){
                        assert response.body() != null;
                        String str = response.body().string();
                        callBack.onSuccess(str);
                    }

                }catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void getShortUrlType(Oauth2AccessToken accessToken, String url, final HttpCallBack callBack){
        final Map<String,String> map = new ArrayMap<>();
        map.put("source",Constants.APP_KEY);
        map.put("access_token",accessToken.getToken());
        map.put("url_short",url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(SHORT_URL_EXPEND,map))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        callBack.onSuccess(response.body().string());
                    }
                } catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getWBEmotions(String access_token, final HttpCallBack callBack){
        final Map<String,String> map = new ArrayMap<>();
        map.put("source",Constants.APP_KEY);
        map.put("access_token",access_token);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .get()
                        .url(buildURLWithParams(EMOTIONS,map))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        callBack.onSuccess(response.body().string());
                    }
                } catch (IOException e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
