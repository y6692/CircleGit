package com.sylar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.nanchen.compresshelper.CompressHelper;
import com.sylar.model.Image;
import com.sylar.ucmlmobile.BaseActivity;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;

public class TestActivity extends BaseActivity {
    @BindView(R.id.webView)
    DWebView webView;

    private CompletionHandler mHandler;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ButterKnife.bind(this);
        ContextUtil.ctx=this;

        init();
        initWebView();
    }

    private void init() {
        context = this;
    }

    private void initWebView() {
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.getSettings().setDomStorageEnabled(true);
        // 解决An attempt was made to break through the security policy of the user agent
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.loadUrl("file:///android_asset/h5/build/dongtai.html");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }


    // js调用原生的方法
    class JsApi {
        @JavascriptInterface
        void testReact(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            String msg = jsonObject.getString("msg");
            ToastUtil.showMessage(msg);

            mHandler.complete("hello u");
        }
    }
}
