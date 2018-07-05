package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.xmpp.Constants;
import com.example.xmpp.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ChatItem;

public class MsgDbHelper {
	private static MsgDbHelper instance = null;
	
	private SqlLiteHelper helper;
	private SQLiteDatabase db;  // 我的最新聊天信息
	private final int SHOW_MSG_COUNT = 15;
	private final int MORE_MSG_COUNT = 10 ;	
	
	public MsgDbHelper(Context context) {
		helper = new SqlLiteHelper(context);
		db = helper.getWritableDatabase();
	}

	public void closeDb(){
		db.close();
		helper.close();
	}
	public static MsgDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MsgDbHelper(context);
		}
		return instance;
	}
	
	private class SqlLiteHelper extends SQLiteOpenHelper {

		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "chat";
		private static final String VIDEO_NAME = "video";

		public SqlLiteHelper(Context context) {
			super(context, "chat.db", null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE  IF NOT EXISTS " + DB_NAME
						+ "( id INTEGER PRIMARY KEY AUTOINCREMENT, chatType INTEGER, chatName text, "+
						"username text, head text, msg text, sendDate text, inOrOut INTEGER," +
						"whos text, i_filed INTEGER, t_field text, Iswork INTEGER, serialnumber text, " +
						"link text, issubmit INTEGER, parms text, Datas text, msgUrl text, isbit INTEGER, " +
						"isRead INTEGER, bodyType text)";
			db.execSQL(sql);

			sql = "CREATE TABLE  IF NOT EXISTS " + VIDEO_NAME
					+ "( id INTEGER PRIMARY KEY AUTOINCREMENT, msg text, msgUrl text, duration INTEGER)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropTable(db);
			onCreate(db);
		}

		private void dropTable(SQLiteDatabase db) {
			String sql = "DROP TABLE IF EXISTS "+DB_NAME;
			db.execSQL(sql);
		}

	}

	public void saveChatMsg(ChatItem msg){
		ContentValues values = new ContentValues();
		values.put("chatType", msg.chatType);
		values.put("chatName", msg.chatName);
		values.put("username", msg.username);
		values.put("head", msg.head);
		values.put("msg", msg.msg);
		values.put("sendDate",msg.sendDate);
		values.put("inOrOut", msg.inOrOut);
		values.put("whos", Constants.XMPP_USERNAME);
		values.put("Iswork", msg.Iswork);
		values.put("serialnumber", msg.serialnumber);
		values.put("link", msg.link);
		values.put("issubmit", msg.issubmit);
		values.put("parms", msg.parms);
		values.put("Datas", msg.Datas);
		values.put("msgUrl", msg.msgUrl);
		values.put("isbit", msg.isbit);
		values.put("isRead", msg.isRead);
		values.put("bodyType", msg.bodyType);
		db.insert(helper.DB_NAME, "id", values);
	}

	public void saveVideoMsg(String msg, String msgUrl, int duration){
		ContentValues values = new ContentValues();
		values.put("msg", msg);
		values.put("msgUrl", msgUrl);
		values.put("duration", duration);
		db.insert(helper.VIDEO_NAME, "id", values);
	}
	
	public void updateChatMsg(String chatName){
		try{
			ContentValues values = new ContentValues();
			values.put("isRead", "1");
			db.update(helper.DB_NAME, values, "chatName=?", new String[]{chatName});
		}catch(Exception e){
		}
	}

//	public void updateMsgUrl(String msgUrl, String msg){
//		try
//		{
//			ContentValues values = new ContentValues();
//			values.put("msgUrl", msgUrl);
//			values.put("type", "VIDEO");
//			db.update(helper.DB_NAME, values, "msg=?", new String[]{msg});
//		}
//		catch(Exception e)
//		{
//		}
//	}

	public Map getonevideoMsg(String msg){
		try{
			String msgUrl="";
			int duration;
			Map map = new HashMap();
			String sql ="select msgUrl,duration from  "+helper.VIDEO_NAME + " where msg = ?";
			final Cursor cursor = db.rawQuery(sql, new String[]{msg});
			while (cursor.moveToNext()) {
				msgUrl = cursor.getString(0);
				duration = cursor.getInt(1);

				map.put("msgUrl", msgUrl);
				map.put("duration", duration);

			}
			cursor.close();
			return map;
		}catch(Exception e){
			return null;
		}
	}
	
	public int getbit(String chatName){
		ChatItem item=new ChatItem();
		item=getMaxtimeChatMsg(chatName);
		if(item==null){
			return 1;
		}else{
			String nowtime= DateUtil.now_MM_dd_HH_mm_ss();
			String lastmonth=item.sendDate.substring(0, 2);
			String month=nowtime.substring(0, 2);
			String lastday=item.sendDate.substring(3, 5);
			String day=nowtime.substring(3,5);
			String lastH=item.sendDate.substring(7, 9);
			String H=nowtime.substring(7,9);
			
			if(lastmonth.equals(month) && lastday.equals(day) && lastH.equals(H) ){
				int now= Integer.parseInt(nowtime.substring(10, 12))- Integer.parseInt(item.sendDate.substring(10, 12));
				if(now<3){
					return 0;
				}
			}
		}
		return 1;
	}

	/**
	 * 取当前会话窗口的聊天记录，限量count
	 * @param chatName
	 */
	public List<ChatItem> getChatMsg(String chatName){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql = "select a.chatType,a.chatName,a.username,a.head,a.msg,a.sendDate,a.inOrOut,a.Iswork,a.serialnumber,a.link,a.issubmit,a.parms,a.Datas,a.msgUrl,a.isbit,a.isRead,a.bodyType " +
				" from(select * from "+helper.DB_NAME +
				" where chatName = ? and whos = ? order by id desc LIMIT " +SHOW_MSG_COUNT+")a order by a.id";
		Cursor cursor = db.rawQuery(sql, new String[]{chatName,Constants.XMPP_USERNAME});
		while(cursor.moveToNext()){
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 取当前会话窗口的聊天记录，限量count
	 * @param chatName
	 */
	public ChatItem getMaxtimeChatMsg(String chatName){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql = "select a.chatType,a.chatName,a.username,a.head,a.msg,a.sendDate,a.inOrOut,a.Iswork,a.serialnumber,a.link,a.issubmit,a.parms,a.Datas,a.msgUrl,a.isbit,a.isRead,a.bodyType " +
				" from(select * from "+helper.DB_NAME +
				" where chatName = ? and isbit = ? order by id desc )a order by a.id desc";
		Cursor cursor = db.rawQuery(sql, new String[]{chatName,"1"});
		while(cursor.moveToNext()){
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		if(chatItems!=null && chatItems.size()>0){
			return chatItems.get(0);
		}else{
			return null;	
		}


	}
	
	/**
	 * 获取更多好友聊天记录,显示多5条
	 * @param startIndex
	 * @param chatName
	 */
	public List<ChatItem> getChatMsgMore(int startIndex, String chatName){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select a.chatType,a.chatName,a.username,a.head,a.msg,a.sendDate,a.inOrOut,a.Iswork,a.serialnumber,a.link,a.issubmit,a.parms,a.Datas,a.msgUrl,a.isbit,a.isRead,a.bodyType " +
				" from(select * from "+helper.DB_NAME +
				" where chatName = ? and whos = ? order by id desc LIMIT " +MORE_MSG_COUNT+" offset "+startIndex+")a order by a.id";
		Cursor cursor = db.rawQuery(sql, new String[]{chatName,Constants.XMPP_USERNAME});
		while(cursor.moveToNext()){
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getLastMsg(){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut,a.Iswork,a.serialnumber,a.link,a.issubmit,a.parms,a.Datas,a.msgUrl,a.isbit,a.isRead,a.bodyType from  "+helper.DB_NAME +
				" where whos = ? "+
				" GROUP BY chatName "+
				"order by id desc";
		final Cursor cursor = db.rawQuery(sql, new String[]{Constants.XMPP_USERNAME});
		while (cursor.moveToNext()) {
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}
	
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getOutMsg(){
		try{
			List<ChatItem> chatItems = new ArrayList<ChatItem>();
			ChatItem msg;
			String sql ="select DISTINCT chatName from  "+helper.DB_NAME +
					" where isRead = ? "+
						"order by id desc";
			final Cursor cursor = db.rawQuery(sql, new String[]{"0"});
			while (cursor.moveToNext()) {
				msg = new ChatItem(0,cursor.getString(0),null, null, null
						, null, 0,0,null,null,0,null,null,null,0,0,null);
				chatItems.add(msg);
				msg = null;
			}
			cursor.close();
			return chatItems;
		}catch(Exception e){
			return null;	
		}
	}
	
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getrecentlyMsg(){
		try{
			List<ChatItem> chatItems = new ArrayList<ChatItem>();
			ChatItem msg;
			String sql ="select DISTINCT chatName from  "+helper.DB_NAME +
						" where chatName != ? "+
						"order by id desc";
			final Cursor cursor =  db.rawQuery(sql, new String[]{""});
			while (cursor.moveToNext()) {
				msg = new ChatItem(0,cursor.getString(0),null, null, null
						, null, 0,0,null,null,0,null,null,null,0,0,null);
				chatItems.add(msg);
				msg = null;
			}
			cursor.close();
			return chatItems;
		}catch(Exception e){
			return null;	
		}
	}
	
	
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getoneMsg(String chatName){
		try{
			List<ChatItem> chatItems = new ArrayList<ChatItem>();
			ChatItem msg;
			String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut,Iswork,serialnumber,link,issubmit,parms,Datas,msgUrl,isbit,isRead,bodyType from  "+helper.DB_NAME +
					" where chatName = ? and isRead = ? "+
						"order by id desc";
			final Cursor cursor = db.rawQuery(sql, new String[]{chatName,"0"});
			while (cursor.moveToNext()) {
				msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));

				chatItems.add(msg);
				msg = null;
			}
			cursor.close();
			return chatItems;
		}catch(Exception e){
			return null;	
		}
	}
	/**
	 * 取得我的的最新消息，显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getonerecentlyMsg(String chatName){
		try
		{
			List<ChatItem> chatItems = new ArrayList<ChatItem>();
			ChatItem msg;
			String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut,Iswork,serialnumber,link,issubmit,parms,Datas,msgUrl,isbit,isRead,bodyType from  "+helper.DB_NAME +
					" where chatName = ? "+
						"order by isRead desc";
			final Cursor cursor = db.rawQuery(sql, new String[]{chatName});
			while (cursor.moveToNext()) {
				msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
						, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));

				chatItems.add(msg);
				msg = null;
			}
			cursor.close();
			return chatItems;
		}
		catch(Exception e)
		{
			return null;	
		}
	}
	/**
	 * 取得我的的最新消息，模糊搜索,显示在好友表
	 */
	@SuppressWarnings("unused")
	public List<ChatItem> getLastMsg(String keywords){
		List<ChatItem> chatItems = new ArrayList<ChatItem>();
		ChatItem msg;
		String sql ="select chatType,chatName,username,head,msg,sendDate,inOrOut,a.Iswork,a.serialnumber,a.link,a.issubmit,a.parms,a.Datas,a.msgUrl,a.isbit,a.isRead,a.bodyType from  "+helper.DB_NAME +
			 	" where username like ? and whos = ? "+
				 " GROUP BY chatName "+
					" order by id desc";
		final Cursor cursor = db.rawQuery(sql, new String[]{"%"+keywords+"%",Constants.XMPP_USERNAME});
		while (cursor.moveToNext()) {
			msg = new ChatItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)
					, cursor.getString(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getInt(14),cursor.getInt(15),cursor.getString(16));
			chatItems.add(msg);
			msg = null;
		}
		cursor.close();
		return chatItems;
	}

	public String getChatName(String username){
		String name="";
		String sql ="select chatName from  "+helper.DB_NAME +
				" where username = ? ";

		final Cursor cursor = db.rawQuery(sql, new String[]{username});

		while (cursor.moveToNext()) {

			name=cursor.getString(0);

		}
		cursor.close();
		return name;
	}
	
	public void delChatMsg(String msgId){
		db.delete(helper.DB_NAME, "chatName=? and whos=?", new String[]{msgId,Constants.XMPP_USERNAME});
	}

	public void clear(){
		db.delete(helper.DB_NAME, "id>?", new String[]{"0"});
	}
}