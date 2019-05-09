package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Adapter.CommentAdapter;
import com.zzy.minibo.Adapter.EmotionsAdapter;
import com.zzy.minibo.Members.Comment;
import com.zzy.minibo.Members.LP_COMMENTS;
import com.zzy.minibo.Members.LP_EMOTIONS;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.AllParams.ParamsOfCommentReply;
import com.zzy.minibo.Utils.AllParams.ParamsOfComments;
import com.zzy.minibo.Utils.AllParams.ParamsOfCreateComment;
import com.zzy.minibo.WBListener.HttpCallBack;
import com.zzy.minibo.Utils.KeyBoardManager;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.Utils.WBApiConnector;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends BaseActivity {

    private static final String TAG = StatusActivity.class.getSimpleName();

    private static final int GET_COMMENTS_FIRST = 0;
    private static final int CREATE_COMMENT_SUCCESS = 1;
    private static final int FRESH_COMMENT_SUCCESS = 2;
    private static final int GET_MORE_COMMENT_SUCCESS = 3;
    private static final int REPOST_STATUS = 10000;


    private Oauth2AccessToken accessToken;
    private Activity activity;
    private Toolbar toolbar;

    //微博内容相关
    private Status mStatus;
    private Status mRepostStatus;
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
    private String mReplyCommentId;
    private EditText searchEdittext;
    private ImageButton searchBtn;
    private boolean isSearching = false;
    private TextView noCommentText;
    private List<Comment> commentList = new ArrayList<>();
    private List<Comment> commentListCache = new ArrayList<>();
    private RecyclerView commentCardRV;
    private CommentAdapter commentAdapter;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout progressBar;

    //编辑
    private String mCommentUserNameCache = null;
    private LinearLayout commentEditLayout;
    private ImageButton commentSendBtn;
    private EditText commentEditView;
    private TextView commentEditOthers;
    //编辑菜单
    private CheckBox isRepostCheckBox;
    private ImageView editMenu_emotions;
    private RecyclerView emotionsLayout;
    private List<String> emotions_path = new ArrayList<>();
    private ImageView editMenu_at;

    private boolean isRefreshing = false;
    private boolean isReply = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_COMMENTS_FIRST:
                    refreshLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setRefreshing(false);
                    if (commentList.size() == 0){
                        noCommentText.setVisibility(View.VISIBLE);
                    }else {
                        noCommentText.setVisibility(View.GONE);
                    }
                    if (commentList.size() > Integer.valueOf(mStatus.getAttitudes_count())){
                        mCommentsNum.setText("全部评论("+String.valueOf(commentList.size())+")");
                    }
                    commentAdapter.notifyDataSetChanged();
                    break;
                case CREATE_COMMENT_SUCCESS:
                    isReply = false;
                    Comment comment = Comment.getCommentsFromJson((String) msg.obj);
                    commentList.add(0,comment);
                    commentEditView.setText("");
                    refreshLayout.setRefreshing(false);
                    commentEditLayout.setVisibility(View.GONE);
                    noCommentText.setVisibility(View.GONE);
                    commentAdapter.notifyDataSetChanged();
                    mCommentsNum.setText("全部评论("+TextFilter.NumberFliter(String.valueOf(commentList.size()))+")");
                    Toast toast_create = Toast.makeText(getBaseContext(),null,Toast.LENGTH_SHORT);
                    toast_create.setText("已发送");
                    toast_create.show();
                    break;
                case GET_MORE_COMMENT_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    if (commentListCache.size() != 0){
                        commentList.addAll(commentListCache);
                        commentAdapter.notifyDataSetChanged();
                    }else {
                        Toast toast_more = Toast.makeText(getBaseContext(),null,Toast.LENGTH_SHORT);
                        toast_more.setText("没有了");
                        toast_more.show();
                    }
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
        activity = this;
        accessToken = AccessTokenKeeper.readAccessToken(this);
        initView();
        initData();
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
        mStatusPictures.setSimpleIntCallback(new SimpleIntCallback() {
            @Override
            public void callback(int i) {
                Intent intent = new Intent(getBaseContext(), PicturesActivity.class);
                intent.putExtra("currentPosition",i);
                intent.putExtra("isLocal",mStatus.isLocal());
                if (mStatus.getRetweeted_status() == null){
                    intent.putStringArrayListExtra("urls", (ArrayList<String>) mStatus.getPic_urls());
                }else {
                    intent.putStringArrayListExtra("urls", (ArrayList<String>) mStatus.getRetweeted_status().getPic_urls());
                }
                startActivity(intent);
            }
        });

        mRepostStatusLayout = findViewById(R.id.status_card_repost_layout);
        mRepostStatusUserName = findViewById(R.id.status_card_repost_name);
        mRepostStatusText = findViewById(R.id.status_card_repost_text);
        mRepostStatusPictures = findViewById(R.id.status_card_repost_images);
        mRepostStatusPictures.setSimpleIntCallback(new SimpleIntCallback() {
            @Override
            public void callback(int i) {
                Intent intent = new Intent(getBaseContext(), PicturesActivity.class);
                intent.putExtra("currentPosition",i);
                if (mStatus.getRetweeted_status() == null){
                    intent.putStringArrayListExtra("urls", (ArrayList<String>) mStatus.getPic_urls());
                }else {
                    intent.putStringArrayListExtra("urls", (ArrayList<String>) mStatus.getRetweeted_status().getPic_urls());
                }
                startActivity(intent);
            }
        });

        mStatusBottomLayout = findViewById(R.id.status_card_ll);
        mStatusBottomLayout.setVisibility(View.GONE);

        //底部菜单
        mRepost = findViewById(R.id.status_repost);
        mRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),StatusEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Status",mStatus);
                intent.putExtras(bundle);
                startActivityForResult(intent,REPOST_STATUS);
            }
        });
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
        searchEdittext = findViewById(R.id.status_comment_search_edit);
        searchEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchEdittext.isFocused()){
                    searchEdittext.clearFocus();
                    KeyBoardManager.hideKeyBoard(getBaseContext(),searchEdittext);
                }else {
                    searchEdittext.requestFocus();
                    KeyBoardManager.showKeyBoard(getBaseContext(),searchEdittext);
                }
            }
        });
        searchBtn = findViewById(R.id.status_comment_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardManager.hideKeyBoard(getBaseContext(),searchEdittext);
                searchEdittext.clearFocus();
                findString(searchEdittext.getText().toString());
            }
        });
        commentCardRV = findViewById(R.id.comment_card_comment_rv);
        commentAdapter = new CommentAdapter(this,commentList);
        commentAdapter.setSimpleCallback(new SimpleIntCallback() {
            @Override
            public void callback(int i) {
                isReply = true;
                mReplyCommentId = commentList.get(i).getIdstr();
                mCommentUserNameCache = commentList.get(i).getUser().getScreen_name();
                commentEditView.setHint("回复 @"+ mCommentUserNameCache +" :");
                commentEditLayout.setVisibility(View.VISIBLE);
                KeyBoardManager.showKeyBoard(getBaseContext(),commentEditView);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        commentCardRV.setLayoutManager(linearLayoutManager);
        commentCardRV.setAdapter(commentAdapter);
        commentCardRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && commentAdapter.isBottom()){
                    //加载更多
                    getMoreStatus();
                }
            }
        });
        noCommentText = findViewById(R.id.comment_card_no_comment);
        if (commentList.size() == 0){
            noCommentText.setVisibility(View.VISIBLE);
        }
        mCommentsNum = findViewById(R.id.status_comment_num);
        refreshLayout = findViewById(R.id.comment_refresh_layout);
        refreshLayout.setVisibility(View.GONE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                searchEdittext.setText("");
                searchEdittext.clearFocus();
                if (commentList.size() <= Integer.valueOf(mStatus.getComments_count())){
                    getCommentData();
                }else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        progressBar = findViewById(R.id.status_comment_progressbar);

        //编辑
        commentEditLayout = findViewById(R.id.status_comment_edit_layout);
        commentSendBtn = findViewById(R.id.comment_edit_send);
        commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardManager.hideKeyBoard(getBaseContext(),commentEditView);
                String commenttext = null;
                Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getBaseContext());
                if (isReply){
                    commenttext = "回复@"+ mCommentUserNameCache +":"+commentEditView.getText().toString();
                    ParamsOfCommentReply paramsOfCommentReply = new ParamsOfCommentReply();
                    paramsOfCommentReply.setAccess_token(accessToken.getToken());
                    paramsOfCommentReply.setCid(mReplyCommentId);
                    paramsOfCommentReply.setId(mStatus.getIdstr());
                    paramsOfCommentReply.setComment(commenttext);
                    Log.d(TAG, "onClick: "+paramsOfCommentReply.getCid()+"\n" +
                            paramsOfCommentReply.getId()+"\n");
                    WBApiConnector.createCommentReply(paramsOfCommentReply, new HttpCallBack() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d(TAG, "onSuccess: "+response);
                            Message message = new Message();
                            message.obj = response;
                            message.what = CREATE_COMMENT_SUCCESS;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }else {
                    commenttext = commentEditView.getText().toString();
                    ParamsOfCreateComment paramsOfCreateComment = new ParamsOfCreateComment.Builder()
                            .access_token(accessToken.getToken())
                            .comment(commenttext)
                            .idstr(mStatus.getIdstr())
                            .build();
                    WBApiConnector.createComment(paramsOfCreateComment, new HttpCallBack() {
                        @Override
                        public void onSuccess(String response) {
                            Message message = new Message();
                            message.obj = response;
                            message.what = CREATE_COMMENT_SUCCESS;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }

            }
        });
        commentEditView = findViewById(R.id.comment_edit);
        commentEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionsLayout.setVisibility(View.GONE);
            }
        });
        commentEditOthers = findViewById(R.id.comment_edit_others);
        commentEditOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditLayout.setVisibility(View.GONE);
                emotionsLayout.setVisibility(View.GONE);
                KeyBoardManager.hideKeyBoard(getBaseContext(),commentEditView);
            }
        });
        editMenu_emotions = findViewById(R.id.comment_edit_menu_add_emotion);
        editMenu_emotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardManager.hideKeyBoard(getBaseContext(),commentEditView);
                emotionsLayout.setVisibility(View.VISIBLE);
            }
        });
        emotionsLayout = findViewById(R.id.comment_edit_menu_emotions);
        List<LP_EMOTIONS> lp_emotions = LitePal.findAll(LP_EMOTIONS.class);
        for (LP_EMOTIONS l : lp_emotions){
            emotions_path.add(l.getValue());
        }
        EmotionsAdapter emotionsAdapter = new EmotionsAdapter(emotions_path,this);
        emotionsAdapter.setSimpleIntCallback(new SimpleIntCallback() {
            @Override
            public void callback(int i) {
                int curPostion = commentEditView.getSelectionStart();
                StringBuilder sb = new StringBuilder(commentEditView.getText().toString());
                sb.insert(curPostion,emotions_path.get(i));
                commentEditView.setText(TextFilter.statusTextFliter(getBaseContext(),sb.toString(),null));
                commentEditView.setSelection(curPostion+emotions_path.get(i).length());
            }
        });
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(8,StaggeredGridLayoutManager.VERTICAL);
        emotionsLayout.setLayoutManager(staggeredGridLayoutManager);
        emotionsLayout.setAdapter(emotionsAdapter);

    }


    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            mStatus = bundle.getParcelable("status");
        }else {
            Log.d(TAG, "initData: bundle is null !");
        }

        if (mStatus != null){
            initStatus(this,mStatus);
        }else {
            Log.d(TAG, "initData: status is null !");
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
            mStatueText.setText(TextFilter.statusTextFliter(this, mStatus.getText(), null));
            mStatueText.setMovementMethod(new LinkMovementMethod());
        }

        if (mStatus.getPic_urls() != null ){
            mStatusPictures.setVisibility(View.VISIBLE);
            if (!mStatus.isLocal()){
                List<String> urls = new ArrayList<>();
                for (int m = 0;m < mStatus.getPic_urls().size();m++){
                    urls.add(mStatus.getBmiddle_pic()+mStatus.getPic_urls().get(m));
                }
                mStatusPictures.setUrlList(urls);
            }else {
                mStatusPictures.setUrlList(mStatus.getPic_urls());
            }

        }

        if (mStatus.getRetweeted_status() != null){
            mRepostStatusLayout.setVisibility(View.VISIBLE);
            final Status repostStatus = mStatus.getRetweeted_status();
            mRepostStatusUserName.setText("@"+repostStatus.getUser().getScreen_name());
            mRepostStatusUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(),UserCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User",repostStatus.getUser());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            if (repostStatus.getText().length() == 0){
                mRepostStatusText.setVisibility(View.GONE);
            }else {
                mRepostStatusText.setText(TextFilter.statusTextFliter(this, repostStatus.getText(), null));
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
        if (mStatus.getIsLike() == 1){
            mLikes.setSelected(true);
        }
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
                commentList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("comments");
                    for (int i =0 ;i<jsonArray.length();i++){
                        Comment comment = Comment.getCommentsFromJson(jsonArray.getString(i));
                        commentList.add(comment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (commentList.size() == 0){
                    List<LP_COMMENTS> lp_comments_list = LitePal.limit(Integer.valueOf(mStatus.getComments_count())).find(LP_COMMENTS.class);
                    for (LP_COMMENTS lp_comments : lp_comments_list){
                        commentList.add(Comment.getCommentsFromJson(lp_comments.getJson()));
                    }
                    Log.d(TAG, "onSuccess: commentSize:"+lp_comments_list.size());
                }

                Message message = new Message();
                message.what = GET_COMMENTS_FIRST;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 获取更多评论
     */
    private void getMoreStatus() {
        if (commentList.size() >= Integer.valueOf(mStatus.getComments_count())){
            return;
        }
        if (isSearching){
            Toast toast_search = Toast.makeText(this,null,Toast.LENGTH_SHORT);
            toast_search.setText("搜索界面暂不支持加载更多");
            toast_search.show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        ParamsOfComments paramsOfComments = null;
        if (commentList.size() != 0){
            paramsOfComments = new ParamsOfComments.Builder()
                    .access_token(accessToken.getToken())
                    .statusId(mStatus.getIdstr())
                    .max_id(commentList.get(commentList.size()-1).getIdstr())
                    .build();
        }
        if (paramsOfComments != null){
            WBApiConnector.getStatusComments(paramsOfComments, new HttpCallBack() {
                @Override
                public void onSuccess(String response) {
                    commentListCache.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("comments");
                        for (int i =1 ;i<jsonArray.length();i++){
                            Comment comment = Comment.getCommentsFromJson(jsonArray.getString(i));
                            commentListCache.add(comment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = GET_MORE_COMMENT_SUCCESS;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null){
            switch (requestCode){
                case REPOST_STATUS:
                    if (resultCode == RESULT_OK){
                        Bundle bundle = data.getExtras();
                        if (bundle != null){
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            setResult(RESULT_OK,intent);
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }

        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void findString(String str) {
        if (!isSearching){
            commentListCache.clear();
            commentListCache.addAll(commentList);
        }
        if (str == null || str.equals("")){
            commentList.clear();
            commentList.addAll(commentListCache);
            isSearching = false;
        }else {
            isSearching = true;
            List<Comment> comments = new ArrayList<>();
            for (Comment c : commentListCache){
                if (c.getText().contains(str)){
                    comments.add(c);
                }
            }
            commentList.clear();
            commentList.addAll(comments);
        }
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
