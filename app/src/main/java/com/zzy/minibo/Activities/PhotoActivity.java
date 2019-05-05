package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.yalantis.ucrop.UCrop;
import com.zzy.minibo.Adapter.GalleryPhotoViewAdapter;
import com.zzy.minibo.Members.ImageBean;
import com.zzy.minibo.MyViews.PhotoViewPager;
import com.zzy.minibo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends BaseActivity {

    public static final int RESULT_DONE = 10001;

    private int currentPostion = 0;
    private int selectedNum = 0;
    private boolean iscrop = false;
    private boolean unCrop = true;
    private List<ImageBean> mImageBeanList = new ArrayList<>();
    private List<ImageBean> selectedImageList = new ArrayList<>();
    private GalleryPhotoViewAdapter photoViewAdapter;

    private LinearLayout toolbarLayout;
    private ImageView backBtn;
    private TextView currentPositionText;
    private TextView totalPhotoNumber;
    private ImageView selectedBtn;
    private PhotoViewPager photoViewPager;
    private LinearLayout bottomBar;
    private TextView editBtn;
    private TextView selectedNumText;
    private TextView doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_photo);
        initView();
        initData();
    }

    private void initView() {
        toolbarLayout = findViewById(R.id.photo_toolbar_layout);
        backBtn = findViewById(R.id.photo_toolbar_back_iv_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("selected_num",selectedNum);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>) selectedImageList);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        currentPositionText = findViewById(R.id.photo_current_position);
        totalPhotoNumber = findViewById(R.id.photo_total_photo_num);
        selectedBtn = findViewById(R.id.photo_select_btn);
        selectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBtn.isSelected()){
                    selectedBtn.setSelected(false);
                    selectedNum --;
                    for (int i = 0 ;i < selectedImageList.size();i++){
                        if (selectedImageList.get(i).getPath().equals(mImageBeanList.get(currentPostion).getPath())){
                            selectedImageList.remove(i);
                            break;
                        }
                    }
                }else {
                    selectedBtn.setSelected(true);
                    selectedNum ++;
                    selectedImageList.add(mImageBeanList.get(currentPostion));
                    if (selectedNum > 9){
                        selectedBtn.setSelected(false);
                        selectedNum --;
                        Toast.makeText(getBaseContext(),"您只能选9张",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                selectedNumText.setText(String.valueOf(selectedNum));
                mImageBeanList.get(currentPostion).setSelected(selectedBtn.isSelected());
            }
        });
        photoViewPager = findViewById(R.id.photo_recycler_body);
        editBtn = findViewById(R.id.photo_edit_tv_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!unCrop){
                    startUCorp();
                }
            }
        });
        selectedNumText = findViewById(R.id.photo_selected_num);
        doneBtn = findViewById(R.id.photo_done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>) selectedImageList);
                intent.putExtras(bundle);
                setResult(RESULT_DONE,intent);
                finish();
            }
        });
    }

    private void startUCorp() {
        Uri sourceUri = Uri.fromFile(new File(mImageBeanList.get(currentPostion).getPath()));
        //裁剪后保存到文件中
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
        Date date = new Date(System.currentTimeMillis());
        Uri destinationUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/",simpleDateFormat.format(date)+".jpeg"));
        UCrop uCrop = UCrop.of(sourceUri,destinationUri);
        Intent uCropIntent = uCrop.getIntent(this);
        uCropIntent.setClass(this,CropActivity.class);
        startActivityForResult(uCropIntent,UCrop.REQUEST_CROP);
    }

    private void initData() {
        Intent intent = getIntent();
        currentPostion = intent.getIntExtra("position",0);
        unCrop = intent.getBooleanExtra("unCrop",true);
        selectedNum = intent.getIntExtra("selected_num",0);
        selectedNumText.setText(String.valueOf(selectedNum));
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            mImageBeanList = bundle.getParcelableArrayList("imageList");
        }
        if (mImageBeanList != null){
            currentPositionText.setText(String.valueOf(currentPostion + 1));
            totalPhotoNumber.setText(String.valueOf(mImageBeanList.size()));
            selectedBtn.setSelected(mImageBeanList.get(currentPostion).isSelected());
            photoViewAdapter = new GalleryPhotoViewAdapter(this,mImageBeanList);
            photoViewPager.setAdapter(photoViewAdapter);
            for (ImageBean imageBean : mImageBeanList){
                if (imageBean.isSelected()){
                    selectedImageList.add(imageBean);
                }
            }
        }
        photoViewPager.setCurrentItem(currentPostion);
        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPostion = position;
                currentPositionText.setText(String.valueOf(currentPostion + 1));
                selectedBtn.setSelected(mImageBeanList.get(currentPostion).isSelected());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("selected_num",selectedNum);
        intent.putExtra("ucrop",iscrop);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("imageList", (ArrayList<? extends Parcelable>) selectedImageList);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null){
            switch (requestCode){
                case UCrop.REQUEST_CROP:
                    if (resultCode == RESULT_OK){
                        Uri uri = UCrop.getOutput(data);
                        ImageBean imageBean = null;
                        if (uri != null) {
                            imageBean = new ImageBean(uri.getPath(),0,null);
                            imageBean.setSelected(true);
                        }
                        selectedImageList.set(currentPostion,imageBean);
                        mImageBeanList.set(currentPostion,imageBean);
                        photoViewAdapter.notifyDataSetChanged();
                        getBaseContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }
                    iscrop = true;
                    break;
            }

        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
