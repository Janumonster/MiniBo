package com.zzy.minibo.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.zzy.minibo.R;

public class ClickImageView extends android.support.v7.widget.AppCompatImageView {



    //宽高比例
    private float mRatio = 0f;

    public ClickImageView(Context context) {
        super(context);
    }

    public ClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClickImageView);
        mRatio = typedArray.getFloat(R.styleable.ClickImageView_mRatio,0f);
        typedArray.recycle();
    }

    public ClickImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置Image的宽高比
     * @param mRatio
     */
    public void setmRatio(float mRatio) {
        this.mRatio = mRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0){
            float height = width/mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)height,MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getDrawable();
                if (drawable != null){
                    drawable.mutate().setColorFilter(Color.parseColor("#A1F2F3F5"), PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null){
                    drawableUp.mutate().clearColorFilter();
                }
        }
        return super.onTouchEvent(event);
    }
}
