package com.zzy.minibo.MyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class MBViewPager extends ViewPager {

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
