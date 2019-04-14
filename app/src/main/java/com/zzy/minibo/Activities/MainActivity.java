package com.zzy.minibo.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.zzy.minibo.Adapter.MainPagerAdapter;
import com.zzy.minibo.Fragments.MenuFragment;
import com.zzy.minibo.Fragments.StatusFragment;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.MyViews.MBViewPager;
import com.zzy.minibo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private StatusFragment mStatusFragment;
    private MenuFragment mMenuFragment;

    private List<Fragment> fragmentList = new ArrayList<>();

    private MBViewPager mViewPager;
    private TextView mHomeTv, mMenuTv;

    private ScaleAnimation sclaeUp;
    private ScaleAnimation sclaeDowm;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        EmotionsMatcher.initEmotions();
        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.main_viewpager);
        mHomeTv = findViewById(R.id.main_tv_home);
        mMenuTv = findViewById(R.id.main_tv_menu);

        mHomeTv.setOnClickListener(this);
        mMenuTv.setOnClickListener(this);

        initScaleAnimation();

        mStatusFragment = new StatusFragment();
        mMenuFragment = new MenuFragment();

        fragmentList.add(mStatusFragment);
        fragmentList.add(mMenuFragment);

        MainPagerAdapter mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setAdapter(mMainPagerAdapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_tv_home:
                if (!mHomeTv.isSelected()){
                    startTextScaleAnimation(mHomeTv,mMenuTv);
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.main_tv_menu:
                if (!mMenuTv.isSelected()){
                    startTextScaleAnimation(mMenuTv,mHomeTv);
                    mViewPager.setCurrentItem(1);
                }
                break;
        }
    }

    /**
     * 初始化字体缩放动画
     */
    public void initScaleAnimation(){
        //字体放大动画
        sclaeUp = new ScaleAnimation(1.0f,1.3f,1.0f,1.3f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sclaeUp.setDuration(200);
        sclaeUp.setFillAfter(true);
        //字体缩小动画
        sclaeDowm = new ScaleAnimation(1.3f,1.0f,1.3f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sclaeDowm.setDuration(200);
        sclaeDowm.setFillAfter(true);

        mHomeTv.setSelected(true);
        mHomeTv.startAnimation(sclaeUp);
        mHomeTv.setTypeface(Typeface.DEFAULT_BOLD);
    }


    /**
     * 开始字体缩放动画
     * @param selectedTv 需要放大的TextView
     * @param unSelectedTv 需要缩小的TextView
     */
    public void startTextScaleAnimation(TextView selectedTv,TextView unSelectedTv){

        selectedTv.startAnimation(sclaeUp);
        unSelectedTv.startAnimation(sclaeDowm);
        selectedTv.setSelected(true);
        unSelectedTv.setSelected(false);
        selectedTv.setTypeface(Typeface.DEFAULT_BOLD);
        unSelectedTv.setTypeface(Typeface.DEFAULT);
    }

}
