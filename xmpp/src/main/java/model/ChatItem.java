package model;

import java.io.Serializable;

public class ChatItem implements Serializable {
	public static final int CHAT = 0; 
	public static final int GROUP_CHAT = 1;
	public int chatType;     // 0 chat  1 groupChat 2 noti
	public String chatName;  //群聊的话跟username不一样
	public String username;  //对方的昵称
	public String head;
	public String msg;       //消息主体
	public String sendDate;
	public int inOrOut;     //0代表in 1代表out
	public int Iswork;
	public String serialnumber;
	public String link;
	public int issubmit;
	public String parms;
	public String Datas;
	public String msgUrl;
	public int isbit;        //是否显示时间
	public int isRead;
	public String bodyType;      //"5":视频

	public ChatItem() {
		super();
	}

	public ChatItem(int chatType, String chatName, String username, String head, String msg, String sendDate,
                    int inOrOut, int Iswork, String serialnumber, String link, int issubmit, String parms, String Datas, String msgUrl, int isbit, int isRead, String bodyType) {
		super();
		this.chatType = chatType;
		this.chatName = chatName;
		this.username = username;
		this.head = head;
		this.msg = msg;
		this.sendDate = sendDate;
		this.inOrOut = inOrOut;
		this.Iswork =Iswork;
		this.serialnumber = serialnumber;
		this.link=link;
		this.issubmit =issubmit;
		this.parms=parms;
		this.Datas =Datas;
		this.msgUrl=msgUrl;
		this.isbit =isbit;
		this.isRead =isRead;
		this.bodyType = bodyType;
	}
}
