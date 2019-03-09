package com.zzy.minibo.WeiBoTools;

import android.util.ArrayMap;
import android.util.Log;

import com.zzy.minibo.WeiBoTools.AllParams.ParamsOfStatusTL;
import com.zzy.minibo.WeiBoTools.AllParams.ParamsOfUserInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class WBApi {

    private static String TAG = "WBApi";

    //获取用户信息的地址
    private static final String USER_INFO = "https://api.weibo.com/2/users/show.json";
    //获取用户及其所关注的人的微博，默认20条
    private static final String STATUSES_HOME_TIMELINE = "https://api.weibo.com/2/statuses/home_timeline.json";

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
                        callBack.onFinish(str);
                    }
                } catch (IOException e) {
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
        map.put("feature",String.valueOf(params.getFeture()));
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
                        callBack.onFinish(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
