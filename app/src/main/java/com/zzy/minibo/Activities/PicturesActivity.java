package com.zzy.minibo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.TextView;

import com.zzy.minibo.Adapter.PhotoViewAdapter;
import com.zzy.minibo.MyViews.PhotoViewPager;
import com.zzy.minibo.R;

import java.util.ArrayList;
import java.util.List;

public class PicturesActivity extends BaseActivity {

    private PhotoViewPager viewPager;
    private List<String> mOriginList;
    private List<String> mPicturesUrls = new ArrayList<>();
    private int listLength = 0;
    private int currentPosition;
    private PhotoViewAdapter mOriginPhotoViewAdapter;

    TextView picCountText_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.new_act_in,R.anim.old_act_out);
        setContentView(R.layout.activity_pictures);
        viewPager = findViewById(R.id.pic_viewpager);
        picCountText_tv = findViewById(R.id.pic_count_text);
        initData();
    }


    private void initData() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition",0);
        mOriginList = intent.getStringArrayListExtra("urls");
        for (String str : mOriginList){
            mPicturesUrls.add("http://wx4.sinaimg.cn/large/" + str.substring(30));
        }
        listLength = mOriginList.size();
        picCountText_tv.setText(String.valueOf(currentPosition+1)+" | "+listLength);
        mOriginPhotoViewAdapter = new PhotoViewAdapter(this, mOriginList,mPicturesUrls);
        viewPager.setAdapter(mOriginPhotoViewAdapter);
        viewPager.setCurrentItem(currentPosition);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                picCountText_tv.setText(String.valueOf(i+1)+" | "+listLength);
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
}
