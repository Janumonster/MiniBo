package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.minibo.Adapter.PhotoViewAdapter;
import com.zzy.minibo.MyViews.PhotoViewPager;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.FilesManager;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.PicDownloadCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

public class PicturesActivity extends BaseActivity {

    private static final int DOWNLOAD_PICTURE_SUCCESS = 0;

    private PhotoViewPager viewPager;
    private List<String> mOriginList;
    private List<String> mPicturesUrls = new ArrayList<>();
    private int listLength = 0;
    private int currentPosition;
    private PhotoViewAdapter mOriginPhotoViewAdapter;

    private TextView picCountText_tv;
    private int postion = 0;
    private ImageButton downloadButton;
    private boolean isLoacl = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String filename = (String) msg.obj;
                    String path = Environment.getExternalStorageDirectory()+"/MiniBo/downloadPics/"+filename;
                    getBaseContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                    Toast toast = Toast.makeText(getApplicationContext(),null,Toast.LENGTH_SHORT);
                    toast.setText("已保存到："+Environment.getExternalStorageDirectory()+"/DCIM/Camera/"+path);
                    toast.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.new_act_in,R.anim.old_act_out);
        setContentView(R.layout.activity_pictures);
        viewPager = findViewById(R.id.pic_viewpager);
        picCountText_tv = findViewById(R.id.pic_count_text);
        downloadButton = findViewById(R.id.pic_save);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloafpic(mPicturesUrls.get(postion));
            }
        });
        initData();
    }


    private void initData() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition",0);
        isLoacl = intent.getBooleanExtra("isLocal",false);
        mOriginList = intent.getStringArrayListExtra("urls");
        listLength = mOriginList.size();
        picCountText_tv.setText((currentPosition+1)+" | "+listLength);
        if (!isLoacl){
            for (String str : mOriginList){
                mPicturesUrls.add("http://wx4.sinaimg.cn/large/" + str);
            }
        }else {
            mPicturesUrls.addAll(mOriginList);
        }
        mOriginPhotoViewAdapter = new PhotoViewAdapter(this, mPicturesUrls);
        viewPager.setAdapter(mOriginPhotoViewAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                picCountText_tv.setText(String.valueOf(i+1)+" | "+listLength);
                postion = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void downloafpic(String url){
        FilesManager.createFileDir(Environment.getExternalStorageDirectory()+"/MiniBo/downloadPics");
        final String path = Environment.getExternalStorageDirectory()+"/MiniBo/downloadPics/";
        String fileName = null;
        if (isLoacl){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
            Date date = new Date(System.currentTimeMillis());
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path+"MB_"+simpleDateFormat.format(date)+".jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0;
                message.obj = "MB_"+simpleDateFormat.format(date)+".jpg";
                handler.sendMessage(message);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else {
            WBApiConnector.downloadImage(url, new PicDownloadCallback() {
                @Override
                public void callback(InputStream in) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        FileOutputStream fileOutputStream = new FileOutputStream(path+"MB_"+simpleDateFormat.format(date)+".jpg");
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Message message = new Message();
                        message.what = 0;
                        message.obj = "MB_"+simpleDateFormat.format(date)+".jpg";
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

}
