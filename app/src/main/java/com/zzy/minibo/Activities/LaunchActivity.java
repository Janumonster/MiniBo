package com.zzy.minibo.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.widget.Toast;

import com.zzy.minibo.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class LaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    public static final String TAG = "Lanuch";

    //所需权限
    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanuch);
        if(!EasyPermissions.hasPermissions(this,permissions)){
            EasyPermissions.requestPermissions(this,"程序需要这些权限才能正常运行",PERMISSION_REQUEST_CODE,permissions);
        }else {
            startApplication();
        }
    }

    private void startApplication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    startActivity(new Intent(LaunchActivity.this,AuthActivity.class));
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
