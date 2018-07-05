package com.sylar.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by Wikison on 2016/4/21.
 */
public class CircleConstants {

    public static final String APPNAME = "circle";
    public static final String APP_SHAREREFERENCE = "circle";
    // sd卡上文件夹的路径
    public final static String APP_ON_SD_PATH = APPNAME + File.separatorChar;
    // 缓存存储路径
    public final static String APP_FILE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separatorChar + APP_ON_SD_PATH;

    /**
     * 广播
     */
    public static final String HIDE_TAB_BAR_FROM_JS = "hideTabBarFromJs";
    public static final String SHOW_TAB_BAR_FROM_JS = "showTabBarFromJs";
    public static final String QUAN_GO_HOME = "GO_HOME";
    public static final String SHOW_MSG_NUM = "SHOW_MSG_NUM";
    public static final String REFRESH_QUAN = "REFRESH_QUAN";
    public static final String REFRESH_DONGTAI = "REFRESH_DONGTAI";
    public static final String EXIT = "EXIT";
    public static final String FRIEND = "FRIEND";
}


