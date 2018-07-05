package com.sylar.model;

import java.io.Serializable;

/**
 * Created by djy
 * 2017/7/13 0013 上午 10:29
 */

public class VarCard implements Serializable {
    private VCardNew vCard;

    public VCardNew getvCard() {
        return vCard;
    }

    public void setvCard(VCardNew vCard) {
        this.vCard = vCard;
    }
}
