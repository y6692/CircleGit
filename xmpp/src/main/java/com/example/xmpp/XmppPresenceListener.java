package com.example.xmpp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import java.util.Collection;

public class XmppPresenceListener implements PacketListener {

	public  static String jid;
	public  static int del=0;
	Context ctx;

	public XmppPresenceListener(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public void processPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG){
			Log.e("XmppPresenceListener===", presence.toXML());
		}
		
		jid = presence.getFrom();//发送方
        String to = presence.getTo();//接收方
        Type type =presence.getType();

		if (presence.getType().equals(Type.subscribe)) {
			Roster roster = XmppConnection.connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();

			int f=0;
			for(RosterEntry entry : entries) {
				Log.e("entry.getUser=========", entry.getUser() + " - " + jid);

				if(entry.getUser().equals(jid)){
					f=1;
					break;
				}
			}

			if (f==0){
				Intent intent = new Intent("AddUser");
				intent.putExtra("jid", jid);
				intent.putExtra("to", to);
				ctx.sendBroadcast(intent);
			}else{
				Intent intent = new Intent("Confirm");
				intent.putExtra("jid", jid);
				intent.putExtra("to", to);
				ctx.sendBroadcast(intent);
			}

		}else if (presence.getType().equals(Type.subscribed)) {
			Log.e("friend====", jid+"同意添加");
		}else if (presence.getType().equals(Type.unsubscribe) ||presence.getType().equals(Type.unsubscribed)) {
			Intent intent = new Intent("refuse");
			intent.putExtra("jid", jid);
			intent.putExtra("to", to);
			ctx.sendBroadcast(intent);
		}else if (jid.contains("@conference") && presence.getType().equals(Type.unavailable)) {
		}else if (jid.contains("@conference") && presence.getType().equals(Type.available)) {
			for (int i=0;i<XmppConnection.myRooms.size();i++){
				Log.e("myroom===name", "==="+XmppConnection.myRooms.get(i).name);
			}

			if(MessageConstants.isMyroom(jid.split("@")[0])){
				Log.e("myroom===", XmppConnection.myRooms.size()+"==="+jid.split("@")[0]);
			}
		}

		if (presence.toXML().contains("<destroy")) {
			Intent intent = new Intent("destroyRoom");
			intent.putExtra("jid", jid);
			intent.putExtra("to", to);
			ctx.sendBroadcast(intent);
		}else if (presence.toXML().contains("<reason>看你不爽就 踢了你") && !presence.toXML().contains("jid=\"\"")) {
			Intent intent = new Intent("kick");
			intent.putExtra("jid", jid);
			intent.putExtra("to", to);
			ctx.sendBroadcast(intent);
		}else if (presence.toXML().contains("<reason>看你不爽就 踢了你") && presence.toXML().contains("jid=\"\"")) {
			Intent intent = new Intent("kickmem");
			ctx.sendBroadcast(intent);
		}
	}

}
