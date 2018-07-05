package com.sylar.unit;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sylar.constant.CircleConstants;
import com.sylar.ucmlmobile.ContextUtil;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用sp来管理当前用户信息
 * created by Djy
 * 2017/6/16 0016 上午 10:50
 */

public class CircleHelper {
    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        if (!"".equals(dateStr)) {
                            return format.parse(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            })
            .setDateFormat(pattern)
            .registerTypeAdapter(Object.class, new JsonDeserializer<Double>() {
                public Double deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        return json.getAsDouble();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0d;
                }
            })
            .create();

    static {
        preferences = ContextUtil.getInstance().getSharedPreferences(
                CircleConstants.APP_SHAREREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public static final void setSettingString(String key, String status) {
        editor.putString(key, status).commit();
    }

    public static final String getSettingString(String key, String defaulStatus) {
        return preferences.getString(key, defaulStatus);
    }

    public static UserManager userManager() {
        return UserManager.instance();
    }

    /**
     * 个人
     */
    public static final class User {
        public static class Key {
            public static final String USER_PWD = "USER_PWD";
            public static final String USER_INFO = "USER_INFO";//个人用户信息
            public static final String CONTACTSLIST = "CONTACTSLIST";//联系人列表
            public static final String UNREADMESSAGE = "UNREADMESSAGE";//未读消息
        }
    }
}
