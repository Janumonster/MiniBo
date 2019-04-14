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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zzy.minibo.R;

import androidx.appcompat.widget.Toolbar;

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
        webSettings = mWebView.getSettings();
        //设置支持JS，可以与页面中的js交互
        webSettings.setJavaScriptEnabled(true);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);//将图片调整到webview大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
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
