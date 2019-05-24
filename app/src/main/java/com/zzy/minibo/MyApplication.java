package com.zzy.minibo;

import android.app.Application;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import jackmego.com.jieba_android.JiebaSegmenter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        JiebaSegmenter.init(getApplicationContext());
    }

}
