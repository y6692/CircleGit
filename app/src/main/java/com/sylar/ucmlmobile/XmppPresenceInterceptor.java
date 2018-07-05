package com.sylar.ucmlmobile;

import android.util.Log;

import com.sylar.unit.Util;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

public class XmppPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat send", presence.toXML());

		String to = presence.getTo();//
		if (presence.getType().equals(Presence.Type.subscribe)) {//

		}else if (presence.getType().equals(Presence.Type.subscribed)) {//
			if(Constants.IS_DEBUG)
	        Log.e("friend", to+"");
//			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		}else if (presence.getType().equals(Presence.Type.unsubscribe)||presence.getType().equals(Presence.Type.unsubscribed)) {//
			VCard vcard = new VCard();
			if(Util.brand.equals("Huawei")||Util.brand.equals("HONOR")){
				vcard.setField("token", "");
				//XmppConnection.getInstance().changeVcard(vcard);
			}
		}

	}
}
