package com.sylar.adapter;

import android.util.Log;

import com.sylar.ucmlmobile.Constants;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class XmppPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG){
			Log.e("xmppchat send", presence.toXML());
		}

		String from = presence.getFrom();// 发送方
		String to = presence.getTo();// 接收方
		
	}

}
