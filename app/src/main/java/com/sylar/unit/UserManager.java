package com.sylar.unit;

import com.sylar.model.User;

/**
 * 圈子的用户管理
 * created by Djy
 * 2017/6/16 0016 上午 11:01
 */
public class UserManager {

    private static UserManager instance;
    User m_Userinfo;

    public static UserManager instance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public User getUserinfo() {
        if (m_Userinfo == null) {
            m_Userinfo  = CircleHelper.gson.fromJson(CircleHelper.getSettingString(CircleHelper.User.Key.USER_INFO,""), User.class);
        }
       return m_Userinfo ;
    }

//    public int getUserId() {
//        if (m_Userinfo == null) {
//            getUserinfo();
//        }
//        return m_Userinfo==null?0:m_Userinfo.getUserId();
//    }
    public void saveUserinfo(User userinfo) {
        CircleHelper.setSettingString(CircleHelper.User.Key.USER_INFO,CircleHelper.gson.toJson(userinfo));
        m_Userinfo=userinfo;
    }
}
