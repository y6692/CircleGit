package com.sylar.ucmlmobile;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.administrator.circlegit.MainActivity;
import com.huawei.android.pushagent.PushManager;
import com.lzy.imagepicker.ImagePicker;
import com.sylar.Receiver.PustDemoActivity;
import com.sylar.activity.HomeActivity;
import com.sylar.activity.LoginActivity;
import com.sylar.app.CrashHandler;
import com.sylar.constant.CircleConstants;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.fragment.CircleFragment;
import com.sylar.fragment.MsgFragment;
import com.sylar.model.ChatItem;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.GlideImageLoader;
import com.sylar.unit.ImgConfig;
import com.sylar.unit.Tool;
import com.sylar.unit.Util;
import com.sylar.unit.XmppLoadThread;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import static com.sylar.ucmlmobile.XmppConnection.getFullUsername;


public class ContextUtil extends MultiDexApplication implements PacketListener {
	protected static final String TAG = "提示";
	public static ContextUtil instance;
	public static SharedPreferences sharedPreferences;
	public static String manList = null;
	public static Context ctx;
	private static ImagePicker imagePicker;
	public static boolean isLeaving = false;
	public static int f=0;
	public static int curtab=0;
	private static PustDemoActivity mPustTestActivity = null;
	public static int isback=0;
	String jid="";
	String to="";
	String roomname="";
	public static int width;
	public static int height;

	private NewMsgReceiver newMsgReceiver;

	public static ImagePicker getImagePicker() {
		return imagePicker;
	}


	public static ContextUtil getInstance() {
		return instance;
	}

	public void setMainActivity(PustDemoActivity activity) {
		mPustTestActivity = activity;
	}

	public PustDemoActivity getMainActivity() {
		return mPustTestActivity;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		ctx =this;

		DisplayMetrics metric = new DisplayMetrics();
		((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(metric);
		width = metric.widthPixels;
		height = metric.heightPixels;

		try{
			if(Util.brand.equals("Xiaomi")){
				String APP_ID = "2882303761517595116";
				String APP_KEY = "5801759556116";
				MiPushClient.registerPush(this, APP_ID, APP_KEY);
			}else if(Util.brand.equals("Huawei") || Util.brand.equals("HONOR")){
				PushManager.requestToken(ContextUtil.this);
			}else{
				JPushInterface.setDebugMode(true);
				JPushInterface.init(this);

				//JPushInterface.setAlias(ctx, "yy", null);
			}
		}catch(Exception e){
			Log.e("ContextUtil===eee", e+"==="+e.getMessage());
		}
		instance = this;

		imagePicker = ImagePicker.getInstance();
		imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
		InitializationConfig config = InitializationConfig.newBuilder(this)
				// 全局连接服务器超时时间，单位毫秒，默认10s。
				.connectionTimeout(5 * 1000)
				// 全局等待服务器响应超时时间，单位毫秒，默认10s。
				.readTimeout(5* 1000)
				.networkExecutor(new OkHttpNetworkExecutor())
				.build();
		NoHttp.initialize(config); // NoHttp默认初始化。
		//Logger.setDebug(true); // 开启NoHttp调试模式。
		//Logger.setTag("NoHttpSample"); // 设置NoHttp打印Log的TAG。

		newMsgReceiver = new NewMsgReceiver();
		registerReceiver(newMsgReceiver, new IntentFilter("Confirm"));
		registerReceiver(newMsgReceiver, new IntentFilter("refuse"));
		registerReceiver(newMsgReceiver, new IntentFilter("Del"));
		registerReceiver(newMsgReceiver, new IntentFilter("leave"));
		registerReceiver(newMsgReceiver, new IntentFilter("kick"));
		registerReceiver(newMsgReceiver, new IntentFilter("kickmem"));
		registerReceiver(newMsgReceiver, new IntentFilter("destroyRoom"));

		sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		ImgConfig.initImageLoader();

		new Timer().schedule(new TimerTask() {  //1秒后开始，5分钟上传一次自己的位置
			@Override
			public void run() {
				if (ContextUtil.sharedPreferences.getBoolean("isShare", true)) {
				}
			}
		}, 1000,Constants.UPDATE_TIME);


		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}
	
	public static boolean isBackground() {
	    ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(ctx.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//	                    XmppConnection.getInstance().changepresence(0);
						Log.e("后台", appProcess.processName);
	                    return true;
	                }else{
//	                	XmppConnection.getInstance().changepresence(1);
				    	Log.e("前台", appProcess.processName);
				    	return false;
	                }
	           }
	    }
	    return false;
	}



	private class NewMsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, Intent intent) {
			jid = intent.getStringExtra("jid");
			to = intent.getStringExtra("to");
			roomname=intent.getStringExtra("roomname");

			if (intent.getAction().equals("ChatNewMsg")) {
			} else if (intent.getAction().equals("Confirm")) {
				Tool.initToast(ContextUtil.getInstance(), jid.split("@")[0]+"同意添加我为好友！");

				MessageConstants.friendschange=2;
				XmppConnection.getInstance().reconnect();
			}else if (intent.getAction().equals("refuse")) {
				XmppConnection.getInstance().removeUser(jid);
				MsgDbHelper.getInstance(ctx).delChatMsg(jid.split("@")[0]);
				NewMsgDbHelper.getInstance(ctx).delNewMsg(jid.split("@")[0]);
				sendBroadcast(new Intent("ChatNewMsg"));

				if(XmppConnection.getFullUsername(NewChatActivity.chatName).equals(jid)){
					CloseActivityClass.exitClient(ctx);
				}
				sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
                MessageConstants.friendschange=1;
				MessageConstants.addfriendslist.remove(jid.split("@")[0]);
				intent = new Intent(CircleConstants.FRIEND);
				sendBroadcast(intent);
			}else if (intent.getAction().equals("Del")) {
				XmppConnection.getInstance().removeUser(jid);
				MessageConstants.addfriendslist.remove(jid.split("@")[0]);
				XmppPresenceListener.del=0;
			}else if (intent.getAction().equals("kick")) {
				new XmppLoadThread(ctx) {

					@Override
					protected Object load() {
						return  null;
					}

					@Override
					protected void result(Object object) {
						XmppConnection.getInstance().reconnect();

						new Thread(){
							@Override
							public void run() {
								try {
									sleep(1*1000);
									handler.sendEmptyMessage(5);
								} catch (Exception e) {
									e.printStackTrace();
								}
								super.run();
							}
						}.start();


					}
				};

			}else if (intent.getAction().equals("kickmem")) {
				XmppConnection.getInstance().reconnect();
			} else if (intent.getAction().equals("destroyRoom")) {

				new XmppLoadThread(ctx) {

					@Override
					protected Object load() {
						return  null;
					}

					@Override
					protected void result(Object object) {
						XmppConnection.getInstance().reconnect();
						handler.sendEmptyMessage(2);
					}
				};

			} else if (intent.getAction().equals("conflict")) {
				Tool.initToast(getApplicationContext(), "此账号已在别处登录");
			} else if (intent.getAction().equals("leave")) {
				Intent i = new Intent(ctx, CircleFragment.class);
				startActivity(i);
			} else {
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
			}else if (msg.what == 2) {
				f=0;
				XmppLoadThread.mdialog.dismiss();
				Tool.initToast(ctx, "圈子"+XmppConnection.getRoomName(jid)+"已被销毁");

				if(XmppConnection.getFullRoomname(NewChatActivity.chatName).equals(jid.split("/")[0])){
					CloseActivityClass.exitClient(ctx);
				}

				sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
				sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
				MsgDbHelper.getInstance(ctx).delChatMsg(XmppConnection.getRoomName(jid));
				NewMsgDbHelper.getInstance(ctx).delNewMsg(XmppConnection.getRoomName(jid));
				sendBroadcast(new Intent("ChatNewMsg"));
			}else if (msg.what == 3) {
				Tool.initToast(ctx, jid+"加入圈子"+roomname);
			}else if (msg.what == 5) {
				Tool.initToast(ctx, "你已经被踢出圈子"+XmppConnection.getRoomName(jid));

				if(XmppConnection.getFullRoomname(NewChatActivity.chatName).equals(jid.split("/")[0])){
					CloseActivityClass.exitClient(ctx);
				}

				sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
				sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
				MsgDbHelper.getInstance(ctx).delChatMsg(XmppConnection.getRoomName(jid));
				NewMsgDbHelper.getInstance(ctx).delNewMsg(XmppConnection.getRoomName(jid));
				sendBroadcast(new Intent("ChatNewMsg"));
			}else if (msg.what == 6) {
				XmppLoadThread.mdialog.dismiss();
			}


		}
	};


	@Override
	public void processPacket(Packet packet) {
		// TODO Auto-generated method stub
		Message message = (Message) packet;
		String body = message.getBody();
		String from = message.getFrom();
		android.os.Message mess = android.os.Message.obtain();
		mess.what = 1;
		mess.obj = from + ":" + body;
		handler.sendMessage(mess);
		// 在这里面更新ui会出现迟缓及更新不出来状况
		// 发送一个boardcast或者使用handler来更新ui会很好

	}



}
