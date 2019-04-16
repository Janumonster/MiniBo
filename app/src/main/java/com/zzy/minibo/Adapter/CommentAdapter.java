package com.zzy.minibo.Adapter;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.Comment;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private Context mContext;

    private SimpleIntCallback simpleCallback = new SimpleIntCallback() {
        @Override
        public void callback(int i) {

        }
    };

    public void setSimpleCallback(SimpleIntCallback simpleCallback) {
        this.simpleCallback = simpleCallback;
    }

    private boolean isBottom = false;

    public CommentAdapter(Context context,List<Comment> list){
        this.mContext = context;
        this.commentList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView userName;
        TextView commentText;
        TextView createAt;
        ImageView likeNum;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.comment_card_user_icon);
            userName = itemView.findViewById(R.id.comment_card_user_name);
            commentText = itemView.findViewById(R.id.comment_card_text);
            createAt = itemView.findViewById(R.id.comment_card_create_at);
            likeNum = itemView.findViewById(R.id.comment_card_like);
            constraintLayout = itemView.findViewById(R.id.comment_card_context);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_card_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Comment comment = commentList.get(i);
        User user = comment.getUser();
        if (user != null){
            Glide.with(mContext)
                    .load(user.getAvatar_large())
//                    .placeholder(R.drawable.icon_user)
                    .into(viewHolder.circleImageView);
            viewHolder.userName.setText(user.getScreen_name());
        }
        viewHolder.commentText.setText(TextFilter.statusTextFliter(mContext, comment.getText(), null));
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
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleCallback.callback(i);
            }
        });

        if (i >= commentList.size()-1){
            isBottom = true;
        }else {
            isBottom = false;
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public boolean isBottom() {
        return isBottom;
    }
}
