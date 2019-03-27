package com.zzy.minibo.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.FilesManager;
import com.zzy.minibo.Utils.HttpCallBack;
import com.zzy.minibo.Utils.WBApiConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;

public class LanuchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    public static final String TAG = "Lanuch";

    //所需权限
    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanuch);
        sharedPreferences = getSharedPreferences("Emotions",MODE_PRIVATE);
        if(!EasyPermissions.hasPermissions(this,permissions)){
            EasyPermissions.requestPermissions(this,"程序需要这些权限才能正常运行",PERMISSION_REQUEST_CODE,permissions);
        }else {
            getEmotions();
            startApplication();
        }
    }

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

    private void startApplication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    startActivity(new Intent(LanuchActivity.this,AuthActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this,"感谢您的配合。",Toast.LENGTH_SHORT).show();
        startApplication();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this,"权限拿不到啊！",Toast.LENGTH_SHORT).show();
    }
}
