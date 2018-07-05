package com.example.xmpp;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import model.ChatItem;

/**
 * 创建日期：2018/6/25
 * 作者： yy
 */

public class XMPP {
    public static void setNotifyClass(Context ctx, Class notifyClass){
        XmppConnection.getInstance(ctx).setNotifyClass(notifyClass);
    }

    public static void setRecevier(Context ctx, String chatName, int chatType){
        XmppConnection.getInstance(ctx).setRecevier(chatName, chatType);
    }

    public static void sendMsg(Context ctx, String msg, int chatType){
        try{
            JSONObject obj = new JSONObject();
            obj.put("msg", msg);
            obj.put("bodyType", "0");
            XmppConnection.getInstance(ctx).sendMsg(ctx, obj.toString(), chatType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loginAccount(final Context ctx, final String userName, final String password, XmppLoadThread.OnTaskListener xmppLoad) {

        new XmppLoadThread(ctx, xmppLoad){

            @Override
            protected Object load() {
                String url = ConfigInfo.getReqHost(ctx);
                Constants.XMPP_HOST = url.split("//")[1].split("/")[0].split(":")[0];
                Constants.XMPP_HOSTNAME = Constants.XMPP_HOST;

                Log.e("loginAccount====", userName+"===");

                boolean isSuccess = XmppConnection.getInstance(ctx).login(userName, password);
                if (isSuccess) {
                    Constants.XMPP_USERNAME = userName;
                    Constants.XMPP_PASSWORD = password;
                    Constants.XMPP_NICKNAME = MessageConstants.getNameById(ctx, userName);
                }
                return isSuccess;
            }
        };
    }

}
