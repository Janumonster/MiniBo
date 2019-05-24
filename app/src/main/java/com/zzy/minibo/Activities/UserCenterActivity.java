package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.StatusAdapter;
import com.zzy.minibo.Fragments.StatusFragment;
import com.zzy.minibo.Members.LP_USER;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.StatusTimeLine;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserTimeLine;
import com.zzy.minibo.WBListener.HttpCallBack;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.PictureTapCallback;
import com.zzy.minibo.WBListener.RepostStatusCallback;
import com.zzy.minibo.WBListener.StatusTapCallback;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity {

    private static final String TAG = UserCenterActivity.class.getSimpleName();

    private static final int FEATURE_ALL = 0;
    private static final int FEATURE_CREATE_BY_ME = 1;

    private static final int GET_USER_STATUS_SUCCESS = 0;
    private static final int GET_USER_STATUS_ERROR = 1;
    private static final int GET_USER_INFO_FROM_API = 2;

    private User mUser;
//    private Toolbar mToolbar;
    private List<Status> statusList = new ArrayList<>();

    private ImageView cover_image;
    private CircleImageView mUserIcon_civ;
    private TextView mUserName_tv;
    private TextView mUserLocationAndGender_tv;
    private TextView mDescription;
    private TextView mUserFollowers_tv;
    private TextView mUserFriends_tv;
    private RecyclerView mUserStatus_rv;
    private ConstraintLayout mStatusLayout;
    private LinearLayout linearLayout;
    private TextView verified;
    private TextView creatime;
    private ImageView backBtn;

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
//        mToolbar = findViewById(R.id.uc_toolbar);
        cover_image = findViewById(R.id.uc_top_image);
        mUserIcon_civ = findViewById(R.id.uc_civ_user_img);
        mUserName_tv = findViewById(R.id.uc_tv_user_name);
        mUserLocationAndGender_tv = findViewById(R.id.uc_tv_user_location_gender);
        mDescription = findViewById(R.id.uc_tv_user_description);
        mUserFollowers_tv = findViewById(R.id.uc_tv_user_followers);
        mUserFriends_tv = findViewById(R.id.uc_tv_user_friends);
//        mUserStatus_rv = findViewById(R.id.uc_rv_status_list);
//        mStatusLayout = findViewById(R.id.uc_status_list);
        linearLayout = findViewById(R.id.uc_more_detial);
        verified = findViewById(R.id.uc_verified);
        creatime = findViewById(R.id.uc_create_time);
        backBtn = findViewById(R.id.uc_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        statusAdapter = new StatusAdapter(statusList,this);
//        statusAdapter.setRepostStatusCallback(new RepostStatusCallback() {
//            @Override
//            public void callback(int position) {
//                Intent intent = new Intent(getBaseContext(),StatusEditActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("Status",statusList.get(position));
//                intent.putExtras(bundle);
//                startActivityForResult(intent, StatusFragment.REPOST_STATUS_CODE);
//            }
//        });
//        statusAdapter.setStatusTapCallback(new StatusTapCallback() {
//            @Override
//            public void callback(int position, boolean isRepost) {
//                Intent intent = new Intent(getBaseContext(), StatusActivity.class);
//                Bundle bundle = new Bundle();
//                if (isRepost){
//                    bundle.putParcelable("status",statusList.get(position).getRetweeted_status());
//                }else {
//                    bundle.putParcelable("status",statusList.get(position));
//                }
//                intent.putExtras(bundle);
//                startActivityForResult(intent,StatusFragment.REPOST_STATUS_CODE);
//            }
//        });
//        statusAdapter.setPictureTapCallback(new PictureTapCallback() {
//            @Override
//            public void callback(int statusPosition, int postion, int from, boolean islocal) {
//                Intent intent = new Intent(getBaseContext(), PicturesActivity.class);
//                intent.putExtra("currentPosition",postion);
//                intent.putExtra("isLocal",islocal);
//                if (from == 0){ // 0，外部微博，1为转发微博，这里进行判断
//                    intent.putStringArrayListExtra("urls", (ArrayList<String>) statusList.get(statusPosition).getPic_urls());
//                } else {
//                    intent.putStringArrayListExtra("urls", (ArrayList<String>) statusList.get(statusPosition).getRetweeted_status().getPic_urls());
//                }
//                startActivity(intent);
//            }
//        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        mUserStatus_rv.setLayoutManager(linearLayoutManager);
//        mUserStatus_rv.setAdapter(statusAdapter);
    }

    private void initData(){
        if (mUser == null){
            String user_name = mIntent.getStringExtra("user_name");
            if (user_name != null){
                List<LP_USER> lp_users = LitePal.where("screen_name = ?",user_name).find(LP_USER.class);
                for (LP_USER lp_user : lp_users){
                    mUser = User.makeJsonToUser(lp_user.getJson());
                }
            }
        }
        if (mUser != null){
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
//            if (mUser.getUID().equals(accessToken.getUid())){
//                linearLayout.setVisibility(View.GONE);
//                mStatusLayout.setVisibility(View.VISIBLE);
//                ParamsOfUserTimeLine paramsOfUserTimeLine = new ParamsOfUserTimeLine.Builder()
//                        .access_token(accessToken.getToken())
//                        .uid(mUser.getUID())
//                        .screen_name(mUser.getScreen_name())
//                        .feature(FEATURE_ALL)
//                        .build();
//                WBApiConnector.getStatusUserTimeLine(paramsOfUserTimeLine, new HttpCallBack() {
//                    @Override
//                    public void onSuccess(String response) {
//                        StatusTimeLine statusTimeLine = StatusTimeLine.getStatusesLine(getApplicationContext(),response);
//                        statusList.addAll(statusTimeLine.getStatuses());
//                        Message message = new Message();
//                        message.what = GET_USER_STATUS_SUCCESS;
//                        handler.sendMessage(message);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Message message = new Message();
//                        message.what = GET_USER_STATUS_ERROR;
//                        handler.sendMessage(message);
//                    }
//                });
//            }
            if (mUser.getCover_image_phone() != null){
                Glide.with(this)
                        .load(mUser.getCover_image_phone())
                        .into(cover_image);
            }
            Glide.with(this)
                    .load(mUser.getAvatar_large())
                    .placeholder(R.drawable.icon_user)
                    .into(mUserIcon_civ);
            mUserName_tv.setText(mUser.getScreen_name());
            mUserLocationAndGender_tv.setText(getUserLocationAndGenderString());
            mDescription.setText(mUser.getDescription());
            mUserFollowers_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFollowers_count())));
            mUserFriends_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFriends_count())));
            creatime.setText("创建时间："+TextFilter.getCreateTime(mUser.getCreated_at()));
            if (mUser.isVerified()){
                verified.setText("认证信息："+mUser.getVerified_reason());
            }


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
