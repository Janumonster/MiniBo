package com.zzy.minibo.Members;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatusTimeLine {

    private static String TAG = StatusTimeLine.class.getName();

    //返回的微博内容
    private List<Status> statuses = new ArrayList<>();
    //未知
    private boolean hasvisible ;

    //该组微博的最小微博ID
    private String since_id;
    //该组微博的最大微博ID
    private String max_id;
    //微博总数
    private int total_number;

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public boolean isHasvisible() {
        return hasvisible;
    }

    public void setHasvisible(boolean hasvisible) {
        this.hasvisible = hasvisible;
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

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public static StatusTimeLine getStatusesLine(Context context,String json){
        if (null != json){
            StatusTimeLine statusesTimeLine = new StatusTimeLine();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("statuses");
                List<Status> statuses = new ArrayList<>();
                for (int i = 0;i<jsonArray.length();i++){
                    Status status = Status.getStatusFromJson(jsonArray.getString(i));
                    statuses.add(status);
                }
                statusesTimeLine.setStatuses(statuses);
                statusesTimeLine.setHasvisible(jsonObject.getBoolean("hasvisible"));
                statusesTimeLine.setTotal_number(jsonObject.getInt("total_number"));
                statusesTimeLine.setSince_id(jsonObject.getString("since_id"));
                statusesTimeLine.setMax_id(jsonObject.getString("max_id"));

//                Log.d(TAG, "getStatusesLine: "+ statusesTimeLine.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return statusesTimeLine;
        }else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "StatusesTimeLine{" +
                "statuses=" + statuses +
                ", hasvisible=" + hasvisible +
                ", since_id='" + since_id + '\'' +
                ", max_id='" + max_id + '\'' +
                ", total_number=" + total_number +
                '}';
    }
}
