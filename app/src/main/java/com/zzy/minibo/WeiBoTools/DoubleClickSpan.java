package com.zzy.minibo.WeiBoTools;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DoubleClickSpan extends ClickableSpan {

    private String str;

    private String msg;

    private Context mContext ;

    public DoubleClickSpan(Context context, String string,String message){
        this.mContext = context;
        this.str = string;
        this.msg = message;
    }

    @Override
    public void onClick(@NonNull View widget) {
        if(widget  instanceof TextView){
            ((TextView)widget).setHighlightColor(Color.TRANSPARENT);
        }

        Toast.makeText(mContext, str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(0xff507daf);
        ds.setUnderlineText(false);
    }
}
