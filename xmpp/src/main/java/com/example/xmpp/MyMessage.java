package com.example.xmpp;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by yy on 2018/4/10.
 */

public class MyMessage extends Message {
    private String bodyType;

    public MyMessage(String bodyType) {
        this.bodyType = bodyType;
    }

    public String toXML() {
        String s = super.toXML();
        return "<message  bodyType=\""+bodyType+"\""+ s.split("<message")[1];
    }
}
