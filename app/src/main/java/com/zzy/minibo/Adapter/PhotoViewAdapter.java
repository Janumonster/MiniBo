package com.zzy.minibo.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.FilesManager;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.PicDownloadCallback;
import com.zzy.minibo.WBListener.SimpleCallback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        final String url = pics.get(position);
        final PhotoView photoView = new PhotoView(mActivity);
        Glide.with(mActivity)
                .asBitmap()
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
