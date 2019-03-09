package com.zzy.minibo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity {

    private ImageButton mBackButton_ib;
    private User mUser;

    private CircleImageView mUserIcon_civ;
    private TextView mUserName_tv;
    private TextView mUserMsg;
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mUser = bundle.getParcelable("User");
        }
        if (mUser!=null){
            init();
        }
    }

    private void init() {
        mBackButton_ib = findViewById(R.id.uc_ib_back);
        mUserIcon_civ = findViewById(R.id.uc_civ_user_img);
        mUserName_tv = findViewById(R.id.uc_tv_user_name);
        mUserMsg = findViewById(R.id.uc_tv_user_msg);
        mDescription = findViewById(R.id.uc_tv_user_description);

        Glide.with(this).load(mUser.getAvatar_large()).into(mUserIcon_civ);
        mUserName_tv.setText(mUser.getScreen_name());
        mDescription.setText(mUser.getDescription());
        mUserMsg.setText(getUserMsg());
        mBackButton_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获得介绍的Text，之后采用SpanableString来进行设计
     * @return
     */
    private String getUserMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append("一个来自 ").append(mUser.getLocation()).append(" 的 ");
        switch (mUser.getGender()){
            case "m":sb.append("男生");break;
            case "f":sb.append("女生");break;
            case "n":sb.append("用户");break;
        }
        sb.append("\n关注了 ").append(mUser.getFriends_count()).append(" 人，").append("有着 ").append(mUser.getFollowers_count()).append(" 个粉丝");
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
