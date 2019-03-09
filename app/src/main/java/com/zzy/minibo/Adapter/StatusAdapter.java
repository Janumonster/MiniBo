package com.zzy.minibo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    public static final String TAG = "StatusAdapter";

    private List<Status> statuses;
    private Context context;
    private boolean isGetBottom = false;

    public StatusAdapter(List<Status> statuses,Context context){
        this.statuses = statuses;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImage;
        TextView userName;
        TextView createTime;
        TextView statusText;
        TextView statusRepostNum;
        TextView statusCommentNum;
        TextView statusLikeNum;
        NineGlideView statusPictures;

        LinearLayout repostStatusLayout;
        TextView repostStatusText;
        NineGlideView repostStatusImages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.status_card_user_icon);
            userName = itemView.findViewById(R.id.status_card_user_name);
            createTime = itemView.findViewById(R.id.status_card_create_at);
            statusText = itemView.findViewById(R.id.status_card_text);
            statusRepostNum = itemView.findViewById(R.id.status_card_repost);
            statusCommentNum = itemView.findViewById(R.id.status_card_comment);
            statusLikeNum = itemView.findViewById(R.id.status_card_like);
            statusPictures = itemView.findViewById(R.id.status_card_images);
            repostStatusLayout = itemView.findViewById(R.id.status_card_repost_layout);
            repostStatusText = itemView.findViewById(R.id.status_card_repost_text);
            repostStatusImages = itemView.findViewById(R.id.status_card_repost_images);
        }
    }

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.status_layout,viewGroup,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder viewHolder, int i) {
        final Status status = statuses.get(i);
        Log.d(TAG, "onBindViewHolder: "+status.getUser().getName()+" "+status.getType());
        Glide.with(context).load(status.getUser().getAvatar_large()).into(viewHolder.userImage);
        viewHolder.userName.setText(status.getUser().getScreen_name());
        viewHolder.createTime.setText(status.getCreated_at());
        //待修改
        viewHolder.statusText.setText(status.getText());
        viewHolder.statusRepostNum.setText(status.getReposts_count());
        viewHolder.statusCommentNum.setText(status.getComments_count());
        viewHolder.statusLikeNum.setText(status.getAttitudes_count());
        //用于存放完整中等品质图片地址
        List<String> list = new ArrayList<>();
        //是否图片微博
        if (status.getPic_urls().size() >=1) {
            viewHolder.statusPictures.setVisibility(View.VISIBLE);
            for (int m = 0;m<status.getPic_urls().size();m++){
                list.add(0,status.getBmiddle_pic()+status.getPic_urls().get(m));
            }
            viewHolder.statusPictures.setUrlList(list);
        } else {
            viewHolder.statusPictures.setVisibility(View.GONE);
        }

        //是否是转发微博
        if (status.getRetweeted_status() != null) {
            viewHolder.repostStatusLayout.setVisibility(View.VISIBLE);
            Status s = status.getRetweeted_status();
            viewHolder.repostStatusText.setText(s.getUser().getScreen_name() + ":" + s.getText());
            //转发微博是否是图片微博
            if (s.getPic_urls().size() >=1) {
                viewHolder.repostStatusImages.setVisibility(View.VISIBLE);
                for (int n = 0 ; n < status.getPic_urls().size() ; n++){
                    list.add(0,status.getBmiddle_pic()+status.getRetweeted_status().getPic_urls().get(n));
                }
                viewHolder.repostStatusImages.setUrlList(list);
            }else {
                viewHolder.repostStatusImages.setVisibility(View.GONE);
            }
        }else {
            viewHolder.repostStatusLayout.setVisibility(View.GONE);
        }

        if (i >= statuses.size()-3){
            isGetBottom = true;
        }else {
            isGetBottom = false;
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public boolean isNearBottom(){
        return isGetBottom;
    }
}
