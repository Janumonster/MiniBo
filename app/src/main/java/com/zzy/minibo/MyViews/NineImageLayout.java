package com.zzy.minibo.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzy.minibo.R;

import java.util.ArrayList;
import java.util.List;

public abstract class NineImageLayout extends ViewGroup {

    //默认间距
    private static final float DEFULT_SPACING = 3f;
    //默认最多张数
    private static final int MAX_COUNT = 9;

    protected Context mContext;
    private float mSpacing = DEFULT_SPACING;
    private int mColums;//列数
    private int mRows;//行数
    private int mTotalWidh;//控件总宽度
    private int mSingleWidth;//单张图片宽度

    private boolean mIsShowAll = false;//是否显示全部图片
    private boolean mIsFirst = true;
    //图片列表
    private List<String> mUrlList = new ArrayList<>();

    public NineImageLayout(Context context) {
        super(context);
        init(context);
    }

    public NineImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineImageLayout);
        mSpacing = typedArray.getDimension(R.styleable.NineImageLayout_mSpacing,DEFULT_SPACING);
        typedArray.recycle();
        init(context);
    }

    public NineImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        mContext = context;
        if (getListSize(mUrlList) == 0){
            setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom){
        mTotalWidh = right - left;
        mSingleWidth = (int) ((mTotalWidh-mSpacing*(3-1))/3);
        if (mIsFirst){
            notifyDataSetChanged();
            mIsFirst = false;
        }
    }

    /**
     * 数据发生变化时调用
     */
    public void notifyDataSetChanged() {
        removeAllViews();
        int size = getListSize(mUrlList);
        if (size > 0){
            setVisibility(VISIBLE);
        }else {
            setVisibility(GONE);
        }

        if (size == 1){
            String url = mUrlList.get(0);
            ClickImageView clickImageView = createClickIamge(0,url);

            LayoutParams params = getLayoutParams();
            params.height = mSingleWidth;
            setLayoutParams(params);
            clickImageView.layout(0, 0, mSingleWidth, mSingleWidth);

            boolean isShowDefult = displayOneImage(clickImageView,url,mTotalWidh);
            if (isShowDefult){
                layoutImageView(clickImageView,0,url,false);
            }else {
                addView(clickImageView);
            }
            return;
        }

        generateChlidrenLayout(size);
        layoutParams();

        for (int i = 0 ;i < size; i++){
            String url = mUrlList.get(i);
            Log.d("LOG", "notifyDataSetChanged: "+url);
            ClickImageView clickImageView;
            if (!mIsShowAll){
                if (i < MAX_COUNT - 1){
                    clickImageView = createClickIamge(i,url);
                    layoutImageView(clickImageView,i,url,false);
                }else {
                    if (size <= MAX_COUNT){
                        clickImageView = createClickIamge(i,url);
                        layoutImageView(clickImageView,i,url,false);
                    }else {
                        clickImageView = createClickIamge(i,url);
                        layoutImageView(clickImageView,i,url,true);
                        break;
                    }
                }
            }else {
                clickImageView = createClickIamge(i,url);
                layoutImageView(clickImageView,i,url,false);
            }
        }
    }

    /**
     * 根据View的数量来确定控件的高度
     */
    private void layoutParams() {
        int singleHeight = mSingleWidth;

        LayoutParams params = getLayoutParams();
        params.height = (int) (singleHeight * mRows + mSpacing * (mRows -1));
        setLayoutParams(params);
    }

    /**
     * 确定 行数 和 列数
     * @param size 图片数量
     */
    private void generateChlidrenLayout(int size) {
        if (size <= 3){
            mRows = 1;
            mColums = size;
        }else if (size <= 6){
            mRows = 2;
            mColums = 3;
            if (size == 4){
                mColums = 2;
            }
        }else {
            mColums = 3;
            if (mIsShowAll){
                mRows = size / 3;
                int b = size % 3;
                if (b > 0){
                    mRows++;
                }
            }else {
                mRows = 3;
            }
        }
    }

    /**
     * 设置目标控件的位置，并添加到viewgroup中，确定是否显示多于9张的文字
     * @param imageView 目标控件
     * @param i position
     * @param url address
     * @param showNumFlag 是否显示文字
     */
    private void layoutImageView(ClickImageView imageView, int i, String url, boolean showNumFlag) {
        final int singleWidth = (int) ((mTotalWidh-mSpacing*(3-1))/3);
        int singleHeight = singleWidth;

        int[] postion = findPostion(i);
        Log.d("LOG", "layoutImageView: "+postion[0]+" "+postion[1]);
        int left = (int) ((singleWidth + mSpacing)*postion[1]);
        int top = (int) ((singleHeight + mSpacing)*postion[0]);
        int right = left + singleWidth;
        int bottom = top + singleHeight;

        imageView.layout(left,top,right,bottom);

        addView(imageView);
        if (showNumFlag){
            int overCount = getListSize(mUrlList)- MAX_COUNT;
            if (overCount > 0){
                float textSize = 30;
                final TextView textView = new TextView(mContext);
                textView.setText("+"+String.valueOf(overCount));
                textView.setTextColor(Color.WHITE);
                textView.setPadding(0,singleHeight/2 -getFontHeight(textSize),0,0);
                textView.setTextSize(textSize);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(Color.BLACK);
                textView.getBackground().setAlpha(120);

                textView.layout(left,top,right,bottom);
                addView(textView);
            }
        }
        displayImage(imageView,url);
    }

    private int getFontHeight(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
    }

    /**
     * 根据位置判断出imageview的偏移量
     * @param num position
     * @return top和left的偏移数量级
     */
    private int[] findPostion(int num) {
        int[] postion = new int[2];
        for (int i = 0 ; i < mRows ; i++){
            for (int j = 0 ; j < mColums ; j++){
                if ((i * mColums + j) == num){
                    postion[0] = i;//行
                    postion[1] = j;//列
                    break;
                }
            }
        }
        return postion;
    }

    /**
     * 设置一张图片的位置
     * @param clickImageView 目标控件
     * @param width 宽度
     * @param height 高度
     */
    protected void setOneImageLayout(ClickImageView clickImageView,int width,int height){
        clickImageView.setLayoutParams(new LayoutParams(width,height));
        clickImageView.layout(0,0,width,height);

        LayoutParams params = getLayoutParams();
//        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    /**
     * 用于创建ClickIamgeVIew，并未其添加点击监听
     * @param i 图片序号
     * @param url 图片地址
     * @return clickimageview
     */
    private ClickImageView createClickIamge(final int i, final String url) {
        ClickImageView clickImageView = new ClickImageView(mContext);
        clickImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        clickImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(i,url,mUrlList);
            }
        });
        return clickImageView;
    }

    /**
     * 返回图片列表的长度
     * @param mUrlList 图片地址列表
     * @return int
     */
    private int getListSize(List<String> mUrlList) {
        if (mUrlList == null || mUrlList.size() == 0){
            return 0;
        }
        return mUrlList.size();
    }

    public void setSpacing(float spacing){
        mSpacing = spacing;
    }

    public void setIsShowAll(boolean isShoeAll){
        mIsShowAll = isShoeAll;
    }

    public void setUrlList(List<String> urlList){
        if (getListSize(urlList) == 0){
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        mUrlList.clear();
        mUrlList.addAll(urlList);

        if (!mIsFirst){
            notifyDataSetChanged();
        }
    }

    protected abstract boolean displayOneImage(ClickImageView imageView, String url, int parentWidth);
    protected abstract void displayImage(ClickImageView imageView,String url);
    protected abstract void onClickImage(int postion,String url,List<String> urlList);
}
