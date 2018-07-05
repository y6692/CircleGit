package com.sylar.ucmlmobile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;

import com.example.administrator.circlegit.R;
import com.sylar.unit.CloseActivityClass;


public class NotifyUpdate extends Activity {
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		CloseActivityClass.activityList.add(this);
		setContentView(R.layout.activity_notify_update);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		update();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.notify_update, menu);
		return true;
	}
	
	private void update() {
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		String mSavePath = sdpath + "Download/";
		UpdateApp updateManager = new UpdateApp(this, ConfigInfo.getReqHost(ContextUtil.getInstance())+"apk/version.xml", mSavePath);
		updateManager.checkUpdate();
	}
}
