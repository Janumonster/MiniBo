package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.StatusAdapter;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.StatusTimeLine;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.WeiBoTools.AllParams.ParamsOfUserTimeLine;
import com.zzy.minibo.WeiBoTools.HttpCallBack;
import com.zzy.minibo.WeiBoTools.WBApi;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity {

    private static final int FEATURE_ALL = 0;
    private static final int FEATURE_CREATE_BY_ME = 1;

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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    StatusAdapter statusAdapter = new StatusAdapter(statusList,getBaseContext());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mUserStatus_rv.setLayoutManager(linearLayoutManager);
                    mUserStatus_rv.setAdapter(statusAdapter);
                    break;
                case 1:
                    Toast.makeText(getBaseContext(),"刷新失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mUser = bundle.getParcelable("User");
        }else {
            Toast.makeText(this,"Can't find user",Toast.LENGTH_SHORT).show();
            finish();
        }
        if (mUser != null){
            init();
        }
    }

    private void init() {
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

        Glide.with(this).load(mUser.getAvatar_large()).into(mUserIcon_civ);
        mUserName_tv.setText(mUser.getScreen_name());
        mUserLocationAndGender_tv.setText(getUserLocationAndGenderString());
        mDescription.setText(mUser.getDescription());
        mUserFollowers_tv.setText(String.valueOf(mUser.getFollowers_count()));
        mUserFriends_tv.setText(String.valueOf(mUser.getFriends_count()));

        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        ParamsOfUserTimeLine paramsOfUserTimeLine = new ParamsOfUserTimeLine.Builder()
                .access_token(accessToken.getToken())
                .uid(mUser.getUID())
                .screen_name(mUser.getScreen_name())
                .feature(FEATURE_ALL)
                .build();
        WBApi.getStatusUserTimeLine(paramsOfUserTimeLine, new HttpCallBack() {
            @Override
            public void onFinish(String response) {
                StatusTimeLine statusTimeLine = StatusTimeLine.getStatusesLine(response);
                statusList.addAll(statusTimeLine.getStatuses());
                Message message = new Message();
                message.what = 0;
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
