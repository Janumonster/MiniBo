package com.zzy.minibo.Adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.R;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import java.util.List;

public class EmotionsAdapter extends RecyclerView.Adapter<EmotionsAdapter.ViewHolder> {

    private  final String path = Environment.getExternalStorageDirectory()+"/MiniBo/emotions/";
    private List<String> list;
    private Context mContext;
    private SimpleIntCallback simpleIntCallback;

    public void setSimpleIntCallback(SimpleIntCallback simpleIntCallback) {
        this.simpleIntCallback = simpleIntCallback;
    }

    public EmotionsAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.emotions);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.emotion_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String url = list.get(position);
        Glide.with(mContext)
                .load(path + url)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleIntCallback.callback(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
