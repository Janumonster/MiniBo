package com.zzy.minibo.MyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzy.minibo.R;

import java.util.List;

public class NineGlideView extends NineImageLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGlideView(Context context) {
        super(context);
    }

    public NineGlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NineGlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean displayOneImage(ClickImageView imageView, String url, int parentWidth) {

        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.loading_drawable)
                .into(imageView);
        return false;
    }

    @Override
    protected void displayImage(ClickImageView imageView, String url) {
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.loading_drawable)
                .into(imageView);
    }

    @Override
    protected void onClickImage(int postion, String url, List<String> urlList) {
        Toast.makeText(mContext,"点击了第"+postion+"张图片",Toast.LENGTH_SHORT).show();
    }

}
