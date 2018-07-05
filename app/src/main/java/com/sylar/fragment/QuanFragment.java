package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.sylar.activity.QuanCommonActivity;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.H5NativeApis;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.ucmlmobile.AddRoomActivity;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.SearchRoomActivity;
import com.sylar.unit.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

import static android.app.Activity.RESULT_OK;

/**
 * 圈子
 * created by Djy
 * 2017/6/14 8:38
 */
public class QuanFragment extends BaseFragment {

    public static final String TAG = QuanFragment.class.getSimpleName();
    private static final int ADD_QUAN = 88;
    private static final int ENTER_QUAN = 99;

    Unbinder unbinder;
    @BindView(R.id.webView)
    DWebView webView;

    private String mTitle = "圈子";
    private CompletionHandler mHandler;
    private Context context;
    private boolean hasLoadUrl;

    public static QuanFragment getInstance(String title) {
        QuanFragment sf = new QuanFragment();
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
        Log.e(TAG, "onActivityCreated");
        init();
        initWebView();
    }

    /**
     * 初始化
     */
    private void init() {
        context = getActivity();
        registerReceiver(new String[]{CircleConstants.REFRESH_QUAN});
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());

        // 圈设置里面删除了圈子
        if (CircleConstants.REFRESH_QUAN.equals(intent.getAction())) {
            webView.callHandler(H5NativeApis.REFRESH_QUAN_FROM_NATIVE,new Object[]{},new OnReturnValue(){
                @Override
                public void onValue(String retValue) {
                    Log.d("jsbridge","call succeed,return value is "+retValue);
                }
            });
        }
    }

    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/h5/build/quanzi.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissPd();
            }
        });
    }

    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net() {
        ContextUtil.curtab = 2;

        if(!hasLoadUrl){
            getActivity().sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
            hasLoadUrl = true;
        }
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
        void createQuanFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            Intent i = new Intent(context, AddRoomActivity.class);
            startActivityForResult(i, ADD_QUAN);
        }
        @JavascriptInterface
        void enterQuanFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            Intent i = new Intent(context, SearchRoomActivity.class);
            startActivityForResult(i, ENTER_QUAN);
        }
        @JavascriptInterface
        void newQuanCommonWebFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            String url = jsonObject.getString("url");
            String json = jsonObject.getString("quan");

            Intent i = new Intent(context, QuanCommonActivity.class);
            i.putExtra(QuanCommonActivity.INTENT_URL, url);
            i.putExtra(QuanCommonActivity.INTENT_QUAN, json);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_QUAN || requestCode == ENTER_QUAN){
                CommonResult result = new CommonResult();
                result.setStatus(100);
                mHandler.complete(JsonUtil.objectToJson(result));
            }
        }
    }
}