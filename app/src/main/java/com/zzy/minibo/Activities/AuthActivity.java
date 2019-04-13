package com.zzy.minibo.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import com.zzy.minibo.Utils.FilesManager;
import com.zzy.minibo.Utils.HttpCallBack;
import com.zzy.minibo.Utils.WBApiConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

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

    /**
     * 授权监听
     */
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
                        getEmotions();
                        startActivity(new Intent(AuthActivity.this,MainActivity.class));
                        finish();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            //授权被取消
            Toast.makeText(AuthActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            //授权失败
            Toast.makeText(AuthActivity.this, wbConnectErrorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取表情地址
     */
    private void getEmotions() {
        Oauth2AccessToken oauth2AccessToken = AccessTokenKeeper.readAccessToken(this);
        WBApiConnector.getWBEmotions(oauth2AccessToken.getToken(), new HttpCallBack() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0 ; i < jsonArray.length() ; i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        saveEomtions(jsonObject.getString("value"),jsonObject.getString("url"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 下载表情并保存
     * @param value 表情名称：[呵呵]
     * @param url 表情地址
     */
    private void saveEomtions(final String value, final String url) {
        FilesManager.createFileDir(Environment.getExternalStorageDirectory()+"/MiniBo");
        FilesManager.createFileDir(Environment.getExternalStorageDirectory()+"/MiniBo/emotions");
        final String path = Environment.getExternalStorageDirectory()+"/MiniBo/emotions/";
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(url)
                        .build();
                try {
                    ResponseBody responseBody = client.newCall(request).execute().body();
                    if (responseBody != null){
                        InputStream in = responseBody.byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        FileOutputStream fileOutputStream = new FileOutputStream(path+value);
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
