package com.sylar.dao;

import android.telephony.IccOpenLogicalChannelResponse;
import android.util.Log;

import com.sylar.constant.Urls;
import com.sylar.model.User;
import com.sylar.model.apimodel.APIM_getMucManager;
import com.sylar.model.apimodel.APIM_getUserManager;
import com.sylar.model.apimodel.APIM_getUsers;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

/**
 * @author
 * @version 1.0
 * @date 2017/7/13
 */

public class GetHttpUserApplaction {
    public User mUser;

    public void getuser(String username){
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.GET_User, RequestMethod.GET);
        request.add("username", username);

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
            @Override
            public void onStart(int what) {
                super.onStart(what);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                // 请求成功。
                APIM_getUserManager result = JsonUtil.jsonToObject(response.get(), APIM_getUserManager.class);
                //100 ：成功 101：失败 102:输入错误
                if(result.getStatus() == 100){
                    mUser =result.getResults();
                }else{
                    ToastUtil.showMessage(result.getMessage());
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                ToastUtil.showMessage("网络请求失败");
            }

            @Override
            public void onFinish(int what) {
                super.onFinish(what);
            }
        };
        CallServer.getInstance().request(0, request, listener);
    }

    public void getusers(){
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.GET_Users, RequestMethod.POST);
        request.add("page", 1);
        request.add("rows", 20);

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
            @Override
            public void onStart(int what) {
                super.onStart(what);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                // 请求成功。
                APIM_getUsers result = JsonUtil.jsonToObject(response.get(), APIM_getUsers.class);
                //100 ：成功 101：失败 102:输入错误
                if(result.getStatus() == 100){
                    Log.e("getusers===", "===="+result.getResults().getRows().size());
                }else{
                    ToastUtil.showMessage(result.getMessage());
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                ToastUtil.showMessage("网络请求失败");
            }

            @Override
            public void onFinish(int what) {
                super.onFinish(what);
            }
        };
        CallServer.getInstance().request(0, request, listener);
    }
}
