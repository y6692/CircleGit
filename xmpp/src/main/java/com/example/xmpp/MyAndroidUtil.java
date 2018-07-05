package com.example.xmpp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyAndroidUtil {
	private static Notification myNoti;
	public static int isback=0;

	/**
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param okBtn
	 * 没有取消功能的了
	 */
	public static void showDialog(Context context , String title, String message, int icon, DialogInterface.OnClickListener okBtn){
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setIcon(icon)
		.setMessage(message)
		.setPositiveButton("确定",okBtn)
		.setNegativeButton("返回", null).show();
	}
	
	/**
	 * 修改缓存
	 * @param name     一般都name+   actid、或者userId
	 * @param object        要保存的
	 */
//	public static void editXml(String name, Object object) {
//		Editor editor = ContextUtil.sharedPreferences.edit();
//		if (ContextUtil.sharedPreferences.getString(name, null) != null) {
//			editor.remove(name);
//		}
//		editor.putString(name, JsonUtil.objectToJson(object));
//		editor.commit();
//	}
	
	
	/**
	 * 修改缓存
	 * @param name     一般都name+   actid、或者userId
	 * @param result        要保存的
	 */
//	public static void editXmlByString(String name, String result) {
//		Editor editor = ContextUtil.sharedPreferences.edit();
//		if (ContextUtil.sharedPreferences.getString(name, null) != null) {
//			editor.remove(name);
//		}
//		editor.putString(name, result);
//		editor.commit();
//	}
	
	/**
	 * 修改缓存
	 * @param name     //一般都name+   actid、或者userId
	 * @param is       //true or fasle        要保存的
	 */
//	public static void editXml(String name, boolean is) {
//		Editor editor = ContextUtil.sharedPreferences.edit();
//		editor.putBoolean(name, is);
//		editor.commit();
//	}

	
//	public static void removeXml(String name){
//		Editor editor = ContextUtil.sharedPreferences.edit();
//		editor.remove(name);
//		editor.commit();
//	}
	
//	public static void clearNoti(){
//		myNoti.number = 0;
//		NotificationManager manger = (NotificationManager) ContextUtil.getInstance()
//				.getSystemService(NOTIFICATION_SERVICE);
//		manger.cancelAll();
//	}

	// djy edit  setLatestEventInfo此方法过时了
//	public static void showNoti(String notiMsg){
//		//android推送
//		if(notiMsg.contains(Constants.SAVE_IMG_PATH))
//			myNoti.tickerText = "[图片]";
//		else if(notiMsg.contains(Constants.SAVE_SOUND_PATH))
//			myNoti.tickerText = "[语音]";
//		else if(notiMsg.contains("[/g0"))
//			myNoti.tickerText = "[动画表情]";
//		else if(notiMsg.contains("[/f0"))  //适配表情
//			myNoti.tickerText = ExpressionUtil.getText(ContextUtil.getInstance(), StringUtil.Unicode2GBK(notiMsg));
//		else if(notiMsg.contains("[/a0"))
//			myNoti.tickerText = "[位置]";
//		else{
//			myNoti.tickerText = notiMsg;
//		}
//
//		Intent intent = new Intent();   //要跳去的界面
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.setClass(ContextUtil.getInstance(), MessageMain.class);
//
//		NotificationManager mNotificationManager =
//	    		(NotificationManager) ContextUtil.getInstance().getSystemService(NOTIFICATION_SERVICE);
//		PendingIntent appIntent = PendingIntent.getActivity(ContextUtil.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		myNoti.icon = R.drawable.ic_launcher;
//		myNoti.flags = Notification.FLAG_SHOW_LIGHTS| Notification.FLAG_AUTO_CANCEL;  //闪光灯
//		myNoti.ledARGB= 0xff00ff00;           //绿色
//		myNoti.number = NewMsgDbHelper.getInstance(ContextUtil.getInstance()).getMsgCount();
//
//		if (ContextUtil.sharedPreferences.getBoolean("isShake", true)) {
//			myNoti.defaults = Notification.DEFAULT_VIBRATE; // 震动
//		}
//		if (ContextUtil.sharedPreferences.getBoolean("isSound", true)) {
//			myNoti.defaults = Notification.DEFAULT_SOUND; // 响铃
//		}
//
//		myNoti.setLatestEventInfo(ContextUtil.getInstance(), ContextUtil.getInstance().getString(R.string.app_name), myNoti.tickerText, appIntent);
//		mNotificationManager.notify(0, myNoti);
//	}

	/**
	 * @param ctx
	 */
	public static boolean isBackground(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(ctx.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
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



	/**
	 * 显示一个普通的通知
	 *
	 * @param ctx
	 * @param notiMsg
	 * @param notifyClass
	 */
	public static void showNotification(Context ctx, String notiMsg, Class notifyClass) {
		String tickerText;
		//android推送
		if(notiMsg.contains(Constants.SAVE_IMG_PATH))
			tickerText = "[图片]";
		else if(notiMsg.contains(Constants.SAVE_SOUND_PATH))
			tickerText = "[语音]";
		else if(notiMsg.contains(Constants.SAVE_MOVIE_PATH))
			tickerText = "[视频]";
		else if(notiMsg.contains("[/g0"))
			tickerText = "[动画表情]";
		else if(notiMsg.contains("[/f0"))  //适配表情
//			tickerText = ExpressionUtil.getText(ContextUtil.getInstance(), StringUtil.Unicode2GBK(notiMsg));
			tickerText = "[表情]";
		else if(notiMsg.contains("[/a0"))
			tickerText = "[位置]";
		else{
			tickerText = notiMsg;
		}

		Notification notification = new NotificationCompat.Builder(ctx)
				/**设置通知左边的大图标**/
				.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.logo114))
				/**设置通知右边的小图标**/
				.setSmallIcon(R.mipmap.logo114)
				/**通知首次出现在通知栏，带上升动画效果的**/
				//.setTicker("通知来了")
				.setTicker(tickerText)
				/**设置通知的标题**/
				.setContentTitle("您有新的通知")
				/**设置通知的内容**/
				.setContentText(tickerText)
				/**通知产生的时间，会在通知信息里显示**/
				.setWhen(System.currentTimeMillis())
				/**设置该通知优先级**/
				.setPriority(Notification.PRIORITY_DEFAULT)
				/**设置这个标志当用户单击面板就可以让通知将自动取消**/
				.setAutoCancel(true)
				/**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
				.setOngoing(false)
				/**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
				.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
				.setContentIntent(PendingIntent.getActivity(ctx, 1, new Intent(ctx, notifyClass), PendingIntent.FLAG_CANCEL_CURRENT))
				.build();
		NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);
		/**发起通知**/
		notificationManager.notify(0, notification);
	}
}
