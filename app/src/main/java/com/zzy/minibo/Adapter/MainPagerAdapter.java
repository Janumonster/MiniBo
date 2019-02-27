package com.zzy.minibo.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 主界面Viewpager的适配器
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> titles;

    public MainPagerAdapter(FragmentManager fm,List<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    public MainPagerAdapter(FragmentManager fm,List<Fragment> fragments,List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
