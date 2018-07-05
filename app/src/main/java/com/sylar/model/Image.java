package com.sylar.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by djy
 * 2017/6/21 0021 上午 9:50
 */

public class Image implements Serializable {
    private String path;
    private ArrayList<Image> imageList;
    private String base64String;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<Image> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<Image> imageList) {
        this.imageList = imageList;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }
}
