package com.example.xmpp;

import android.app.Activity;
import android.os.Environment;

import model.User;


public class Constants {
	public final static String PATH =  Util.getInstance().getExtPath()+"/GZDMobile";
	public static String XMPP_HOST = "192.168.10.201";
	public static int XMPP_PORT = 5222;
	public static String XMPP_HOSTNAME = "192.168.10.201";
	public static String XMPP_USERNAME = "XMPP_USERNAME";
	public static String XMPP_PASSWORD = "XMPP_PASSWORD";
	public static String XMPP_NICKNAME = "XMPP_NICKNAME";
	public static Activity CONTEXT;
	public static final int DIALOG_SHOW = 0;
	public static final int DIALOG_CANCEL = 1;
	public static final int LOGIN_ERROR_REPEAT = 409;
	public static final int LOGIN_ERROR_NET = 502;
	public static final int LOGIN_ERROR_PWD = 401;
	public static final int LOGIN_ERROR = 404;
	public static final int ERROR_CONN = 4001;
	public static final int ERROR_REGISTER = 4002;
	public static final int ERROR_REGISTER_REPEATUSER = 4003;
	public static final int SUCCESS = 201;
	public static String access_token="";
	public final static boolean IS_DEBUG = true;
	public final static int UPDATE_TIME =  60*1000;   //好友位置刷新时间，同时也是自己的位置上传时间间隔
	public final static String SHARED_PREFERENCES = "openfile";
	public static User loginUser;
	public final static String SAVE_IMG_PATH = PATH + "/images";//设置保存图片文件的路径
	public final static String SAVE_SOUND_PATH = PATH + "/sounds";//设置声音文件的路径
	public final static String SAVE_MOVIE_PATH = PATH + "/movies";//设置小视频文件的路径
	public final static String serialNum = android.os.Build.SERIAL;
	public final static String FILE_START_NAME = "VMS_";
	public final static String VIDEO_EXTENSION = ".mp4";
	public final static String IMAGE_EXTENSION = ".jpg";
	public final static String DCIM_FOLDER = "/DCIM";
	public final static String CAMERA_FOLDER = "/video";
	public final static String TEMP_FOLDER = "/Temp";
	public final static String CAMERA_FOLDER_PATH = Environment
			.getExternalStorageDirectory().toString()
			+ Constants.DCIM_FOLDER
			+ Constants.CAMERA_FOLDER;
	public final static String TEMP_FOLDER_PATH = Environment
			.getExternalStorageDirectory().toString()
			+ Constants.DCIM_FOLDER
			+ Constants.CAMERA_FOLDER + Constants.TEMP_FOLDER;
	public final static String VIDEO_CONTENT_URI = "content://media/external/video/media";
	public final static int RESOLUTION_HIGH = 1300;
	public final static int RESOLUTION_MEDIUM = 500;
	public final static int RESOLUTION_LOW = 180;
	public final static int RESOLUTION_HIGH_VALUE = 2;
	public final static int RESOLUTION_MEDIUM_VALUE = 1;
	public final static int RESOLUTION_LOW_VALUE = 0;
}
