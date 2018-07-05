/*
 * 
 */
package com.intasect.heart.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.intasect.heart.utils.Const;

public class HeartService extends Service {

	private static final String TAG = "HeartService";

	/**
	 * 心跳间隔一分钟
	 */
	private static final long HEARTBEAT_INTERVAL = 60 * 1000L;
	private AlarmManager mAlarmManager;
	private PendingIntent mPendingIntent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				Const.ACTION_HEARTBEAT), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		/*
		// 发送启动推送任务的广播
		Intent startIntent = new Intent(Const.ACTION_START_HEART);
		sendBroadcast(startIntent);

		// 启动心跳定时器
		long triggerAtTime = SystemClock.elapsedRealtime() + HEARTBEAT_INTERVAL;
		mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
				triggerAtTime, HEARTBEAT_INTERVAL, mPendingIntent);
		*/
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Intent startIntent = new Intent(Const.ACTION_STOP_HEART);
		sendBroadcast(startIntent);
		//取消心跳定时器
		//mAlarmManager.cancel(mPendingIntent);
		super.onDestroy();
	}
}
