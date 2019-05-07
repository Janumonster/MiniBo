package com.zzy.minibo.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.zzy.minibo.Adapter.GalleryAdapter;
import com.zzy.minibo.Members.ImageBean;
import com.zzy.minibo.R;
import com.zzy.minibo.WBListener.GalleryTapListener;
import com.zzy.minibo.WBListener.ItemSelectedCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class GalleryActivity extends BaseActivity {

    public static final int FIND_OVER = 0;
    public static final int REQUEST_CODE_ALL = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int REQUEST_CODE_DONE = 102;
    public static final int TAKE_PHOTO = 1001;

    private LinearLayout toobarLayout;
    private ImageView backBtn;
    private RecyclerView galleryRecyclerView;
    private GalleryAdapter galleryAdapter;
    private LinearLayout bottomLayout;
//    private TextView previewBtn;
    private TextView doneBtn;
    private List<ImageBean> mImageBeanList = new ArrayList<>();
    private List<ImageBean> seclectedList = new ArrayList<>();
    private TextView selectedCount;
    private int selectedNum = 0;
    private Uri imageUri;
    private String fileName;
    private String filePath = Environment.getExternalStorageDirectory()+"/DCIM/Camera";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FIND_OVER:
                    galleryAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_gallery);
        initView();
        getPhotos();
    }

    private void initView() {
        toobarLayout = findViewById(R.id.gallery_toolbar_layout);
        backBtn = findViewById(R.id.gallery_toolbar_back_iv_btn);
        selectedCount = findViewById(R.id.gallery_selected_num);
        galleryRecyclerView = findViewById(R.id.gallery_recycler_body);
        bottomLayout = findViewById(R.id.gallery_bottom_bar);
        doneBtn = findViewById(R.id.gallery_done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedNum != 0){
                    List<ImageBean> list = new ArrayList<>();
                    for (ImageBean imageBean : mImageBeanList){
                        if (imageBean.isSelected()){
                            list.add(imageBean);
                        }
                    }
                    Intent intent = new Intent(getBaseContext(),PhotoActivity.class);
                    intent.putExtra("selected_num",selectedNum);
                    intent.putExtra("unCrop",false);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>)list);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_DONE);
                }else {
                    Toast.makeText(getBaseContext(),"您还未选择图片",Toast.LENGTH_SHORT).show();
                }
            }
        });
        galleryAdapter = new GalleryAdapter(this,mImageBeanList);
        galleryAdapter.setColumn(4);
        galleryAdapter.setItemSelectedCallback(new ItemSelectedCallback() {
            @Override
            public void callback(boolean isSeclected, int position) {
                if (isSeclected){
                    selectedNum ++;
                    seclectedList.add(mImageBeanList.get(position));
                }else {
                    selectedNum --;
                    for (int i = 0 ;i < seclectedList.size();i++){
                        if (seclectedList.get(i).getPath().equals(mImageBeanList.get(position).getPath())){
                            seclectedList.remove(i);
                            break;
                        }
                    }
                }
                mImageBeanList.get(position).setSelected(isSeclected);
                selectedCount.setText(String.valueOf(selectedNum));
            }
        });
        galleryAdapter.setGalleryTapListener(new GalleryTapListener() {
            @Override
            public void onTap(int postion) {
                if (postion < 0){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
                    Date date = new Date(System.currentTimeMillis());
                    fileName = "MB_"+simpleDateFormat.format(date)+".jpg";
                    File file = new File(filePath,fileName);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        imageUri = FileProvider.getUriForFile(GalleryActivity.this,"com.zzy.minibo.provider",file);
                    }else {
                        imageUri = Uri.fromFile(file);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent,TAKE_PHOTO);
                }else {
                    Intent intent = new Intent(getBaseContext(),PhotoActivity.class);
                    intent.putExtra("position",postion);
                    intent.putExtra("selected_num",selectedNum);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>) mImageBeanList);
                    intent.putExtras(bundle);
                    if(EasyPermissions.hasPermissions(getBaseContext(), Manifest.permission.CAMERA)){
                        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
                    }

                }
            }
        });
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        galleryRecyclerView.setLayoutManager(layoutManager);
        galleryRecyclerView.setAdapter(galleryAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Bundle bundle = new Bundle();
        if (data != null){
           bundle = data.getExtras();
        }
        switch (requestCode){
            case REQUEST_CODE_PREVIEW:
                if (resultCode == RESULT_OK && data != null){
                    int num = data.getIntExtra("selected_num",0);
                    if (selectedNum != num){
                        selectedNum = num;
                        selectedCount.setText(String.valueOf(selectedNum));
                        if (bundle != null) {
                            List<ImageBean> list = bundle.getParcelableArrayList("imageList");
                            seclectedList.clear();
                            seclectedList.addAll(list);
                        }
                    }
                }else if (resultCode == PhotoActivity.RESULT_DONE){
                    Intent intent = data;
                    setResult(REQUEST_CODE_DONE,intent);
                    finish();
                }
                break;
            case TAKE_PHOTO:
                ImageBean imageBean = new ImageBean("file:///storage/emulated/0/DCIM/Camera/"+fileName,0,null);
                seclectedList.add(0,imageBean);
                this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:///storage/emulated/0/DCIM/Camera/"+fileName)));
                break;
            case REQUEST_CODE_DONE:
                Intent intent = new Intent();
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }

    private void refreshGallery() {
        mImageBeanList.clear();
        Log.d("TEST", "refreshGallery: ");
        getPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGallery();
    }

    private void getPhotos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projImage = {MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DATA
                        , MediaStore.Images.Media.SIZE
                        , MediaStore.Images.Media.DISPLAY_NAME};
                Cursor mCursor = getContentResolver().query(mImageUri,
                        projImage,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/gif"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        File file = new File(path);
                        if (file.exists()){
                            int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
                            String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                            //用于展示相册初始化界面
                            ImageBean imageBean = new ImageBean(path, size, displayName);
                            for (int i = 0; i < seclectedList.size();i++){
                                if (seclectedList.get(i).getPath().equals(imageBean.getPath())){
                                    imageBean.setSelected(true);
                                    break;
                                }
                            }
                            mImageBeanList.add(imageBean);
//                        // 获取该图片的父路径名
//                        String dirPath = new File(path).getParentFile().getAbsolutePath();
                        }
                    }
                    mCursor.close();
                    Message message = new Message();
                    message.what = FIND_OVER;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
