package com.sylar.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.webkit.JavascriptInterface;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.sylar.constant.CircleConstants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import wendu.dsbridge.DWebView;

public class BaseFragment extends Fragment {
    public LoadingDialog loadingDialog;
    private InternalReceiver internalReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setMessageText("数据加载...");
    }

    protected void showPd() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }
    protected void showUncanclePd() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }


    protected void dismissPd() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        getActivity().registerReceiver(internalReceiver, intentfilter);
    }

    // Internal calss.
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            handleReceiver(context, intent);
        }
    }

    /**
     * 如果子界面需要拦截处理注册的广播 需要实现该方法
     *
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
        if (intent == null) {
            return;
        }
    }

    @Override
    public void onDestroyView() {
        try {
            if (internalReceiver != null) {
                getActivity().unregisterReceiver(internalReceiver);
            }
        } catch (Exception e) {
        }

        super.onDestroyView();
    }

//    public boolean onBack( DWebView webView){
//        if (webView.canGoBack()) {  //表示按返回键
//            if(!webView.canGoBackOrForward(-2)){
//                getActivity().sendBroadcast(new Intent(CircleConstants.SHOW_TAB_BAR_FROM_JS));
//            }
//            webView.goBack();   //后退
//            return false;
//        }else{
//            return true;
//        }
//    }
}
