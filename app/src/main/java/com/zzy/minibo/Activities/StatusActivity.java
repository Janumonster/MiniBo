package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.CommentAdapter;
import com.zzy.minibo.Members.Comment;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfComments;
import com.zzy.minibo.Utils.AllParams.ParamsOfCreateComment;
import com.zzy.minibo.Utils.HttpCallBack;
import com.zzy.minibo.Utils.KeyBoardManager;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.Utils.WBClickSpan.UserIdClickSpan;
import com.zzy.minibo.WBListener.StatusTextFliterCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends BaseActivity {

    private static final String TAG = StatusActivity.class.getSimpleName();

    private static final int GET_COMMENTS_FIRST = 0;
    private static final int CREATE_COMMENT_SUCCESS = 1;


    private Oauth2AccessToken accessToken;

    private Toolbar toolbar;

    //微博内容相关
    private Status mStatus;
    private User mUser;
    private CircleImageView mUserIcon;
    private TextView mUserName;
    private TextView mCreateTime;
    private TextView mStatueText;
    private NineGlideView mStatusPictures;
    private LinearLayout mRepostStatusLayout;
    private TextView mRepostStatusUserName;
    private TextView mRepostStatusText;
    private NineGlideView mRepostStatusPictures;
    private LinearLayout mStatusBottomLayout;

    //底部菜单
    private ImageView mRepost;
    private ImageView mComments;
    private ImageView mLikes;

    //评论列表
    private TextView mCommentsNum;
    private TextView noCommentText;
    private List<Comment> commentList = new ArrayList<>();
    private RecyclerView commentCardRV;
    private CommentAdapter commentAdapter;

    //编辑
    private LinearLayout commentEditLayout;
    private ImageButton commentSendBtn;
    private EditText commentEditView;
    private TextView commentEditOthers;
    //编辑菜单
    private CheckBox isRepostCheckBox;
    private ImageView editMenu_photo;
    private ImageView editMenu_at;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_COMMENTS_FIRST:
                    if (commentList.size() == 0){
                        noCommentText.setVisibility(View.VISIBLE);
                    }else {
                        noCommentText.setVisibility(View.GONE);
                    }
                    commentAdapter.notifyDataSetChanged();
                    break;
                case CREATE_COMMENT_SUCCESS:
                    commentAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        setContentView(R.layout.activity_status);
        LitePal.getDatabase();
        accessToken = AccessTokenKeeper.readAccessToken(this);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            mStatus = bundle.getParcelable("status");
        }else {
            Log.d(TAG, "initData: bundle is null !");
        }

        if (mStatus != null && !mStatus.isTruncated()){
            initStatus(this,mStatus);
        }else {
            Log.d(TAG, "initData: status is null or be truncated!");
        }
    }

    /**
     * 初始化微博数据
     */
    private void initStatus(final Context context, final Status mStatus) {
        mUser = mStatus.getUser();
        if (mUser != null){
            Glide.with(this)
                    .load(mUser.getAvatar_large())
                    .into(mUserIcon);
            mUserName.setText(mUser.getScreen_name());
            mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,UserCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User",mUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }else {
            Log.d(TAG, "initStatus: Uer is null !");
        }
        mCreateTime.setText(TextFilter.TimeFliter(mStatus.getCreated_at()));

        if (mStatus.getText() == null){
            mStatueText.setVisibility(View.GONE);
        }else {
            mStatueText.setText(TextFilter.statusTextFliter(this, mStatus.getText(), new StatusTextFliterCallback() {
                @Override
                public void callback(String url,boolean isAll) {

                }
            }));
            mStatueText.setMovementMethod(new LinkMovementMethod());
        }

        if (mStatus.getPic_urls() != null ){
            mStatusPictures.setVisibility(View.VISIBLE);
            List<String> urls = new ArrayList<>();
            for (int m = 0;m < mStatus.getPic_urls().size();m++){
                urls.add(mStatus.getBmiddle_pic()+mStatus.getPic_urls().get(m));
            }
            mStatusPictures.setUrlList(urls);
        }

        if (mStatus.getRetweeted_status() != null){
            mRepostStatusLayout.setVisibility(View.VISIBLE);
            Status repostStatus = mStatus.getRetweeted_status();
            UserIdClickSpan userIdClickSpan_repost = new UserIdClickSpan(this,repostStatus.getUser().getScreen_name());
            SpannableString spannableString_repost = new SpannableString(repostStatus.getUser().getScreen_name());
            spannableString_repost.setSpan(userIdClickSpan_repost,0,spannableString_repost.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mRepostStatusUserName.setText(spannableString_repost);
            mRepostStatusUserName.setMovementMethod(new LinkMovementMethod());
            if (repostStatus.getText().length() == 0){
                mRepostStatusText.setVisibility(View.GONE);
            }else {
                mRepostStatusText.setText(TextFilter.statusTextFliter(this, repostStatus.getText(), new StatusTextFliterCallback() {
                    @Override
                    public void callback(String url,boolean isAll) {

                    }
                }));
                mRepostStatusText.setMovementMethod(new LinkMovementMethod());
            }
            if (repostStatus.getPic_urls() != null){
                mRepostStatusPictures.setVisibility(View.VISIBLE);
                List<String> urls_repost = new ArrayList<>();
                for (int m = 0;m < repostStatus.getPic_urls().size();m++){
                    urls_repost.add(repostStatus.getBmiddle_pic()+repostStatus.getPic_urls().get(m));
                }
                mRepostStatusPictures.setUrlList(urls_repost);
            }
        }


        mCommentsNum.setText("全部评论("+TextFilter.NumberFliter(mStatus.getComments_count())+")");
        mLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLikes.isSelected()){
                    mLikes.setSelected(false);
                }else {
                    mLikes.setSelected(true);
                }
            }
        });
        getCommentData();
    }

    /**
     * 初始化评论数据
     */
    private void getCommentData() {
        ParamsOfComments paramsOfComments = new ParamsOfComments.Builder()
                .access_token(accessToken.getToken())
                .statusId(mStatus.getIdstr())
                .build();
        WBApiConnector.getStatusComments(paramsOfComments, new HttpCallBack() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("comments");
                    for (int i =0 ;i<jsonArray.length();i++){
                        Comment comment = Comment.getCommentsFromJson(jsonArray.getString(i));
                        commentList.add(comment);
                    }
                    Message message = new Message();
                    message.what = GET_COMMENTS_FIRST;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {

        mUserIcon = findViewById(R.id.status_card_user_icon);
        mUserName = findViewById(R.id.status_card_user_name);
        mCreateTime = findViewById(R.id.status_card_create_at);
        mStatueText = findViewById(R.id.status_card_text);
        mStatusPictures = findViewById(R.id.status_card_images);

        mRepostStatusLayout = findViewById(R.id.status_card_repost_layout);
        mRepostStatusUserName = findViewById(R.id.status_card_repost_name);
        mRepostStatusText = findViewById(R.id.status_card_repost_text);
        mRepostStatusPictures = findViewById(R.id.status_card_repost_images);

        mStatusBottomLayout = findViewById(R.id.status_card_ll);
        mStatusBottomLayout.setVisibility(View.GONE);

        //底部菜单
        mRepost = findViewById(R.id.status_repost);
        mComments = findViewById(R.id.status_comment);
        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditView.setHint("回复@"+mUser.getScreen_name()+":");
                commentEditLayout.setVisibility(View.VISIBLE);
                KeyBoardManager.showKeyBoard(getBaseContext(),commentEditView);
            }
        });
        mLikes = findViewById(R.id.status_like);

        toolbar = findViewById(R.id.status_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //评论
        commentCardRV = findViewById(R.id.comment_card_comment_rv);
        commentAdapter = new CommentAdapter(this,commentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        commentCardRV.setLayoutManager(linearLayoutManager);
        commentCardRV.setAdapter(commentAdapter);
        noCommentText = findViewById(R.id.comment_card_no_comment);
        if (commentList.size() == 0){
            noCommentText.setVisibility(View.VISIBLE);
        }
        mCommentsNum = findViewById(R.id.status_comment_num);

        //编辑
        commentEditLayout = findViewById(R.id.status_comment_edit_layout);
        commentSendBtn = findViewById(R.id.comment_edit_send);
        commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ParamsOfCreateComment paramsOfCreateComment = new ParamsOfCreateComment.Builder()
                            .access_token(accessToken.getToken())
                            .comment(URLEncoder.encode(commentEditView.getText().toString(), "UTF-8"))
                            .idstr(mStatus.getIdstr())
                            .build();
                    WBApiConnector.createComment(paramsOfCreateComment, new HttpCallBack() {
                        @Override
                        public void onSuccess(String response) {
                            Comment comment = Comment.getCommentsFromJson(response);
                            commentList.add(0,comment);
                            Message message = new Message();
                            message.what = CREATE_COMMENT_SUCCESS;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        commentEditView = findViewById(R.id.comment_edit);
        commentEditOthers = findViewById(R.id.comment_edit_others);
        commentEditOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditLayout.setVisibility(View.GONE);
                KeyBoardManager.hideKeyBoard(getBaseContext(),commentEditView);
            }
        });
        isRepostCheckBox = findViewById(R.id.edit_menu_checkbox);
        editMenu_photo = findViewById(R.id.edit_menu_photo);
        editMenu_at = findViewById(R.id.edit_menu_at);

    }
}
