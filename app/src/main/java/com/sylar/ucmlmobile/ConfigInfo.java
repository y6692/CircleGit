package com.sylar.ucmlmobile;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigInfo {
	public static String TAG = "UCMLMOBILE";

	public static void setIsLogin(Context context, Boolean f) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("islogin", f);
		editor.commit();
	}

	public static Boolean getIsLogin(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getBoolean("islogin", false);
	}

	public static void setIsConn(Context context, Boolean f) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isconn", f);
		editor.commit();
	}

	public static Boolean getIsConn(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getBoolean("isconn", false);
	}
	
	public static void setUserName(Context context, String uname) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("uname", uname);
		editor.commit();
	}
	
	public static String getUserName(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getString("uname", "sysadmin");
	}
	
	public static void setUserPass(Context context, String upass) {
		SharedPreferences settings  = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("upass", upass);
		editor.commit();
	}

	public static void setHisUserName(Context context, String uname) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("hisuname", uname);
		editor.commit();
	}

	public static String getHisUserName(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getString("hisuname", "");
	}
	
	public static String getUserPass(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getString("upass", "sysadmin");
	}
	
	public static void setReqHost(Context context, String host) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("reqhost", host);
		editor.commit();
	}
	
	public static String getReqHost(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		//return settings.getString("reqhost", "http://192.168.10.201:86/");
		return settings.getString("reqhost", "http://106.15.203.183:5222/");

	}

	public static void setPasswordJ(Context context, String upass) {
		SharedPreferences settings  = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("passwordj", upass);
		editor.commit();
	}

	public static String getPasswordJ(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getString("passwordj", "hE91mW0MvQh1ffs46bnMFA==");
	}

	public static void setServername(Context context, String upass) {
		SharedPreferences settings  = context.getSharedPreferences(TAG, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("servername", upass);
		editor.commit();
	}

	public static String getServername(Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG, 0);
		return settings.getString("servername", "223.112.193.38");
	}

}
