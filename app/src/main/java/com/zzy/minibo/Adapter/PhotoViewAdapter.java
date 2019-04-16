package com.zzy.minibo.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzy.minibo.R;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PhotoViewAdapter extends PagerAdapter {

    private static final String TAG = "IMAGE_SIZE";

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
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        final String uri = pics.get(position);
        final PhotoView photoView = new PhotoView(mActivity);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(mActivity)
                .load(uri)
//                .placeholder(R.drawable.ic_placeholder)
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
