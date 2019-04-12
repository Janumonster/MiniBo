package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.StatusAdapter;
import com.zzy.minibo.Members.LP_USER;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.StatusTimeLine;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserInfo;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserTimeLine;
import com.zzy.minibo.Utils.HttpCallBack;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity {

    private static final String TAG = UserCenterActivity.class.getSimpleName();

    private static final int FEATURE_ALL = 0;
    private static final int FEATURE_CREATE_BY_ME = 1;

    private static final int GET_USER_STATUS_SUCCESS = 0;
    private static final int GET_USER_STATUS_ERROR = 1;
    private static final int GET_USER_INFO_FROM_API = 2;

    private User mUser;
    private Toolbar mToolbar;
    private List<Status> statusList = new ArrayList<>();

    private CircleImageView mUserIcon_civ;
    private TextView mUserName_tv;
    private TextView mUserLocationAndGender_tv;
    private TextView mDescription;
    private TextView mUserFollowers_tv;
    private TextView mUserFriends_tv;
    private RecyclerView mUserStatus_rv;

    private StatusAdapter statusAdapter;
    private Intent mIntent;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_USER_STATUS_SUCCESS:
                    statusAdapter.notifyDataSetChanged();
                    break;
                case GET_USER_STATUS_ERROR:
                    Toast.makeText(getBaseContext(),"刷新失败",Toast.LENGTH_SHORT).show();
                    break;
                case GET_USER_INFO_FROM_API:
                    if (mUser != null){
                        initData();
                    }else {
                        Log.d(TAG, "handleMessage: mUser is null !");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initView();
        mIntent = getIntent();
        Bundle bundle = mIntent.getExtras();
        if (bundle != null) {
            mUser = bundle.getParcelable("User");
        }
        initData();
    }

    private void initView() {
        mToolbar = findViewById(R.id.uc_toolbar);
        mUserIcon_civ = findViewById(R.id.uc_civ_user_img);
        mUserName_tv = findViewById(R.id.uc_tv_user_name);
        mUserLocationAndGender_tv = findViewById(R.id.uc_tv_user_location_gender);
        mDescription = findViewById(R.id.uc_tv_user_description);
        mUserFollowers_tv = findViewById(R.id.uc_tv_user_followers);
        mUserFriends_tv = findViewById(R.id.uc_tv_user_friends);
        mUserStatus_rv = findViewById(R.id.uc_rv_status_list);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        statusAdapter = new StatusAdapter(statusList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mUserStatus_rv.setLayoutManager(linearLayoutManager);
        mUserStatus_rv.setAdapter(statusAdapter);
    }

    private void initData(){
//        if (mUser == null){
//            List<LP_USER> lp_users = LitePal.where("screen_name = ?",mIntent.getStringExtra("user_name")).find(LP_USER.class);
//            for (LP_USER lp_user : lp_users){
//                mUser = User.makeJsonToUser(lp_user.getJson());
//            }
//        }
        if (mUser != null){
            Glide.with(this)
                    .load(mUser.getAvatar_large())
                    .placeholder(R.drawable.icon_user)
                    .into(mUserIcon_civ);
            mUserName_tv.setText(mUser.getScreen_name());
            mUserLocationAndGender_tv.setText(getUserLocationAndGenderString());
            mDescription.setText(mUser.getDescription());
            mUserFollowers_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFollowers_count())));
            mUserFriends_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFriends_count())));

            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
            ParamsOfUserTimeLine paramsOfUserTimeLine = new ParamsOfUserTimeLine.Builder()
                    .access_token(accessToken.getToken())
                    .uid(mUser.getUID())
                    .screen_name(mUser.getScreen_name())
                    .feature(FEATURE_ALL)
                    .build();
            WBApiConnector.getStatusUserTimeLine(paramsOfUserTimeLine, new HttpCallBack() {
                @Override
                public void onSuccess(String response) {
                    StatusTimeLine statusTimeLine = StatusTimeLine.getStatusesLine(getApplicationContext(),response);
                    statusList.addAll(statusTimeLine.getStatuses());
                    Message message = new Message();
                    message.what = GET_USER_STATUS_SUCCESS;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    Message message = new Message();
                    message.what = GET_USER_STATUS_ERROR;
                    handler.sendMessage(message);
                }
            });
        }

    }

    /**
     * 获取location和gender组成的字符串
     * @return
     */
    private String getUserLocationAndGenderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mUser.getLocation()).append(", 性别 ");
        switch (mUser.getGender()){
            case "m":sb.append("男");break;
            case "f":sb.append("女");break;
            case "n":sb.append("其它");break;
        }
        return sb.toString();
    }

    /**
     * 保证再点击返回键后activity被销毁
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
