package com.zzy.minibo.MyViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zzy.minibo.Activities.PicturesActivity;
import com.zzy.minibo.R;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public SimpleIntCallback simpleIntCallback = new SimpleIntCallback() {
        @Override
        public void callback(int i) {

        }
    };

    public void setSimpleIntCallback(SimpleIntCallback simpleIntCallback) {
        this.simpleIntCallback = simpleIntCallback;
    }

    @Override
    protected boolean displayOneImage(final ClickImageView imageView, final String url, final int parentWidth) {
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int w = resource.getWidth();
                        int h = resource.getHeight();

                        int newW;
                        int newH;
                        if (h > w * MAX_W_H_RATIO) {//比较长的图 h:w = 5:3
                            newW = parentWidth / 2;
                            newH = newW * 5 / 3;
                        } else if (h < w) {//比较扁的图 h:w = 2:3
                            newW = parentWidth * 2 / 3;
                            newH = newW * 2 / 3;
                        } else {//一般情况显示原始比例 newH:h = newW :w
                            newW = parentWidth / 2;
                            newH = h * newW / w;
                        }
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        setOneImageLayout(imageView,newW,newH);
                        RequestOptions options = new RequestOptions();
                        options.placeholder(R.drawable.loading_drawable);
                        Glide.with(mContext)
                                .load(url)
                                .apply(options)
                                .into(imageView);
                    }
                });


        return false;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void displayImage(ClickImageView imageView, String url) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.loading_drawable);
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    @Override
    protected void onClickImage(int postion, String url, List<String> urlList) {
        simpleIntCallback.callback(postion);
    }

}
