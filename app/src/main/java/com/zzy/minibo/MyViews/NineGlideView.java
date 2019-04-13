package com.zzy.minibo.MyViews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zzy.minibo.Activities.PicturesActivity;
import com.zzy.minibo.R;

import java.io.File;
import java.util.ArrayList;
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
    protected boolean displayOneImage(final ClickImageView imageView, String url, final int parentWidth) {
        final int[] width = new int[1];
        final int[] height = new int[1];
        final int[] newW = new int[1];
        final int[] newH = new int[1];
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        width[0] = resource.getWidth();
                        height[0] = resource.getHeight();

                        if (height[0] > width[0] * MAX_W_H_RATIO) {//h:w = 5:3
                            newW[0] = parentWidth / 2;
                            newH[0] = newW[0] * 5 / 3;
                        } else if (height[0] < width[0]) {//h:w = 2:3
                            newW[0] = parentWidth * 2 / 3;
                            newH[0] = newW[0] * 2 / 3;
                        } else {//newH:h = newW :w
                            newW[0] = parentWidth / 2;
                            newH[0] = height[0] * newW[0] / width[0];
                        }
                        setOneImageLayout(imageView,newW[0],newH[0]);
                    }
                });
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
        Intent intent = new Intent(mContext, PicturesActivity.class);
        intent.putExtra("currentPosition",postion);
        intent.putStringArrayListExtra("urls", (ArrayList<String>) urlList);
        mContext.startActivity(intent);
    }

}
