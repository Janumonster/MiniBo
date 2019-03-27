package com.zzy.minibo.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.Constants;

public class AuthActivity extends BaseActivity {


    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;

    private Button permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        installWBSdk(this);

        permission = findViewById(R.id.btn_auth);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()){
            Toast.makeText(this,"欢迎回来！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AuthActivity.this,MainActivity.class));
            finish();
        }

        //该实例用于授权回调
        mSsoHandler = new SsoHandler(AuthActivity.this);
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new SelfWbAuthListener());
            }
        });

    }

    private void installWBSdk(Context context) {
        WbSdk.install(context,new AuthInfo(context,Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mSsoHandler != null){
            mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
        }
    }

    private class SelfWbAuthListener implements WbAuthListener{

        @Override
        public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
            AuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = oauth2AccessToken;
                    if (mAccessToken.isSessionValid()){
                        AccessTokenKeeper.writeAccessToken(AuthActivity.this,mAccessToken);
                        Toast.makeText(AuthActivity.this,"已成功授权",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AuthActivity.this,MainActivity.class));
                    }
                }
            });
        }

        @Override
        public void cancel() {
            /**
             * 授权被取消
             */
            Toast.makeText(AuthActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            /**
             * 授权失败
             */
            Toast.makeText(AuthActivity.this, wbConnectErrorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
