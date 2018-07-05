package model;

import java.io.Serializable;


/**
 * Created by djy
 * 2017/6/21 0021 上午 9:50
 */

public class Emoji implements Serializable {
    /** 表情资源对应的名字 */
    private String name;
    private int resId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
