package com.sylar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.sylar.app.BaseActivity;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.H5NativeApis;
import com.sylar.constant.Urls;
import com.sylar.model.ChatItem;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class QuanCommonActivity extends BaseActivity {

    private static final int REQ_ADD_DONGTAI = 100;
    public static final String INTENT_URL = "INTENT_URL";
    public static final String INTENT_QUAN = "INTENT_QUAN";

    @BindView(R.id.webView)
    DWebView webView;

    private CompletionHandler mHandler;
    private Context context;
    private String url;
    private String quanJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ButterKnife.bind(this);
        context = this;
        ContextUtil.ctx=this;

        url = getIntent().getStringExtra(INTENT_URL);
        quanJson = getIntent().getStringExtra(INTENT_QUAN);
        initWebView();
        registerReceiver(new String[]{CircleConstants.QUAN_GO_HOME});
    }


    @Override
    protected void onResume() {
        super.onResume();

        webView.callHandler(H5NativeApis.REFRESH_QUAN_CHENGYUAN_FROM_NATIVE,new Object[]{},new OnReturnValue(){
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge","call succeed,return value is "+retValue);
            }
        });
    }



    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/h5/build/" + url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!StringUtil.isBlank(quanJson)){
                    webView.callHandler(H5NativeApis.SEND_QUAN_FROM_NATIVE,new Object[]{quanJson},new OnReturnValue(){
                        @Override
                        public void onValue(String retValue) {
                            Log.e("jsbridge===","call succeed,return value is "+retValue);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());

        if (CircleConstants.QUAN_GO_HOME.equals(intent.getAction())) {
            finish();
        }
    }

    // js调用原生的方法
    class JsApi {
        @JavascriptInterface
        void addDongtaiFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            int circlenum = jsonObject.getInt("circlenum");
            String circlename = jsonObject.getString("circlename");
            Intent i = new Intent(context, AddDongtaiActivity.class);
            i.putExtra(AddDongtaiActivity.REQ_TYPE, 1);
            i.putExtra(AddDongtaiActivity.REQ_CIRCLE_ID, circlenum);
            i.putExtra(AddDongtaiActivity.REQ_CIRCLE_NAME, circlename);
            startActivityForResult(i, REQ_ADD_DONGTAI);
        }
        @JavascriptInterface
        void chatFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            Intent i = new Intent(context, NewChatActivity.class);
            i.putExtra("SessionID", jsonObject.getString("name"));
            i.putExtra("chatType", ChatItem.GROUP_CHAT);

            if(!StringUtil.isBlank(jsonObject.getString("name")))
                startActivity(i);
        }

        @JavascriptInterface
        void closePageFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            finish();
        }
        @JavascriptInterface
        void showBigImgsFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            String imgs = jsonObject.getString("imgs").replace("\\", "");
            int index = jsonObject.getInt("index");
            ImageUtil.browserPics(imgs, context, index);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_ADD_DONGTAI){
                CommonResult result = new CommonResult();
                result.setStatus(100);
                mHandler.complete(JsonUtil.objectToJson(result));
            }
        }
    }
}
