package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.administrator.circlegit.R;
import com.google.zxing.common.StringUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.sylar.activity.AddDongtaiActivity;
import com.sylar.activity.LoginActivity;
import com.sylar.activity.TestActivity;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.H5NativeApis;
import com.sylar.constant.Urls;
import com.sylar.model.ChatItem;
import com.sylar.model.apimodel.APIM_uploadFile;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.RecorderActivity;
import com.sylar.ucmlmobile.SearchFriendActivity;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.view.LibraryCommonDialog;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

import static android.app.Activity.RESULT_OK;
import static com.sylar.activity.AddDongtaiActivity.REQUEST_CODE_SELECT;

/**
 * 动态
 * created by Djy
 * 2017/6/14 8:38
 */
public class DynamicFragment extends BaseFragment {

    public static final String TAG = DynamicFragment.class.getSimpleName();
    private static final int REQ_ADD_DONGTAI = 88;

    Unbinder unbinder;
    @BindView(R.id.webView)
    DWebView webView;

    private String mTitle;
    private CompletionHandler mHandler;
    private Context context;
    private boolean hasLoadUrl;

    public static DynamicFragment getInstance(String title) {
        DynamicFragment sf = new DynamicFragment();
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
        registerReceiver(new String[]{CircleConstants.REFRESH_DONGTAI});
    }

    private void initWebView() {
        // 使用通用型bridge 实现js与ios android交互
        webView.setJavascriptInterface(new JsApi());
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/h5/build/dongtai.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("initWebView>>>>", "=====" +url);
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());

        // 圈设置里面删除了圈子
        if (CircleConstants.REFRESH_DONGTAI.equals(intent.getAction())) {
            Log.e("djy===", "要刷新");
            webView.callHandler(H5NativeApis.REFRESH_DONGTAI_FROM_NATIVE,new Object[]{},new OnReturnValue(){
                @Override
                public void onValue(String retValue) {
                    Log.d("jsbridge","call succeed,return value is "+retValue);
                }
            });
        }
    }


    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net() {
        ContextUtil.curtab = 1;

        if(!hasLoadUrl){
            hasLoadUrl = true;
            webView.reload();
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
        void addDongtaiFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            mHandler = handler;
            showMenu();
        }
        @JavascriptInterface
        void chatFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {

            String name = jsonObject.getString("name");
            int type = jsonObject.getInt("type");
            Intent i = new Intent(context, NewChatActivity.class);
            i.putExtra("SessionID", name);
            if(type == 1)
                i.putExtra("chatType", ChatItem.GROUP_CHAT);
            startActivity(i);
        }
        @JavascriptInterface
        void showBigImgsFromJs(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            String imgs = jsonObject.getString("imgs").replace("\\", "");
            int index = jsonObject.getInt("index");
            ImageUtil.browserPics(imgs, getActivity(), index);
        }
    }

    protected void showMenu() {

        LibraryCommonDialog.addDongtaiMenuDialog(context, new LibraryCommonDialog.AddDongtaiMenuCallback() {
            @Override public void onVideo() {
                Intent intent = new Intent(context, RecorderActivity.class);
                startActivityForResult(intent, 100);
            }

            @Override public void onPic() {
//                imagePicker.setSelectLimit(maxImgCount - selImageList.size());
                Intent intent1 = new Intent(context, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
                startActivityForResult(intent1, REQUEST_CODE_SELECT);

            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_ADD_DONGTAI){
                CommonResult result = new CommonResult();
                result.setStatus(100);
                mHandler.complete(JsonUtil.objectToJson(result));
            }else if(requestCode == 100){
                int timecount = data.getIntExtra("timecount", 0);
                String imgName = data.getStringExtra("name");
                String path = data.getStringExtra("path");

                if (imgName != null) {
                    try {
                        File newFile = new File(path);
                        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.UPLOAD_VIDEO, RequestMethod.POST);
                        request.add("filename", new FileBinary(newFile));

                        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
                            @Override public void onStart(int what) {
                                super.onStart(what);
                            }

                            @Override public void onSucceed(int what, Response<String> response) {
                                super.onSucceed(what, response);
                                APIM_uploadFile result = JsonUtil.jsonToObject(response.get(), APIM_uploadFile.class);
                                if (result.getStatus() == 100) {
                                    //重新设置用户信息
                                    Intent intent = new Intent(context, AddDongtaiActivity.class);
                                    intent.putExtra("imageUrl", result.getResults());
                                    startActivityForResult(intent, REQ_ADD_DONGTAI);
                                } else {
                                    ToastUtil.showMessage(result.getMessage());
                                }
                            }

                            @Override public void onFailed(int what, Response<String> response) {
                                super.onFailed(what, response);
                                ToastUtil.showMessage("上传失败");

                            }

                            @Override public void onFinish(int what) {
                                super.onFinish(what);
                            }
                        };
                        CallServer.getInstance().request(0, request, listener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                ocr_vehicle(images.get(0).path);
//
//                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                if (images != null) {
//                    selImageList.addAll(images);
//                    adapter.setImages(selImageList);
//                }

                Intent intent = new Intent(context, AddDongtaiActivity.class);
                intent.putExtra("images", images);

                startActivityForResult(intent, REQ_ADD_DONGTAI);
            }
        }

    }

//    public boolean onBack(){
//        return onBack(webView);
//    }


//    @OnClick({R.id.iv_right, R.id.ll_bj, R.id.ll_jhy, R.id.ll_fdt})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_right:
//                llbj.setVisibility(View.VISIBLE);
//                lladd.setVisibility(View.VISIBLE);
//
//                break;
//
//            case R.id.ll_jhy:
//                llbj.setVisibility(View.GONE);
//                lladd.setVisibility(View.GONE);
//
//                Intent i = new Intent(getActivity(), SearchFriendActivity.class);
//                startActivity(i);
//
//                break;
//
//            case R.id.ll_fdt:
//                llbj.setVisibility(View.GONE);
//                lladd.setVisibility(View.GONE);
//
//                startActivityForResult(new Intent(getActivity(), AddDongtaiActivity.class), 88);
//
//                break;
//
//            case R.id.ll_bj:
//
//                llbj.setVisibility(View.GONE);
//                lladd.setVisibility(View.GONE);
//                break;
//
//        }
//    }
//
}