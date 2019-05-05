package com.zzy.minibo.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.PicDownloadCallback;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

public class PhotoViewAdapter extends PagerAdapter {

    private static final String TAG = "IMAGE_SIZE";

    private Activity mActivity;
    private List<String> pics;

    public PhotoViewAdapter(Activity activity,List<String> list){
        this.mActivity = activity;
        this.pics = list;
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        final String uri = pics.get(position);
        final SubsamplingScaleImageView photoView = new SubsamplingScaleImageView(mActivity);
        photoView.setScaleAndCenter(1.0f,new PointF(0,0));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(params);
        RequestOptions options = new RequestOptions();
        Glide.with(mActivity)
                .asFile()
                .load(uri)
                .apply(options)
                .into(new SimpleTarget<File>(){
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        photoView.setImage(ImageSource.uri(resource.getPath()));
                    }
                });

        container.addView(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.old_act_in,R.anim.new_act_out);
            }
        });
        return photoView;
    }

    @Override
    public int getCount() {
        return pics != null ? pics.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

}
