package com.zzy.minibo.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenterAlignImageSpan extends ImageSpan {

    public CenterAlignImageSpan(@NonNull Drawable drawable) {
        super(drawable);
    }

    public CenterAlignImageSpan(@Nullable Context context, int resourceId){
        super(context,resourceId);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent+y+fm.ascent)/2 - drawable.getBounds().bottom/2;
        canvas.save();
        canvas.translate(x,transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
