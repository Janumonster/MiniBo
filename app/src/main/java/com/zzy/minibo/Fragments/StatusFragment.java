package com.zzy.minibo.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Activities.PicturesActivity;
import com.zzy.minibo.Activities.StatusActivity;
import com.zzy.minibo.Activities.StatusEditActivity;
import com.zzy.minibo.Adapter.StatusAdapter;
import com.zzy.minibo.Members.LP_STATUS;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.StatusTimeLine;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfStatusTL;
import com.zzy.minibo.WBListener.HttpCallBack;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.PictureTapCallback;
import com.zzy.minibo.WBListener.RepostStatusCallback;
import com.zzy.minibo.WBListener.StatusTapCallback;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;

public class StatusFragment extends Fragment {

    private static String TAG = StatusFragment.class.getSimpleName();

    private static final int MESSAGE_FROM_REFRESH = 0;
    private static final int MESSAGE_FROM_INITIAL = 1;
    private static final int MESSAGE_FROM_GET_MORE = 2;
    private static final int MESSAGE_FROM_ERROR = 3;
    public static final int RECYCLERVIEW_CACHE_SIZE = 100;
    public static final int REPOST_STATUS_CODE = 1001;
    public static final int CREATE_STATUS_CODE = 1002;

    private List<Status> statusList = new ArrayList<>();
    private List<Status> statusListCache = new ArrayList<>();
    private StatusAdapter statusAdapter;
    private StatusTimeLine statusTimeLine = new StatusTimeLine();
    private Oauth2AccessToken accessToken;

    private RecyclerView mStatus_rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout progress_layout;
    private Toolbar mToolbar;

    private FloatingActionButton floatingMenu;

    private boolean isBottom = false;
    private boolean isGettingMore = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_FROM_INITIAL:
                    statusAdapter = new StatusAdapter(statusList,getContext());
                    statusAdapter.setPictureTapCallback(new PictureTapCallback() {
                        @Override
                        public void callback(int statusPosition,int postion, int from,boolean isLocal) {
                            Intent intent = new Intent(getContext(), PicturesActivity.class);
                            intent.putExtra("currentPosition",postion);
                            intent.putExtra("isLocal",isLocal);
                            if (from == 0){ // 0，外部微博，1为转发微博，这里进行判断
                                intent.putStringArrayListExtra("urls", (ArrayList<String>) statusList.get(statusPosition).getPic_urls());
                            } else {
                                intent.putStringArrayListExtra("urls", (ArrayList<String>) statusList.get(statusPosition).getRetweeted_status().getPic_urls());
                            }
                            startActivity(intent);
                        }
                    });
                    statusAdapter.setRepostStatusCallback(new RepostStatusCallback() {
                        @Override
                        public void callback(int position) {
                            Intent intent = new Intent(getContext(),StatusEditActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("Status",statusList.get(position));
                            intent.putExtras(bundle);
                            startActivityForResult(intent,REPOST_STATUS_CODE);
                        }
                    });
                    statusAdapter.setStatusTapCallback(new StatusTapCallback() {
                        @Override
                        public void callback(int position, boolean isRepost) {
                            Intent intent = new Intent(getContext(), StatusActivity.class);
                            Bundle bundle = new Bundle();
                            if (isRepost){
                                bundle.putParcelable("status",statusList.get(position).getRetweeted_status());
                            }else {
                                bundle.putParcelable("status",statusList.get(position));
                            }
                            intent.putExtras(bundle);
                            startActivityForResult(intent,REPOST_STATUS_CODE);
                        }
                    });
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    mStatus_rv.setLayoutManager(linearLayoutManager);
                    mStatus_rv.setAdapter(statusAdapter);
                    break;
                case MESSAGE_FROM_REFRESH:
                    swipeRefreshLayout.setRefreshing(false);
                    if (statusListCache.size() == 0){
                        Toast relresh_none_toast = Toast.makeText(getContext(),null,Toast.LENGTH_SHORT);
                        relresh_none_toast.setText("已经最新了！");
                        relresh_none_toast.show();
                    }else {
                        statusList.addAll(0,statusListCache);
                        statusAdapter.notifyDataSetChanged();
                        Toast refresh_toast = Toast.makeText(getContext(),null,Toast.LENGTH_SHORT);
                        refresh_toast.setText("更新了"+statusListCache.size()+"条微博");
                        refresh_toast.show();
                    }
                    break;
                case MESSAGE_FROM_GET_MORE:
                    isGettingMore = false;
                    progress_layout.setVisibility(View.GONE);
                    statusList.addAll(statusListCache);
                    statusAdapter.notifyDataSetChanged();
                    Toast get_more_toast = Toast.makeText(getContext(),null,Toast.LENGTH_SHORT);
                    get_more_toast.setText("微博 +"+statusListCache.size());
                    get_more_toast.show();
                    break;
                case MESSAGE_FROM_ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    progress_layout.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"失败了",Toast.LENGTH_SHORT).show();
                    break;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        initView(view);
        accessToken = AccessTokenKeeper.readAccessToken(getContext());
        getInitialStatus();
        mStatus_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && statusAdapter.isNearBottom()){
                    isBottom = true;
                    //加载更多
                    getMoreStatus();
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

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.fg_status_toolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatus_rv.smoothScrollToPosition(0);
            }
        });
        mStatus_rv = view.findViewById(R.id.fg_status_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.fg_status_refreshlayout);
        progress_layout = view.findViewById(R.id.fg_status_progressbar);
        floatingMenu = view.findViewById(R.id.fg_status_fab);
        floatingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),StatusEditActivity.class);
                startActivityForResult(intent,CREATE_STATUS_CODE);
            }
        });
    }

    /**
     * 加载更多微博
     */
    private void getMoreStatus() {
        if (!isGettingMore){
            isGettingMore = true;
            progress_layout.setVisibility(View.VISIBLE);
            if (accessToken != null){
                ParamsOfStatusTL params = new ParamsOfStatusTL.Builder()
                        .access_token(accessToken.getToken())
                        .max_id(statusTimeLine.getMax_id())
                        .build();
                WBApiConnector.getStatusesHomeTimeline(params, new HttpCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        StatusTimeLine sl = StatusTimeLine.getStatusesLine(getContext(),response);
                        statusTimeLine.setMax_id(sl.getMax_id());
                        statusListCache.clear();
                        if (sl.getStatuses() == null ||sl.getStatuses().size() == 0){
                            List<LP_STATUS> LPSTATUSES = LitePal.offset(statusList.size()).limit(20).order("idstr desc").find(LP_STATUS.class);
                            for (LP_STATUS l : LPSTATUSES){
                                statusListCache.add(Status.getStatusFromJson(l.getJson()));
                            }
                        }else {
                            statusListCache.addAll(sl.getStatuses());
                        }
                        Message message = new Message();
                        message.what = MESSAGE_FROM_GET_MORE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        Message message = new Message();
                        message.what = MESSAGE_FROM_ERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        }

    }

    /**
     * 初始化微博列表，第一次请求
     */
    private void getInitialStatus() {

        if (statusList.size() == 0){
            List<LP_STATUS> LPSTATUSES = LitePal.findAll(LP_STATUS.class);
            int length = LPSTATUSES.size();
            for (int i = 0;i < length; i++){
                statusListCache.add(Status.getStatusFromJson(LPSTATUSES.get(length-i-1).getJson()));
                if (statusListCache.size() >= 20){
                    break;
                }
            }
            statusList.addAll(statusListCache);
        }
        Message message = new Message();
        message.what = MESSAGE_FROM_INITIAL;
        handler.sendMessage(message);


//        if (accessToken != null){
//            ParamsOfStatusTL paramsOfStatusTL = new ParamsOfStatusTL.Builder()
//                    .access_token(accessToken.getToken())
//                    .build();
//            WBApiConnector.getStatusesHomeTimeline(paramsOfStatusTL, new HttpCallBack() {
//                @Override
//                public void onSuccess(String response) {
//                    statusTimeLine = StatusTimeLine.getStatusesLine(getContext(),response);
//                    statusList = statusTimeLine.getStatuses();
//                    statusListCache.clear();
//                    if (statusList.size() == 0){
//                        List<LP_STATUS> LPSTATUSES = LitePal.limit(20).order("idstr desc").find(LP_STATUS.class);
//                        for (LP_STATUS l : LPSTATUSES){
//                            statusListCache.add(Status.getStatusFromJson(l.getJson()));
//                        }
//                        statusList.addAll(statusListCache);
//                    }
//                    Message message = new Message();
//                    message.what = MESSAGE_FROM_INITIAL;
//                    handler.sendMessage(message);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    Message message = new Message();
//                    message.what = MESSAGE_FROM_ERROR;
//                    handler.sendMessage(message);
//                }
//            });
//        }
    }


    /**
     * 刷新微博
     */
    private void initRefreshLayout() {
        swipeRefreshLayout.setProgressViewOffset(true,0,120);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorMain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新过程
                final ParamsOfStatusTL paramsOfStatusTL = new ParamsOfStatusTL.Builder()
                        .access_token(accessToken.getToken())
                        .since_id(statusTimeLine.getSince_id())
                        .build();
                WBApiConnector.getStatusesHomeTimeline(paramsOfStatusTL, new HttpCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        statusTimeLine = StatusTimeLine.getStatusesLine(getContext(),response);
                        statusListCache.clear();
                        statusListCache.addAll(statusTimeLine.getStatuses());
                        Message message = new Message();
                        message.what = MESSAGE_FROM_REFRESH;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        Message message = new Message();
                        message.what = MESSAGE_FROM_ERROR;
                        handler.sendMessage(message);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null){
            switch (requestCode){
                case REPOST_STATUS_CODE:
                    if (resultCode == RESULT_OK){
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            Status status = bundle.getParcelable("Status");
                            statusList.add(0,status);
                            statusAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case CREATE_STATUS_CODE:
                    if (resultCode == RESULT_OK){
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            Status status = bundle.getParcelable("Status");
                            statusList.add(0,status);
                            statusAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
