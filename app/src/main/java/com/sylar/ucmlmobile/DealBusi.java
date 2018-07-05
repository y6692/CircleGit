package com.sylar.ucmlmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sylar.fragment.CircleFragment;
import com.sylar.fragment.MsgFragment;
import com.sylar.model.Room;
import com.sylar.unit.Util;

import org.jivesoftware.smackx.packet.VCard;

public class DealBusi {

	ProgressDialog dialog;

	public DealBusi(Context context, String username, String password) {
		Constants.CONTEXT = (Activity) context;
		String url = ConfigInfo.getReqHost(ContextUtil.getInstance());
		Constants.XMPP_HOST = url.split("//")[1].split("/")[0].split(":")[0];
		Constants.XMPP_HOSTNAME = Constants.XMPP_HOST;
		Constants.XMPP_USERNAME = username;
		Constants.XMPP_PASSWORD = password;
	}

	public DealBusi(Context context, String username, String password, String server){
		Constants.CONTEXT = (Activity) context;
		Constants.XMPP_USERNAME = username;
		Constants.XMPP_PASSWORD = password;
		
		if(server.isEmpty() || server == "223.112.193.38") {
			String url = ConfigInfo.getReqHost(ContextUtil.getInstance());
			Constants.XMPP_HOST = url.split("//")[1].split("/")[0].split(":")[0];
		}else {
			Constants.XMPP_HOST = server;
		}
		Constants.XMPP_HOSTNAME = Constants.XMPP_HOST;
	}
	
	public DealBusi(Context context) {
		Constants.CONTEXT = (Activity) context;
	}
	
	public boolean login() {
		boolean isSuccess = XmppConnection.getInstance().login(Constants.XMPP_USERNAME, Constants.XMPP_PASSWORD);
		if (isSuccess) {
			VCard vcard = new VCard();
			vcard.setField("Alias", Constants.XMPP_USERNAME);
			vcard.setField("LastSource", Util.brand);
			vcard.setField("serialNum",Constants.serialNum);

			if(Util.brand.equals("Huawei")||Util.brand.equals("HONOR")){
				vcard.setField("token", Constants.access_token);
			}else{
				vcard.setField("token", "");
			}
			XmppConnection.getInstance().changeVcard(vcard);
			return true;
		}else{
			return false;	
		}
	}
	

//	private void getusers() {
//		 String manList=ContextUtil.manList;
//
//		Log.e("manList====", "===="+manList);
//
//		if(manList!=null)
//		{
//			JSONArray arr = new JSONArray();
//			try {
//				arr = new JSONArray(manList);
//			}catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			if(arr != null) {
//				for(int i=0;i<arr.length();i++) {
//					JSONObject jObjUser = new JSONObject();
//					try {
//						jObjUser = arr.getJSONObject(i);
//						String oid = jObjUser.getString("oid");
//
//
//
//					} catch(JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
	
	public void logout() {
		MsgFragment.flag=1;
		CircleFragment.flag=1;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		XmppConnection.myRooms.clear();
		exit();
		stopService();
		XmppConnection.getInstance().closeConnection();
		NotificationManager nm = (NotificationManager) Constants.CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
	
	public void regist() {
	}
	
	public void exit() {
		XmppConnection.getInstance().closeConn();
	}
	
	private void startService() {
		Intent chatService = new Intent(Constants.CONTEXT, XmppService.class);
		Constants.CONTEXT.startService(chatService);
	}
	
	private void stopService() {		
		Intent chatService = new Intent(Constants.CONTEXT, XmppService.class);
		Constants.CONTEXT.stopService(chatService);
	}

	
	@SuppressLint("HandlerLeak")
	private Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.LOGIN_ERROR:
				Toast.makeText(Constants.CONTEXT, "登录XMPP失败", Toast.LENGTH_SHORT).show();
				break;
			case Constants.LOGIN_ERROR_NET:
				Toast.makeText(Constants.CONTEXT, "连接XMPP服务器失败", Toast.LENGTH_SHORT).show();
				break;
			case Constants.LOGIN_ERROR_PWD:
				Toast.makeText(Constants.CONTEXT, "密码错误或账号不存在", Toast.LENGTH_SHORT).show();
				break;
			case Constants.LOGIN_ERROR_REPEAT:
				Toast.makeText(Constants.CONTEXT, "重复登录", Toast.LENGTH_SHORT).show();
				break;
			case 200:
				startService();
				break;
			case Constants.DIALOG_SHOW:
				dialog = new ProgressDialog(Constants.CONTEXT);
				dialog.setMessage("Loading");
				dialog.show();
				break;
			case Constants.DIALOG_CANCEL:
				if(dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				break;
			}
		} 
	};
	
	@SuppressLint("HandlerLeak")
	private Handler registHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.ERROR_CONN:
				Toast.makeText(Constants.CONTEXT, "连接服务器失败", Toast.LENGTH_SHORT).show();
				break;
			case Constants.ERROR_REGISTER:
				Toast.makeText(Constants.CONTEXT, "注册失败", Toast.LENGTH_SHORT).show();
				break;
			case Constants.ERROR_REGISTER_REPEATUSER:
				Toast.makeText(Constants.CONTEXT, "账号已存在", Toast.LENGTH_SHORT).show();
				break;
			case Constants.SUCCESS:
				Toast.makeText(Constants.CONTEXT, "注册成功", Toast.LENGTH_SHORT).show();
				login();
				break;
			}
		}
	};
}
