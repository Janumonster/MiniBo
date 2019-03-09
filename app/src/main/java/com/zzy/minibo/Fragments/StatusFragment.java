package com.zzy.minibo.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.StatusAdapter;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.StatusTimeLine;
import com.zzy.minibo.R;
import com.zzy.minibo.WeiBoTools.AllParams.ParamsOfStatusTL;
import com.zzy.minibo.WeiBoTools.HttpCallBack;
import com.zzy.minibo.WeiBoTools.WBApi;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {

    private static String TAG = StatusFragment.class.getSimpleName();

    private List<Status> statusList = new ArrayList<>();
    private StatusAdapter statusAdapter;
    private StatusTimeLine statusTimeLine = new StatusTimeLine();
    private Oauth2AccessToken accessToken;

    private RecyclerView mStatus_rv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isBottom = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    statusAdapter = new StatusAdapter(statusList,getContext());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mStatus_rv.setLayoutManager(linearLayoutManager);
                    mStatus_rv.setAdapter(statusAdapter);
                    break;
                case 1:
                    statusAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case 2:
                    Toast.makeText(getContext(),"刷新失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        mStatus_rv = view.findViewById(R.id.fg_status_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.fg_status_refreshlayout);
        accessToken = AccessTokenKeeper.readAccessToken(getContext());
        getInitialStatus();
        mStatus_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && statusAdapter.isNearBottom()){
                    isBottom = true;
                    //刷新
                    getMoreStatus();
                    statusAdapter.notifyDataSetChanged();
                }else {
                    isBottom = false;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        initRefreshLayout();
        return view;
    }

    private void getMoreStatus() {
        if (accessToken != null){
            ParamsOfStatusTL params = new ParamsOfStatusTL.Builder()
                    .access_token(accessToken.getToken())
                    .max_id(statusTimeLine.getMax_id())
                    .build();
            WBApi.getStatusesHomeTimeline(params, new HttpCallBack() {
                @Override
                public void onFinish(String response) {
                    StatusTimeLine timeLine = StatusTimeLine.getStatusesLine(response);
                    statusTimeLine.setMax_id(timeLine.getMax_id());
                    statusList.addAll(timeLine.getStatuses());
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

    private void getInitialStatus() {
        if (accessToken != null){
            ParamsOfStatusTL paramsOfStatusTL = new ParamsOfStatusTL.Builder()
                    .access_token(accessToken.getToken())
                    .build();
            WBApi.getStatusesHomeTimeline(paramsOfStatusTL, new HttpCallBack() {
                @Override
                public void onFinish(String response) {
                    statusTimeLine = StatusTimeLine.getStatusesLine(response);
                    statusList = statusTimeLine.getStatuses();
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

    private void initRefreshLayout() {
        swipeRefreshLayout.setProgressViewOffset(true,0,120);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorMain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新过程
                ParamsOfStatusTL paramsOfStatusTL = new ParamsOfStatusTL.Builder()
                        .access_token(accessToken.getToken())
                        .since_id(statusTimeLine.getSince_id())
                        .build();
                WBApi.getStatusesHomeTimeline(paramsOfStatusTL, new HttpCallBack() {
                    @Override
                    public void onFinish(String response) {
                        statusTimeLine = StatusTimeLine.getStatusesLine(response);
                        statusList.clear();
                        statusList.addAll(statusTimeLine.getStatuses());
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                });
            }
        });
    }

}
