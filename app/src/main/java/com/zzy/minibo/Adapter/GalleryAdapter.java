package com.zzy.minibo.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.ImageBean;
import com.zzy.minibo.R;
import com.zzy.minibo.WBListener.GalleryTapListener;
import com.zzy.minibo.WBListener.ItemSelectedCallback;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<ImageBean> mImageBeanList;
    private Context mContext;

    private int column = 3;
    private int spacing = 8;
    private int selectedNum = 0;

    private int maxSeclecterNum = 9;

    private ItemSelectedCallback itemSelectedCallback;

    private GalleryTapListener galleryTapListener;

    public GalleryAdapter(Context context, List<ImageBean> imageBeans){
        this.mContext = context;
        this.mImageBeanList = imageBeans;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_gallery,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryAdapter.ViewHolder holder, final int position) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    galleryTapListener.onTap(position-1);
            }
        });
        if (position == 0){
            Glide.with(mContext)
                    .load(R.drawable.ic_camera_alt_grey_400_24dp)
                    .into(holder.imageView);
            holder.checkBox.setVisibility(View.GONE);
            return;
        }
        final ImageBean imageBean = mImageBeanList.get(position-1);
        Glide.with(mContext)
                .load(new File(imageBean.getPath()))
                .into(holder.imageView);
        holder.checkBox.setVisibility(View.VISIBLE);


        holder.checkBox.setSelected(imageBean.isSelected());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isSelected()){
                    selectedNum --;
                    holder.checkBox.setSelected(false);
                }else {
                    selectedNum ++;
                    if (selectedNum > maxSeclecterNum){
                        Toast.makeText(mContext,"您只能选"+maxSeclecterNum+"张",Toast.LENGTH_SHORT).show();
                        selectedNum --;
                        return;
                    }
                    holder.checkBox.setSelected(true);
                }

                imageBean.setSelected(holder.checkBox.isSelected());
                itemSelectedCallback.callback(imageBean.isSelected(),position-1);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageBeanList.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_card_image);
            checkBox = itemView.findViewById(R.id.gallery_card_checkbox);
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int w = displayMetrics.widthPixels;
            int halfspacing = spacing / 2;
            int newWidth = w / column;
            int newHeight = newWidth;
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = newWidth;
            layoutParams.height = newHeight;
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(halfspacing,halfspacing,halfspacing,halfspacing);
        }
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getMaxSeclecterNum() {
        return maxSeclecterNum;
    }

    public void setMaxSeclecterNum(int maxSeclecterNum) {
        this.maxSeclecterNum = maxSeclecterNum;
    }

    public void setItemSelectedCallback(ItemSelectedCallback itemSelectedCallback) {
        this.itemSelectedCallback = itemSelectedCallback;
    }

    public void setGalleryTapListener(GalleryTapListener galleryTapListener) {
        this.galleryTapListener = galleryTapListener;
    }
}
