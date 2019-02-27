package com.zzy.minibo.MyViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MBViewPager extends ViewPager{

    private boolean isScrolled = false;

    public MBViewPager(@NonNull Context context) {
        super(context);
    }

    public MBViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrolled(boolean scrolled) {
        isScrolled = scrolled;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrolled){
            return super.onTouchEvent(ev);
        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if (isScrolled){
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
