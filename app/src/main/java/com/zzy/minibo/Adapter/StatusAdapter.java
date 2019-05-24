package com.zzy.minibo.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.zzy.minibo.Activities.StatusActivity;
import com.zzy.minibo.Activities.StatusEditActivity;
import com.zzy.minibo.Activities.UserCenterActivity;
import com.zzy.minibo.Members.LP_COMMENTS;
import com.zzy.minibo.Members.LP_LikeStatusId;
import com.zzy.minibo.Members.LP_RepostCount;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.URLHolder;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.Utils.WBClickSpan.UserIdClickSpan;
import com.zzy.minibo.WBListener.HttpCallBack;
import com.zzy.minibo.WBListener.PictureTapCallback;
import com.zzy.minibo.WBListener.RepostStatusCallback;
import com.zzy.minibo.WBListener.SimpleIntCallback;
import com.zzy.minibo.WBListener.StatusTapCallback;
import com.zzy.minibo.WBListener.StatusTextFliterCallback;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.litepal.LitePal;

import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    public static final String TAG = "StatusAdapter";
    public static final int VIDEO_FROM_STATUS = 0;
    public static final int VIDEO_FROM_REPOST = 1;

    private List<Status> statuses;
    private Context mContext;
    private boolean isGetBottom = false;
    private PictureTapCallback pictureTapCallback;
    private RepostStatusCallback repostStatusCallback;
    private StatusTapCallback statusTapCallback;



    public void setStatusTapCallback(StatusTapCallback statusTapCallback) {
        this.statusTapCallback = statusTapCallback;
    }

    public void setPictureTapCallback(PictureTapCallback pictureTapCallback) {
        this.pictureTapCallback = pictureTapCallback;
    }
    public void setRepostStatusCallback(RepostStatusCallback repostStatusCallback) {
        this.repostStatusCallback = repostStatusCallback;
    }

    public StatusAdapter(List<Status> statuses, Context mContext){
        this.statuses = statuses;
        this.mContext = mContext;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImage;
        TextView userName;
        TextView createTime;
        TextView statusText;
        TextView statusRepostNum;
        TextView statusCommentNum;
        TextView statusLikeNum;
        JCVideoPlayerStandard statusVideo;
        NineGlideView statusPictures;
        ConstraintLayout statusBody;

        LinearLayout repostStatusLayout;
        TextView repostUsername;
        TextView repostStatusText;
        JCVideoPlayerStandard repostStatusVideo;
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
            statusVideo = itemView.findViewById(R.id.status_card_video);
            statusPictures = itemView.findViewById(R.id.status_card_images);
            statusBody = itemView.findViewById(R.id.status_card_body);

            repostStatusLayout = itemView.findViewById(R.id.status_card_repost_layout);
            repostUsername = itemView.findViewById(R.id.status_card_repost_name);
            repostStatusText = itemView.findViewById(R.id.status_card_repost_text);
            repostStatusVideo = itemView.findViewById(R.id.status_card_repost_video);
            repostStatusImages = itemView.findViewById(R.id.status_card_repost_images);

        }
    }

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(mContext).inflate(R.layout.status_card_layout,viewGroup,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StatusAdapter.ViewHolder viewHolder, final int i) {
        final Status status = statuses.get(i);
        final Status repost_status = status.getRetweeted_status();
//        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                URLHolder urlHolder = (URLHolder) msg.obj;
//                switch (msg.what){
//                    case VIDEO_FROM_STATUS:
//                        if (urlHolder.isResult() && urlHolder.getType() == 39){
////                            viewHolder.statusVideo.setVisibility(View.VISIBLE);
//                            viewHolder.statusVideo.setUp(urlHolder.getUrl_long(),JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,status.getUser().getScreen_name()+"的视频");
//                        }
//                        break;
//                    case VIDEO_FROM_REPOST:
//                        if (urlHolder.isResult() && urlHolder.getType() == 39){
////                            viewHolder.repostStatusVideo.setVisibility(View.VISIBLE);
//                            viewHolder.repostStatusVideo.setUp(urlHolder.getUrl_long(),JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,repost_status.getUser().getScreen_name()+"的视频");
//                        }
//                        break;
//                }
//            }
//        };
        if (status.getUser() != null){
            Glide.with(mContext)
                    .load(status.getUser().getAvatar_large())
                    .placeholder(R.drawable.icon_user)
                    .into(viewHolder.userImage);
            viewHolder.userName.setText(status.getUser().getScreen_name());
        }
        viewHolder.createTime.setText(TextFilter.TimeFliter(status.getCreated_at()));
        //待修改
        viewHolder.statusText.setText(TextFilter.statusTextFliter(mContext, status.getText(), new StatusTextFliterCallback() {
            @Override
            public void callback(final String url, boolean isAll) {
//                if (!isAll && status.getRetweeted_status() == null && (status.getPic_urls() == null || status.getPic_urls().size() ==0)){
//                    WBApiConnector.getShortUrlType(AccessTokenKeeper.readAccessToken(mContext), url, new HttpCallBack() {
//                        @Override
//                        public void onSuccess(String response) {
//                            Message message = new Message();
//                            message.what = VIDEO_FROM_STATUS;
//                            URLHolder urlHolder = URLHolder.getInstanceFromJSON(response);
//                            message.obj = urlHolder;
//                            handler.sendMessage(message);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//
//                        }
//                    });
//
//                }else {
//                    viewHolder.statusVideo.setVisibility(View.GONE);
//                }
            }
        }));
        viewHolder.statusText.setMovementMethod(LinkMovementMethod.getInstance());
        List<LP_RepostCount> lp_repostCounts = LitePal.where("status_id = ?",status.getIdstr()).find(LP_RepostCount.class);
        viewHolder.statusRepostNum.setText(String.valueOf(Integer.valueOf(status.getReposts_count())+lp_repostCounts.size()));
        List<LP_COMMENTS> lp_comments = LitePal.where("status_id = ?",status.getIdstr()).find(LP_COMMENTS.class);
        viewHolder.statusCommentNum.setText(String.valueOf(Integer.valueOf(status.getComments_count())+lp_comments.size()));
        viewHolder.statusLikeNum.setText(TextFilter.NumberFliter(status.getAttitudes_count()));
        viewHolder.statusLikeNum.setSelected(false);
        if (LitePal.isExist(LP_LikeStatusId.class,"status_id = "+status.getIdstr())){
            viewHolder.statusLikeNum.setSelected(true);
            List<LP_LikeStatusId> lp_likeStatusIds = LitePal.where("status_id = ?",status.getIdstr()).find(LP_LikeStatusId.class);
            for (LP_LikeStatusId l : lp_likeStatusIds){
                viewHolder.statusLikeNum.setText(String.valueOf(l.getLike_num()));
            }

        }


        //用于存放完整中等品质图片地址
        List<String> list = new ArrayList<>();
        //加载微博图片
        viewHolder.statusPictures.setVisibility(View.GONE);
        if (status.getPic_urls() != null && status.getPic_urls().size() != 0){
            viewHolder.statusPictures.setVisibility(View.VISIBLE);
            if (!status.isLocal()){
                for (int m = 0;m < status.getPic_urls().size();m++){
                    list.add(status.getBmiddle_pic()+status.getPic_urls().get(m));
                }
            }else {
                list.addAll(status.getPic_urls());
            }
            viewHolder.statusPictures.setUrlList(list);
        }
        viewHolder.statusPictures.setSimpleIntCallback(new SimpleIntCallback() {
            @Override
            public void callback(int position) {
                if (pictureTapCallback != null){
                    pictureTapCallback.callback(i,position,0,status.isLocal());
                }

            }
        });
        //是否是转发微博
        if (repost_status != null) {
            viewHolder.repostStatusLayout.setVisibility(View.VISIBLE);

            viewHolder.repostStatusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusTapCallback.callback(i,true);
                }
            });
            viewHolder.repostUsername.setText("@"+repost_status.getUser().getScreen_name());
            viewHolder.repostUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User",repost_status.getUser());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            viewHolder.repostUsername.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.repostStatusVideo.setVisibility(View.GONE);
            viewHolder.repostStatusText.setText(TextFilter.statusTextFliter(mContext, repost_status.getText(), new StatusTextFliterCallback() {
                @Override
                public void callback(final String url, boolean isAll) {
                    status.setTruncated(isAll);
                    repost_status.setTruncated(isAll);
//                    if (repost_status.getPic_urls() == null || repost_status.getPic_urls().size() ==0){
//                        WBApiConnector.getShortUrlType(AccessTokenKeeper.readAccessToken(mContext), url, new HttpCallBack() {
//                            @Override
//                            public void onSuccess(String response) {
//                                Message message = new Message();
//                                message.what = VIDEO_FROM_REPOST;
//                                URLHolder urlHolder = URLHolder.getInstanceFromJSON(response);
//                                message.obj = urlHolder;
//                                handler.sendMessage(message);
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//                    }else {
//                        viewHolder.repostStatusVideo.setVisibility(View.GONE);
//                    }
                }
            }));
            viewHolder.repostStatusText.setMovementMethod(LinkMovementMethod.getInstance());
            //转发微博是否是图片微博
            viewHolder.repostStatusImages.setVisibility(View.GONE);
            if (repost_status.getPic_urls() != null &&repost_status.getPic_urls().size() != 0){
                for (int n = 0 ; n < repost_status.getPic_urls().size() ; n++){
                    if (repost_status.isLocal()){
                        list.add(status.getRetweeted_status().getPic_urls().get(n));
                    }else {
                        list.add(status.getBmiddle_pic()+status.getRetweeted_status().getPic_urls().get(n));
                    }
                }
                viewHolder.repostStatusImages.setVisibility(View.VISIBLE);
                viewHolder.repostStatusImages.setUrlList(list);
            }
            viewHolder.repostStatusImages.setSimpleIntCallback(new SimpleIntCallback() {
                @Override
                public void callback(int postion) {
                    pictureTapCallback.callback(i,postion,1,repost_status.isLocal());
                }
            });
        }else {
            viewHolder.repostStatusLayout.setVisibility(View.GONE);
        }

        //-------------------------------点击事件[start]--------------------------------------------
        //微博外部
        viewHolder.statusBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTapCallback.callback(i,false);
//                Intent intent = new Intent(mContext, StatusActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("status",status);
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
            }
        });
        viewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("User",status.getUser());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        viewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("User",status.getUser());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        //微博转发按钮
        viewHolder.statusRepostNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repostStatusCallback != null){
                    repostStatusCallback.callback(i);
                }
            }
        });
        //微博评论按钮
        viewHolder.statusCommentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTapCallback.callback(i,false);
//                Intent intent = new Intent(mContext, StatusActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("status",status);
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
            }
        });
        //微博点赞按钮
        viewHolder.statusLikeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LP_LikeStatusId lp_likeStatusId = new LP_LikeStatusId();
                lp_likeStatusId.setStatus_id(status.getIdstr());
                int like = Integer.parseInt(viewHolder.statusLikeNum.getText().toString());
                if (viewHolder.statusLikeNum.isSelected()){
                    viewHolder.statusLikeNum.setSelected(false);
                    viewHolder.statusLikeNum.setText(String.valueOf(like - 1));
                    if (LitePal.isExist(LP_LikeStatusId.class,"status_id = "+status.getIdstr())){
                        LitePal.deleteAll(LP_LikeStatusId.class,"status_id = "+status.getIdstr());
                    }
                    status.setIsLike(0);
                }else {
                    viewHolder.statusLikeNum.setSelected(true);
                    viewHolder.statusLikeNum.setText(String.valueOf(like + 1));
                    status.setIsLike(1);
                    lp_likeStatusId.setLike_num(like+1);
                    lp_likeStatusId.save();
                }


                status.setAttitudes_count(viewHolder.statusLikeNum.getText().toString());
            }
        });
        //---------------------------------点击事件[end]--------------------------------------------

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
