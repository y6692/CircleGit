package com.sylar.model;

import java.io.Serializable;

/**
 * Created by djy
 * 2017/7/13 0013 上午 10:22
 */

public class VCardNew implements Serializable {
    private String headimg;
    private String address;
    private String sex;


    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
