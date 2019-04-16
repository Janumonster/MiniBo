package com.zzy.minibo.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Activities.UserCenterActivity;
import com.zzy.minibo.Members.LP_USER;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfUserInfo;
import com.zzy.minibo.WBListener.HttpCallBack;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;


import org.litepal.LitePal;

import java.util.List;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFragment extends Fragment {


    private TextView mUserCenter_tv;
    private CircleImageView mUserIcon_civ;
    private TextView mUserName_tv;
    private TextView mUserStatusNum_tv;
    private TextView mUserFriendNum_tv;
    private TextView mUserFansNum_tv;

    private User mUser;
    private  Oauth2AccessToken accessToken;
    private Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:setUserText();break;
            }
        }
    };

    private void setUserText() {
        Glide.with(mContext).load(mUser.getAvatar_large()).into(mUserIcon_civ);
        mUserName_tv.setText(mUser.getScreen_name());
        mUserStatusNum_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getStatuses_count())));
        mUserFriendNum_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFriends_count())));
        mUserFansNum_tv.setText(TextFilter.NumberFliter(String.valueOf(mUser.getFollowers_count())));
    }

    public MenuFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mContext = getContext();
        init(view);
        return view;
    }

    private void init(View view) {
        mUserIcon_civ = view.findViewById(R.id.fg_menu_ig_user_icon);
        mUserName_tv = view.findViewById(R.id.fg_menu_tv_user_name);
        mUserCenter_tv = view.findViewById(R.id.fg_menu_tv_user_center);
        mUserStatusNum_tv = view.findViewById(R.id.fg_menu_tv_status);
        mUserFriendNum_tv = view.findViewById(R.id.fg_menu_tv_friends);
        mUserFansNum_tv = view.findViewById(R.id.fg_menu_tv_fans);

        accessToken = AccessTokenKeeper.readAccessToken(getContext());

        if (accessToken != null && accessToken.isSessionValid()){

            List<LP_USER> lp_users = LitePal.where("uidstr = ?",accessToken.getUid()).find(LP_USER.class);
            for (LP_USER lp_user : lp_users){
                mUser = User.makeJsonToUser(lp_user.getJson());
            }
            setUserText();
            ParamsOfUserInfo paramsOfUserInfo = new ParamsOfUserInfo.Builder()
                    .access_token(accessToken.getToken())
                    .uid(accessToken.getUid())
                    .build();
            WBApiConnector.getUserInfo(paramsOfUserInfo, new HttpCallBack() {
                @Override
                public void onSuccess(String response) {
                    mUser = User.makeJsonToUser(response);
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        mUserCenter_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("User",mUser);
                Intent intent = new Intent(getContext(), UserCenterActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    
}
