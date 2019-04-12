package com.zzy.minibo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.Comment;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.WBListener.StatusTextFliterCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private Context mContext;

    public CommentAdapter(Context context,List<Comment> list){
        this.mContext = context;
        this.commentList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView userName;
        TextView commentText;
        TextView createAt;
        TextView likeNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.comment_card_user_icon);
            userName = itemView.findViewById(R.id.comment_card_user_name);
            commentText = itemView.findViewById(R.id.comment_card_text);
            createAt = itemView.findViewById(R.id.comment_card_create_at);
            likeNum = itemView.findViewById(R.id.comment_card_like);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_card_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Comment comment = commentList.get(i);
        User user = comment.getUser();
        if (user != null){
            Glide.with(mContext)
                    .load(user.getAvatar_large())
                    .placeholder(R.drawable.icon_user)
                    .into(viewHolder.circleImageView);
            viewHolder.userName.setText(user.getScreen_name());
        }
        viewHolder.commentText.setText(TextFilter.statusTextFliter(mContext, comment.getText(), new StatusTextFliterCallback() {
            @Override
            public void callback(String url, boolean isAll) {

            }
        }));
        viewHolder.commentText.setMovementMethod(new LinkMovementMethod());
        viewHolder.createAt.setText(TextFilter.TimeFliter(comment.getCreate_at()));
        viewHolder.likeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.likeNum.isSelected()){
                    viewHolder.likeNum.setSelected(false);
                }else {
                    viewHolder.likeNum.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


}
