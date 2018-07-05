package com.sylar.ucmlmobile;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyIQ extends IQ {
    @Override
    public String getChildElementXML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<req var='read'>< attr var='temprature'/></req>");
        return stringBuilder.toString();
    }
}
