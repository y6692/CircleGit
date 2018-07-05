package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.sylar.activity.LoginActivity;
import com.sylar.activity.MineCommonActivity;
import com.sylar.activity.QuanCommonActivity;
import com.sylar.app.BaseFragment;
import com.sylar.constant.H5NativeApis;
import com.sylar.model.Image;
import com.sylar.model.User;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.DealBusi;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

/**
 * 我的
 * created by Djy
 * 2017/6/15 1:00
 */
public class MineFragment extends BaseFragment {
    public static final String TAG = MineFragment.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.webView)
    DWebView webView;

    private String mTitle;
    private Context context;

    public static MineFragment getInstance(String title) {
        MineFragment sf = new MineFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.web_layout, null);
        unbinder = ButterKnife.bind(this, v);
        ContextUtil.ctx = getActivity();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initWebView();
    }

    private void init() {
        context = getActivity();
    }

    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/h5/mines.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.reload();
    }

    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net() {
        ContextUtil.curtab = 4;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // show自己页面时，才去网络操作
        if (!hidden) {
            net();
            Log.i("circle", "show" + mTitle);
        } else
            Log.i("circle", "hidden" + mTitle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // js调用原生的方法
    class JsApi {
        @JavascriptInterface
        void mineSettingFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            String url = jsonObject.getString("url");
            Intent i = new Intent(context, MineCommonActivity.class);
            i.putExtra(MineCommonActivity.INTENT_URL, url);
            context.startActivity(i);
        }
    }
}