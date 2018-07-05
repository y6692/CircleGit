package com.example.xmpp;

public class Msg {
	public String userid;
	public String msg;
	public String date;
	public String from;
	public String sessionid;
	
	public Msg(String userid, String msg, String date, String from, String sessionid) {
		this.userid = userid;
		this.msg = msg;
		this.date = date;
		this.from = from;
		this.sessionid = sessionid;
	}
}
