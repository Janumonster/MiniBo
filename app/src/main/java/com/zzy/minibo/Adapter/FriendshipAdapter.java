package com.zzy.minibo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendshipAdapter extends RecyclerView.Adapter<FriendshipAdapter.ViewHolder> {

    private List<User> userList;
    private Context mContext;
    private SimpleIntCallback simpleIntCallback;

    public void setSimpleIntCallback(SimpleIntCallback simpleIntCallback) {
        this.simpleIntCallback = simpleIntCallback;
    }

    public FriendshipAdapter(Context context,List<User> list){
        this.mContext = context;
        this.userList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User user = userList.get(position);
        if (user != null){
            Glide.with(mContext)
                    .load(user.getAvatar_large())
                    .into(holder.userImage);
            holder.userName.setText(user.getScreen_name());
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleIntCallback != null){
                    simpleIntCallback.callback(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CircleImageView userImage;
        TextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.user_card_layout);
            userImage = itemView.findViewById(R.id.user_card_user_image);
            userName = itemView.findViewById(R.id.user_card_user_name);
        }
    }
}
