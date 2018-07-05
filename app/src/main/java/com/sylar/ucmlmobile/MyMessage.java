package com.sylar.ucmlmobile;

import android.util.Log;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyMessage extends Message{
    private String bodyType;

    public MyMessage(String bodyType) {
        this.bodyType = bodyType;
    }

    public String toXML() {
        String s = super.toXML();
        return "<message  bodyType=\""+bodyType+"\""+ s.split("<message")[1];
    }
}
