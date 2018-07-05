package com.sylar.ucmlmobile;

import android.content.Intent;
import android.util.Log;

import com.sylar.dao.MsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.unit.DateUtil;
import com.sylar.unit.FileUtil;
import com.sylar.unit.StringUtil;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Session;
import org.json.JSONObject;

public class XmppMessageInterceptor implements PacketInterceptor {
	public String id="";

	@Override
	public void interceptPacket(Packet packet) {
		Message nowMessage = (Message) packet;
//		MyMessage myMessage = (MyMessage) nowMessage;
//		nowMessage = new MyMessage();
		if(Constants.IS_DEBUG)
		Log.e("xmppchat send>>>", id+"=="+nowMessage.getPacketID()+"=="+nowMessage.toXML());


		//if(!nowMessage.getFrom().split("/")[1].equals("IOS")){
			if(id.equals(nowMessage.getPacketID())) return;

			id = nowMessage.getPacketID();
		//}


		if (nowMessage.getType() == Message.Type.groupchat || nowMessage.getType() == Message.Type.chat) {
			String chatName = "";
			String userName = "";
			String Iswork="0";
			int chatType = ChatItem.CHAT;

			//name
			if (nowMessage.getType() == Message.Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getTo());
				userName = nowMessage.getTo();
				chatType = ChatItem.GROUP_CHAT;
			} else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getTo());
			}

			if(nowMessage.getProperty("iswork")!=null)
			{
				Iswork=nowMessage.getProperty("iswork").toString();
			}
			//type
			// 记录我们发出去的消息
			String msgBody = "" ;
			String msgType = "";
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
					msgType = "0";
				} else if (isJson && "2".equals(obj.getString("bodyType"))) { //接收图片
					msgBody = Constants.SAVE_IMG_PATH + "/"+ System.currentTimeMillis()+ ".jpg";
					FileUtil.saveFileByBase64(obj.getString("msg"), msgBody);
					msgType = "2";
				} else if (isJson && "3".equals(obj.getString("bodyType"))) { //接收图片
					msgBody = Constants.SAVE_SOUND_PATH + "/"+ System.currentTimeMillis()+ ".mp3";
					FileUtil.saveFileByBase64(obj.getString("msg"), msgBody);
					msgType = "3";
				} else if (isJson && "5".equals(obj.getString("bodyType"))) { //接收视频
					msgBody = nowMessage.getBody();
					msgType = "5";
				} else if (isJson && "6".equals(obj.getString("bodyType")) ) { //接收定位
					msgBody = nowMessage.getBody();
					msgType = "6";
				}else {
					msgBody = nowMessage.getBody();
				}


				if (nowMessage.getBody().contains("[RoomChange")) {
					System.out.println("圈子要发生改变了");
				} else {
					int isbit= MsgDbHelper.getInstance(ContextUtil.getInstance()).getbit(chatName);
					ChatItem msg = new ChatItem(chatType,chatName,userName, "", msgBody, DateUtil.now_MM_dd_HH_mm_ss(), 1, Integer.parseInt(Iswork),null,null,0,null,null,null,isbit,1, msgType);
					MsgDbHelper.getInstance(ContextUtil.getInstance()).saveChatMsg(msg);
					ContextUtil.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
				}

			}catch (Exception e){
				Log.e("JSONObject===eee222", "===");
				e.printStackTrace();
			}

		}

		
	}

}
