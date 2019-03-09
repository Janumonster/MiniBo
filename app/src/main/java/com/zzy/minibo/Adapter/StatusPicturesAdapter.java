package com.zzy.minibo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.Status;

public class StatusPicturesAdapter extends RecyclerView.Adapter<StatusPicturesAdapter.ViewHolder> {

    private Status status;
    private Context context;

    public StatusPicturesAdapter(Context context, Status status){
        this.status = status;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
    @NonNull
    @Override
    public StatusPicturesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(320,320));
        imageView.setPadding(0,12,0,0);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusPicturesAdapter.ViewHolder viewHolder, int i) {
        Glide.with(context).load(status.getBmiddle_pic()+status.getPic_urls().get(i)).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return status.getPic_urls().size();
    }


}
