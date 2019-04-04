package com.zzy.minibo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Activities.StatusActivity;
import com.zzy.minibo.Activities.StatusDetialActivity;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBClickSpan.UserIdClickSpan;
import com.zzy.minibo.WBListener.StatusTypeListener;

import org.litepal.LitePal;

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
        ConstraintLayout statusBody;

        LinearLayout repostStatusLayout;
        TextView repostUsername;
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
            statusBody = itemView.findViewById(R.id.status_card_body);

            repostStatusLayout = itemView.findViewById(R.id.status_card_repost_layout);
            repostUsername = itemView.findViewById(R.id.status_card_repost_name);
            repostStatusText = itemView.findViewById(R.id.status_card_repost_text);
            repostStatusImages = itemView.findViewById(R.id.status_card_repost_images);

        }
    }

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.status_card_layout,viewGroup,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StatusAdapter.ViewHolder viewHolder, int i) {
        final Status status = statuses.get(i);
        if (status.getUser() != null){
            Glide.with(context).load(status.getUser().getAvatar_large()).into(viewHolder.userImage);
            viewHolder.userName.setText(status.getUser().getScreen_name());
        }

//        List<User> users = LitePal.where("statusId = ?",status.getIdstr()).find(User.class);
//        for (User u:users) {
//            Glide.with(context).load(u.getAvatar_large()).into(viewHolder.userImage);
//            viewHolder.userName.setText(u.getScreen_name());
//        }

        viewHolder.createTime.setText(status.getCreated_at());
        //待修改
        viewHolder.statusText.setText(status.getSpanText());

//        viewHolder.statusText.setText(TextFilter.statusTextFliter(context, status.getText(), new StatusTypeListener() {
//            @Override
//            public void videoUrL(String url) {
//
//            }
//        }));

        viewHolder.statusText.setMovementMethod(LinkMovementMethod.getInstance());

        viewHolder.statusRepostNum.setText(status.getReposts_count());
        viewHolder.statusCommentNum.setText(status.getComments_count());
        viewHolder.statusLikeNum.setText(status.getAttitudes_count());
        viewHolder.statusLikeNum.setSelected(status.isLike());
        //用于存放完整中等品质图片地址
        List<String> list = new ArrayList<>();
        //加载微博图片
        if (status.getPic_urls() != null){
            for (int m = 0;m < status.getPic_urls().size();m++){
                list.add(status.getBmiddle_pic()+status.getPic_urls().get(m));
            }
            viewHolder.statusPictures.setUrlList(list);
        }
        //是否是转发微博
        if (status.getRetweeted_status() != null) {
            viewHolder.repostStatusLayout.setVisibility(View.VISIBLE);
            viewHolder.repostStatusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, StatusDetialActivity.class));
                }
            });
            Status s = status.getRetweeted_status();
//            List<User> repostUsers = LitePal.where("statusId = ?",status.getRetweeted_status().getIdstr()).find(User.class);
//            for (User u:repostUsers) {
//                UserIdClickSpan userIdClickSpan = new UserIdClickSpan(context,"@"+u.getScreen_name());
//                SpannableString spannableString = new SpannableString("@"+u.getScreen_name());
//                spannableString.setSpan(userIdClickSpan,0,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                viewHolder.repostUsername.setText(spannableString);
//                viewHolder.repostUsername.setMovementMethod(LinkMovementMethod.getInstance());
//            }
            UserIdClickSpan userIdClickSpan = new UserIdClickSpan(context,"@"+status.getRetweeted_status().getUser().getScreen_name());
            SpannableString spannableString = new SpannableString("@"+status.getRetweeted_status().getUser().getScreen_name());
            spannableString.setSpan(userIdClickSpan,0,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.repostUsername.setText(spannableString);
            viewHolder.repostUsername.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.repostStatusText.setText(status.getRetweeted_status().getSpanText());
            viewHolder.repostStatusText.setMovementMethod(LinkMovementMethod.getInstance());
            //转发微博是否是图片微博
            for (int n = 0 ; n < s.getPic_urls().size() ; n++){
                list.add(status.getBmiddle_pic()+status.getRetweeted_status().getPic_urls().get(n));
            }
            viewHolder.repostStatusImages.setUrlList(list);
        }else {
            viewHolder.repostStatusLayout.setVisibility(View.GONE);
        }

        viewHolder.statusBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, StatusActivity.class));
            }
        });
        viewHolder.statusRepostNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.statusCommentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, StatusActivity.class));
            }
        });
        viewHolder.statusLikeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = Integer.parseInt(status.getAttitudes_count());
                if (status.isLike()){
                    viewHolder.statusLikeNum.setSelected(false);
                    status.setLike(false);
                    status.setAttitudes_count(String.valueOf(like - 1));
                }else {
                    viewHolder.statusLikeNum.setSelected(true);
                    status.setLike(true);
                    status.setAttitudes_count(String.valueOf(like + 1));
                }
                notifyDataSetChanged();
            }
        });

        if (i >= statuses.size()-1){
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
