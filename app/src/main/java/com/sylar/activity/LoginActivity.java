package com.sylar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.sylar.app.BaseActivity;
import com.sylar.constant.Urls;
import com.sylar.model.User;
import com.sylar.model.apimodel.APIM_getCircles;
import com.sylar.model.apimodel.APIM_getUserManager;
import com.sylar.model.apimodel.APIM_getUsers;
import com.sylar.model.apimodel.APIM_login;
import com.sylar.ucmlmobile.ConfigInfo;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.XmppLoadThread;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.webView)
    DWebView webView;

    private static final int REQUEST_CODE_SELECT = 100;
    private CompletionHandler mHandler;
    private Context context;
    public static Roster roster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ButterKnife.bind(this);
        ContextUtil.ctx = this;
        context = this;

        if(CircleHelper.userManager().getUserinfo() != null){
//            VarCard varCard = CircleHelper.userManager().getUserinfo().getVarCardObj();
//            loginAccount(CircleHelper.userManager().getUserinfo().getUsername(), CircleHelper.userManager().getUserinfo().getPlainPassword());

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }


        initImagePicker(true);
        initWebView();
    }


    private void initWebView(){
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/h5/html/mine/login.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // TODO: 2017/6/16 0016
            }
        });
    }

    class JsApi {
        @JavascriptInterface
        void loginSuccessFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            // 逻辑处理
            String json = jsonObject.getString("userInfo");
            CircleHelper.userManager().saveUserinfo(JsonUtil.jsonToObject(json, User.class));

            Log.e("user===", JsonUtil.jsonToObject(json, User.class).getUsername()+"==="+JsonUtil.jsonToObject(json, User.class).getPlainPassword());

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
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
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }
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
                mHandler.complete(base64Str);
            }
        }
    }

    public static List<String> getConferenceServices(String server, XMPPConnection connection) throws Exception {
        List<String> answer = new ArrayList<String>();
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);

        DiscoverItems items = discoManager.discoverItems(server);
        for (Iterator<DiscoverItems.Item> it = items.getItems(); it.hasNext(); ) {
            DiscoverItems.Item item = (DiscoverItems.Item) it.next();
            if (item.getEntityID().startsWith("conference") || item.getEntityID().startsWith("private")) {
                answer.add(item.getEntityID());
            } else {
                try {
                    DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
                    if (info.containsFeature("http://jabber.org/protocol/muc")) {
                        answer.add(item.getEntityID());
                    }
                } catch (XMPPException e) {
                }
            }
        }
        return answer;
    }
   
}
