/*
 * 
 */
package com.intasect.heart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.intasect.heart.utils.Const;

/**
 * 推送广播接收器
 */
public class HeartReceiver extends BroadcastReceiver {

	private static final String TAG = "HeartReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "action" + action);
		if (Const.ACTION_START_HEART.equals(action)) {
			Log.d(TAG, "Start heart");
		} else if (Const.ACTION_HEARTBEAT.equals(action)) {
			Log.d(TAG, "Heartbeat");
			// 在此完成心跳需要完成的工作，比如请求远程服务器……

			/*
			XMPPConnection connection = XmppUtils.getInstance().getConnection();
			NetworkInfo info = ReConnectService.connectivityManager
					.getActiveNetworkInfo();

			if (info != null && info.isAvailable()) {
				if (connection.isConnected()) {
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
				}
				else if (ReConnectService.ReConService.ReConRun==null)
				{   
					ReConnectService.ReConService.reConnect(connection);
				}
			}
			*/
		} else if (Const.ACTION_STOP_HEART.equals(action)) {
			Log.d(TAG, "Stop heart");
		}
	}

}
