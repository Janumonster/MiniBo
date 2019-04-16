package com.zzy.minibo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Members.LP_USER;
import com.zzy.minibo.Members.Status;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.MyViews.NineGlideView;
import com.zzy.minibo.R;
import com.zzy.minibo.Utils.KeyBoardManager;
import com.zzy.minibo.Utils.TextFilter;
import com.zzy.minibo.WBListener.SimpleIntCallback;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

public class StatusEditActivity extends BaseActivity {

    private static final String TAG = StatusEditActivity.class.getSimpleName();
    public static final int REPOST_FROM_MAIN = 0;
    public static final int REPOST_FROM_STATUS = 1;

    //
    private Toolbar toolbar;
    private EditText mEdittextView;
    private NineGlideView mNineGlideView;
    private ImageButton addPics;
    private ImageButton addEmotions;
    private ImageButton atFriends;
    private ImageButton statusSend;
    private CheckBox isCutPic;

    private List<LocalMedia> selectedList = new ArrayList<>();
    private List<String> selectedPath = new ArrayList<>();
    private Activity mActivity;

    private Oauth2AccessToken accessToken;
    private boolean isRepost = false;
    private Status repostStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_edit);
        accessToken = AccessTokenKeeper.readAccessToken(this);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.edit_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdittextView = findViewById(R.id.edit_edit_text);
        KeyBoardManager.showKeyBoard(this,mEdittextView);
        mNineGlideView = findViewById(R.id.edit_nine_pic_layout);
        mNineGlideView.setSimpleIntCallback(new SimpleIntCallback() {
            @Override
            public void callback(int i) {
                selectedPath.remove(i);
                selectedList.remove(i);
                mNineGlideView.setUrlList(selectedPath);
            }
        });
        isCutPic = findViewById(R.id.edit_cut_pic_check);
        addPics = findViewById(R.id.edit_add_pics);
        addPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCutPic.isChecked()){
                    PictureSelector.create(StatusEditActivity.this)
                            .openGallery(PictureMimeType.ofImage())//全部图片
                            .theme(R.style.picture_minibo_style)//默认主题
                            .maxSelectNum(9)//最多选9张
                            .imageSpanCount(4)//每行几张
                            .selectionMode(PictureConfig.MULTIPLE)//多选
                            .previewImage(true)//预览图片
                            .isCamera(true)//调用相机
                            .enableCrop(true)//允许裁剪
                            .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                            .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                            .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                            .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                            .isGif(true)//显示GIF
//                            .compress(true)// 是否压缩 true or false
                            .previewEggs(true)//滑动预览
                            .selectionMedia(selectedList)// 是否传入已选图片 List<LocalMedia> list;
                            .isDragFrame(true)// 是否可拖动裁剪框(固定)
                            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                }else {
                    PictureSelector.create(StatusEditActivity.this)
                            .openGallery(PictureMimeType.ofImage())//全部图片
                            .theme(R.style.picture_minibo_style)//默认主题
                            .maxSelectNum(9)//最多选9张
                            .imageSpanCount(4)//每行几张
                            .selectionMode(PictureConfig.MULTIPLE)//多选
                            .previewImage(true)//预览图片
                            .isCamera(true)//调用相机
                            .isGif(true)//显示GIF
//                            .compress(true)// 是否压缩 true or false
                            .previewEggs(true)//滑动预览
                            .selectionMedia(selectedList)// 是否传入已选图片 List<LocalMedia> list;
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                }

            }
        });
        addEmotions = findViewById(R.id.edit_add_emotions);
        addEmotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardManager.showKeyBoard(getBaseContext(),mEdittextView);
            }
        });
        atFriends = findViewById(R.id.edit_add_at);
        statusSend = findViewById(R.id.edit_send_btn);
        statusSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status status = new Status();
                User mUser = null;
                List<LP_USER> lp_users = LitePal.where("uidstr = ?",accessToken.getUid()).find(LP_USER.class);
                for (LP_USER l : lp_users){
                    mUser = User.makeJsonToUser(l.getJson());
                }
                status.setUser(mUser);
                status.setCreated_at(TextFilter.createTimeString());
                status.setText(mEdittextView.getText().toString());
                if (isRepost){
                    status.setRetweeted_status(repostStatus);
                }
                status.setReposts_count("0");
                status.setComments_count("0");
                status.setAttitudes_count("0");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PictureConfig.CHOOSE_REQUEST:
                    selectedList = PictureSelector.obtainMultipleResult(data);
                    selectedPath.clear();
                    for (LocalMedia localMedia : selectedList){
                        if (isCutPic.isChecked()){
                            selectedPath.add(localMedia.getCutPath());
                        }else {
                            selectedPath.add(localMedia.getPath());
                        }
                    }
                    mNineGlideView.setUrlList(selectedPath);
                    break;
            }
        }
    }



}
