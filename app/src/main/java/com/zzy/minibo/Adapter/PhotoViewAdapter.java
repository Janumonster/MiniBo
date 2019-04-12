package com.zzy.minibo.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzy.minibo.R;

import java.util.List;

public class PhotoViewAdapter extends PagerAdapter {

    private Activity mActivity;
    private List<String> pics;
    private List<String> placeHolder;

    public PhotoViewAdapter(Activity activity,List<String> placeHolder,List<String> list){
        this.mActivity = activity;
        this.placeHolder = placeHolder;
        this.pics = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = pics.get(position);
        PhotoView photoView = new PhotoView(mActivity);

        Glide.with(mActivity)
                .load(url)
                .into(photoView);
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
