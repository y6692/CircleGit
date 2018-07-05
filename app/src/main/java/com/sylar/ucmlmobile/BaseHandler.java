/**
 * 
 */
package com.sylar.ucmlmobile;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * @author WangFeng
 * @date 2014上午11:57:11
 */
public class BaseHandler<T> extends Handler {
	private WeakReference<T> mActivity = null;

	BaseHandler(T activity) {
		mActivity = new WeakReference<T>(activity);
	}

	@Override
	public void handleMessage(Message msg) {
		T theActivity = mActivity.get();
		if (theActivity != null) {
			if (msg.what == IHandler.APP_EXIT) {
				AppManager.getAppManager().finishAllActivity();
				System.exit(0);
			} else {
				((IHandler) theActivity).onMessage(msg);
			}
		}
	}
}
