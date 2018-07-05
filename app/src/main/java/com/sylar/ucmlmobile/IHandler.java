/**
 * 
 */
package com.sylar.ucmlmobile;

import android.os.Message;

/**
 * @author wangfeng
 *
 */
public abstract interface IHandler {
	final int APP_EXIT = 999999;

	/**
	 * Handler消息事件驱动
	 * 
	 * @param msg
	 */
	public abstract void onMessage(Message msg);
}
