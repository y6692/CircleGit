package com.sylar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.sylar.app.BaseActivity;
import com.sylar.constant.CircleConstants;
import com.sylar.model.User;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.DealBusi;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;

import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;

public class MineCommonActivity extends BaseActivity {
    public static final String INTENT_URL = "INTENT_URL";
    private static final int REQUEST_CODE_SELECT = 100;

    @BindView(R.id.webView)
    DWebView webView;

    private CompletionHandler mHandler;
    private Context context;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ButterKnife.bind(this);
        context = this;
        ContextUtil.ctx=this;

        url = getIntent().getStringExtra(INTENT_URL);
        initWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ContextUtil.isback=0;
        ContextUtil.ctx = this;
    }

    @Override
    protected void onPause() {
        super.onPause();

        ContextUtil.isback=1;
    }

    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/h5/html/mine/" + url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    ArrayList<ImageItem> images = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                String base64Str = ImageUtil.compressAndBase64(images.get(0).path);
                VCard vcard= XmppConnection.getInstance().getUservcard(Constants.XMPP_USERNAME);
                vcard.setField("headimg", base64Str);
                XmppConnection.getInstance().changeVcard(vcard);
                mHandler.complete(base64Str);
            }
        }
    }

    // js调用原生的方法
    class JsApi {
        @JavascriptInterface
        void exitFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            CircleHelper.userManager().saveUserinfo(null);
            DealBusi busi = new DealBusi(context);
            busi.logout();
            startActivity(new Intent(context, LoginActivity.class));
            sendBroadcast(new Intent(CircleConstants.EXIT));
            finish();
        }
        @JavascriptInterface
        void changeUserInfoFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            String json = jsonObject.getString("userInfo");
            User user=JsonUtil.jsonToObject(json, User.class);
            VCard vcard= XmppConnection.getInstance().getUservcard(user.getUsername());
            vcard.setField("Name", user.getName());
            //vcard.setField("nickname", user.getName());
            XmppConnection.getInstance().changeVcard(vcard);
            Intent intent = new Intent(CircleConstants.FRIEND);
            sendBroadcast(intent);
            CircleHelper.userManager().saveUserinfo(JsonUtil.jsonToObject(json, User.class));
        }
        @JavascriptInterface
        void headTakePicFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            // 逻辑处理
            Intent intent = new Intent(context, ImageGridActivity.class);
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }

        @JavascriptInterface
        void headChoosePicFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            //打开选择,本次允许选择的数量
            Intent intent = new Intent(context, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//            intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }
        @JavascriptInterface
        void closePageFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            finish();
        }
    }
}