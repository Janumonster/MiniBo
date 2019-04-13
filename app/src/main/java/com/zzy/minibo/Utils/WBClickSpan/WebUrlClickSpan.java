package com.zzy.minibo.Utils.WBClickSpan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzy.minibo.Activities.WebActivity;
import com.zzy.minibo.R;

import androidx.annotation.NonNull;

public class WebUrlClickSpan extends ClickableSpan {

    private String str;

    private Context mContext ;

    public WebUrlClickSpan(Context context, String string){
        this.mContext = context;
        this.str = string;
    }

    @Override
    public void onClick(@NonNull View widget) {
        if(widget  instanceof TextView){
            ((TextView)widget).setHighlightColor(Color.TRANSPARENT);
        }
        Intent intent = new Intent(mContext, WebActivity.class);
        intent.putExtra("url",str);
        mContext.startActivity(intent);
        Toast.makeText(mContext, str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mContext.getResources().getColor(R.color.colorMain));
        ds.setUnderlineText(false);
    }
}
