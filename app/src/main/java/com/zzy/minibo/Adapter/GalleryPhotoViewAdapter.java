package com.zzy.minibo.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzy.minibo.Members.ImageBean;

import java.util.List;

public class GalleryPhotoViewAdapter extends PagerAdapter {

    private static final String TAG = "IMAGE_SIZE";

    private Activity mActivity;
    private List<ImageBean> pics;

    public GalleryPhotoViewAdapter(Activity activity, List<ImageBean> list){
        this.mActivity = activity;
        this.pics = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        final ImageBean imageBean = pics.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(imageBean.getPath());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height > width * 3){
            bitmap.recycle();
            SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(mActivity);
            imageView.setImage(ImageSource.uri(imageBean.getPath()));
            imageView.setScaleAndCenter(1.0f,new PointF(0,0));
            container.addView(imageView);
            return imageView;
        }else {
            bitmap.recycle();
            final PhotoView photoView = new PhotoView(mActivity);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(mActivity)
                    .load(imageBean.getPath())
                    .into(photoView);
            container.addView(photoView);
            return photoView;
        }
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
