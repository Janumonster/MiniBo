package com.zzy.minibo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.zzy.minibo.R;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.InvocationTargetException;

public class WebActivity extends BaseActivity {

    private static final String TAG = WebActivity.class.getSimpleName();

    private WebView mWebView;
    private WebSettings webSettings;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initView();
        initWebSetting();
    }

    private void initView() {
        mWebView = findViewById(R.id.web_body);
        progressBar = findViewById(R.id.web_progress);
        toolbar = findViewById(R.id.web_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSetting() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (url != null){
            mWebView.loadUrl(url);
        }
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(requiredStorage * 2);
                super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //加载进度
                Log.d(TAG, "onProgressChanged: "+newProgress);
                progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取标题
            }
        });

        webSettings = mWebView.getSettings();
        //设置支持JS，可以与页面中的js交互
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);//将图片调整到webview大小
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDisplayZoomControls(false);
        WebView.setWebContentsDebuggingEnabled(true);
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面的前提。
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片

    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        if (webSettings != null){
            webSettings.setJavaScriptEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mWebView.getClass().getMethod("onPause").invoke(mWebView,(Object[])null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webSettings != null){
            webSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        mWebView.clearCache(true);
    }
}
