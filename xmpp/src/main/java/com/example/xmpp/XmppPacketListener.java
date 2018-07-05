package com.example.xmpp;


import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.json.JSONObject;

import java.util.Iterator;

import dao.MsgDbHelper;
import dao.NewMsgDbHelper;
import model.ChatItem;
import model.Room;

public class XmppPacketListener implements PacketListener {

	public  static String jid;
    public  static String to;
	public  String id="";
	Context ctx;
	Class notifyClass;

	public XmppPacketListener(Context ctx, Class notifyClass) {
		this.ctx = ctx;
		this.notifyClass = notifyClass;
	}

	@Override
	public void processPacket(Packet packet) {
		final Message nowMessage = (Message) packet;

		if(Constants.IS_DEBUG){
			Log.e("XmppMessageListener===", "==="+nowMessage.toXML());
		}

		jid = nowMessage.getFrom();//发送方
        to = nowMessage.getTo();

		if(nowMessage.getPacketID()!=null){
			if(id.equals(nowMessage.getPacketID())) return;
			id = nowMessage.getPacketID();
		}

		if (nowMessage.toXML().contains("<invite")) {
            Intent intent = new Intent("invite");
            intent.putExtra("jid", jid);
            intent.putExtra("to", to);
            ctx.sendBroadcast(intent);
		}

		if(nowMessage.getBody()==null){
			return;
		}

		String msgtype="";
		Type type = nowMessage.getType();
		if ((type == Type.groupchat || type == Type.chat)&&  !nowMessage.getBody().equals("")) {

			if(nowMessage.getProperty("join")!=null){
				Intent intent = new Intent("join");
				intent.putExtra("jid", jid.split("/")[0]);
				intent.putExtra("to", to);
				intent.putExtra("roomname", nowMessage.getProperty("join").toString());
				ctx.sendBroadcast(intent);

				String msgBody=nowMessage.getBody();
				String chatName =  XmppConnection.getUsername(nowMessage.getFrom());
				String userName = chatName;
				int isbit= MsgDbHelper.getInstance(ctx).getbit(userName);
				ChatItem msg =  new ChatItem(ChatItem.CHAT, chatName, userName, "",  msgBody, DateUtil.now_MM_dd_HH_mm_ss(), 0, 0,null,null,0,null,null,null,isbit,0, msgtype);
				NewMsgDbHelper.getInstance(ctx).saveNewMsg(chatName);
				MsgDbHelper.getInstance(ctx).saveChatMsg(msg);
				ctx.sendBroadcast(new Intent("ChatNewMsg"));

				if(MyAndroidUtil.isBackground(ctx) || MyAndroidUtil.isback==1){
					MyAndroidUtil.showNotification(ctx, msgBody, notifyClass);//打开提示框
				}else {
					//播放提示音
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(ctx, notification);
					r.play();
				}
				return;
			}

			if(nowMessage.getProperty("quit")!=null){
				Intent intent = new Intent("quit");
				intent.putExtra("jid", jid.split("/")[0]);
				intent.putExtra("to", to);
				intent.putExtra("roomname", nowMessage.getProperty("quit").toString());
				ctx.sendBroadcast(intent);

				String msgBody=nowMessage.getBody();
				String chatName =  XmppConnection.getUsername(nowMessage.getFrom());
				String userName = chatName;
				int isbit= MsgDbHelper.getInstance(ctx).getbit(userName);
				ChatItem msg =  new ChatItem(ChatItem.CHAT, chatName, userName, "",  msgBody, DateUtil.now_MM_dd_HH_mm_ss(), 0, 0,null,null,0,null,null,null,isbit,0, msgtype);
				NewMsgDbHelper.getInstance(ctx).saveNewMsg(chatName);
				MsgDbHelper.getInstance(ctx).saveChatMsg(msg);
				ctx.sendBroadcast(new Intent("ChatNewMsg"));

				if(MyAndroidUtil.isBackground(ctx) || MyAndroidUtil.isback==1){
					MyAndroidUtil.showNotification(ctx, msgBody, notifyClass);//打开提示框
				}else {
					//播放提示音
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(ctx, notification);
					r.play();
				}
				return;
			}

			String chatName = "";
			String userName = "";
			String Iswork="0";
			int chatType = ChatItem.CHAT;

			//name
			if (type == Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getFrom());
				userName = XmppConnection.getRoomUserName(nowMessage.getFrom());
				chatType = ChatItem.GROUP_CHAT;
			}else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getFrom());
			}

			if (!userName.equals(Constants.XMPP_USERNAME)) {  //不是自己发出的,防群聊
				//time
				String dateString;
				DelayInformation inf = (DelayInformation) nowMessage.getExtension("x", "jabber:x:delay");
				dateString = DateUtil.now_MM_dd_HH_mm_ss();
				//msg
				ChatItem msg = null;
				String msgBody="";
				String fromtype = nowMessage.getFrom().split("/")[1];
				if(fromtype.equals("IOS")){
					 Document doc = null;
					 try {
						doc = DocumentHelper.parseText(nowMessage.toXML());
						Element rootElt = doc.getRootElement();
					   Iterator iter = rootElt.elementIterator("property"); // 获取根节点下的子节点head
					   while (iter.hasNext()) {
		                  Element itemEle = (Element) iter.next();
		                  String name = itemEle.elementTextTrim("name"); // 拿到head下的子节点script下的字节点username的值
		                  String value = itemEle.elementTextTrim("value");
		                  nowMessage.setProperty(name, value);

		                  if (name.equals("join")){
							Intent intent = new Intent("join");
							intent.putExtra("jid", jid.split("/")[0]);
							intent.putExtra("to", to);
							intent.putExtra("roomname", nowMessage.getProperty("join").toString());
							ctx.sendBroadcast(intent);
							return;
						  }
					   }
					} catch (DocumentException e) {
						e.printStackTrace();
					} // 将字符串转为XML
				}
				if(nowMessage.getProperty("iswork")!=null){
					Iswork=nowMessage.getProperty("iswork").toString();
				}


				boolean isJson = false;
				JSONObject obj = null;
				try {
					obj = new JSONObject(nowMessage.getBody());
					isJson = true;
				}catch (Exception e){
					e.printStackTrace();
				}

				try {
					if (isJson && "0".equals(obj.getString("bodyType"))) { //接收视频
						msgBody = nowMessage.getBody();
						msgtype = "0";
					} else if (isJson && "2".equals(obj.getString("bodyType"))) { //接收图片
						msgBody = Constants.SAVE_IMG_PATH + "/"+ System.currentTimeMillis()+ ".jpg";
						FileUtil.saveFileByBase64(obj.getString("msg"), msgBody);
						msgtype = "2";
					} else if (isJson && "3".equals(obj.getString("bodyType"))) { //接收图片
						msgBody = Constants.SAVE_SOUND_PATH + "/"+ System.currentTimeMillis()+ ".mp3";
						FileUtil.saveFileByBase64(obj.getString("msg"), msgBody);
						msgtype = "3";
					} else if (isJson && "5".equals(obj.getString("bodyType"))) { //接收视频
						msgBody = nowMessage.getBody();
						msgtype = "5";
					} else if (isJson && "6".equals(obj.getString("bodyType")) ) { //接收定位
						msgBody = nowMessage.getBody();
						msgtype = "6";
					} else{
						msgBody = nowMessage.getBody();
					}
				}catch (Exception e){
					e.printStackTrace();
				}

				if (type == Type.groupchat && XmppConnection.leaveRooms.contains(new Room(chatName))) {  //正常保存了
					Log.e("packetListener===", "我已经离开这个圈子了");
				} else if (nowMessage.getBody().contains("[RoomChange")) {
					XmppConnection.getInstance(ctx).reconnect();
				} else {
					int isbit= MsgDbHelper.getInstance(ctx).getbit(userName);
					msg =  new ChatItem(chatType,chatName,userName, "",  msgBody, dateString, 0, Integer.parseInt(Iswork),null,null,0,null,null,null,isbit,0, msgtype);
					NewMsgDbHelper.getInstance(ctx).saveNewMsg(chatName);
					MsgDbHelper.getInstance(ctx).saveChatMsg(msg);
					ctx.sendBroadcast(new Intent("ChatNewMsg"));

					if(MyAndroidUtil.isBackground(ctx) || MyAndroidUtil.isback==1){
						MyAndroidUtil.showNotification(ctx, msgBody, notifyClass);//打开提示框
					}else {
						//播放提示音
						Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						Ringtone r = RingtoneManager.getRingtone(ctx, notification);
						r.play();
					}
				}
			}
		}
	}

}
