package com.example.xmpp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import dao.MsgDbHelper;
import dao.NewMsgDbHelper;
import model.ChatItem;
import model.Room;
import model.User;

public class XmppConnection implements PacketListener {

	public static XMPPConnection connection = null;
	public static XmppConnection xmppConnection;
	public static Roster roster;
	private static Chat newchat;
	public static MultiUserChat mulChat;
	private XmppConnecionListener connectionListener;
	private XmppMessageInterceptor xmppMessageInterceptor;
	private XmppPacketListener packetListener;
	public static List<Room> myRooms = new ArrayList<Room>();
	public static List<Room> leaveRooms = new ArrayList<Room>();
	private List<Msg> offlineList = new ArrayList<Msg>();
	Context ctx;
	Class notifyClass;

	public Class getNotifyClass() {
		return notifyClass;
	}

	public void setNotifyClass(Class notifyClass) {
		this.notifyClass = notifyClass;
	}

	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XmppConnection(Context ctx) {
		this.ctx = ctx;
	}

	public static XmppConnection getInstance(Context ctx) {
		if (xmppConnection == null) {
			xmppConnection = new XmppConnection(ctx);
		}
		return xmppConnection;
	}


	public XMPPConnection getConnection() {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	public boolean openConnection() {
		try {
			if (null == connection || !connection.isAuthenticated()) {
				XMPPConnection.DEBUG_ENABLED = true;
				ConnectionConfiguration config = new ConnectionConfiguration(Constants.XMPP_HOST, Constants.XMPP_PORT, Constants.XMPP_HOSTNAME);
//				if (Build.VERSION.SDK_INT >= 14) {
//					config.setKeystoreType("AndroidCAStore");
//					config.setTruststorePassword(null);
//					config.setKeystorePath(null);
//				} else {
//					config.setKeystoreType("BKS");
//					String path = System.getProperty("javax.net.ssl.trustStore");
//					if (path == null)
//						path = System.getProperty("java.home") + File.separator
//								+ "etc" + File.separator + "security"
//								+ File.separator + "cacerts.bks";
//					config.setKeystorePath(path);
//				}
// 				config.setSASLAuthenticationEnabled(false);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(SecurityMode.disabled);
				config.setSASLAuthenticationEnabled(false);
				config.setSendPresence(true); //
				connection = new XMPPConnection(config);
//				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				connection.connect();
				configureConnection(ProviderManager.getInstance());
				connectionListener = new XmppConnecionListener(ctx);
				connection.addConnectionListener(connectionListener);
				xmppMessageInterceptor = new XmppMessageInterceptor(ctx);
				packetListener = new XmppPacketListener(ctx, notifyClass);
				connection.addPacketInterceptor(xmppMessageInterceptor,new PacketTypeFilter(Message.class));
				connection.addPacketListener(packetListener,new PacketTypeFilter(Message.class));
//				connection.addPacketListener(packetListener, new NotFilter(null));
//				connection.addPacketListener(packetListener,new PacketTypeFilter(IQ.class));
//				connection.a.addMessageListener(messageListener,new PacketTypeFilter(Message.class));
				connection.addPacketListener(new XmppPresenceListener(ctx), new PacketTypeFilter(Presence.class));
				connection.addPacketInterceptor(new XmppPresenceInterceptor(ctx), new PacketTypeFilter(Presence.class));
//				connection.addPacketInterceptor(new XmppPresenceInterceptor(), new PacketTypeFilter(Message.class));
//				connection.addPacketListener(this, new PacketTypeFilter(new HomeActivity().getClass()));
//				connection.sendPacket();
// 				connection.addPacketListener(arg0, arg1);
				ProviderManager.getInstance().addIQProvider("muc", "MZH", new MUCPacketExtensionProvider());

				return true;
			}
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void processPacket(Packet packet) {
//		MyMessage message = (MyMessage) packet;
//		Log.e("发送了=======", packet.toXML() + "===" + message.toXML());
		Log.e("发送了=======", packet.toXML() + "===");

		// 在这里面更新ui会出现迟缓及更新不出来状况
		// 发送一个boardcast或者使用handler来更新ui会很好
	}


	/**
	 * @param user
	 * @param xmppConnection
	 * @return
	 */
	public VCard getUserInfo(String user, XmppConnection xmppConnection) {  //
		try {
			VCard vcard = new VCard();
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			if (user == null) {
				vcard.load(xmppConnection.getConnection());
			}
			else {
				vcard.load(xmppConnection.getConnection(), user + "@" + Constants.XMPP_HOSTNAME);
			}
			if (vcard != null)
				return vcard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class MUCPacketExtensionProvider implements IQProvider {
		@Override
		public IQ parseIQ(XmlPullParser parser) throws Exception {
			int eventType = parser.getEventType();

			myRooms.clear();
			leaveRooms.clear();

			Room info = null;
			while (true) {
				if (eventType == XmlPullParser.START_TAG) {
					if ("room".equals(parser.getName())) {
						String account = parser.getAttributeValue("", "account");
						String roomName = parser.getAttributeValue("", "roomName");
						String roomJid = parser.getAttributeValue("", "roomJid");
						info = new Room();
						info.name = roomName;
						info.roomid = roomJid;
						myRooms.add(info);
					}

					if ("friend".equals(parser.getName())) {
						info.friendList.add(XmppConnection.getUsername(parser.nextText()));
					}

				} else if (eventType == XmlPullParser.END_TAG) {
					if ("muc".equals(parser.getName())) {
						break;
					}
				}
				eventType = parser.next();
			}

			return null;
		}
	}

	public void deleteOfflineMsg() {
		OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
		try {
			offlineManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (connection != null) {
			connection.removeConnectionListener(connectionListener);
			ProviderManager.getInstance().removeIQProvider("muc", "MZH");
			try {
				connection.disconnect();
			} catch (Exception e) {
				if(Constants.IS_DEBUG)
				Log.e("asmack dis", e.getMessage());
				e.printStackTrace();
			}
			finally{
				connection = null;
				xmppConnection = null;
			}
		}
		if(Constants.IS_DEBUG){}
	}
	
	
	public void reconnect(){
		new Thread(){
			@Override
			public void run() {
				try {
//					ContextUtil.isLeaving = true;
					closeConnection();

					login(Constants.XMPP_USERNAME, Constants.XMPP_PASSWORD);
					for (Room room : XmppConnection.getInstance(ctx).getMyRoom()) {
						MultiUserChat muc = new MultiUserChat(XmppConnection.getInstance(ctx).getConnection(), XmppConnection.getFullRoomname(room.name));
						DiscussionHistory history = new DiscussionHistory();
						history.setMaxChars(0);
						history.setSince(new Date());
						muc.join(Constants.XMPP_USERNAME, null, history, SmackConfiguration.getPacketReplyTimeout());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				super.run();
			}
		}.start();
	}
	
	/**
	 * @param account
	 * @param password
	 * @return
	 */
	public boolean login(String account, String password) {
		try {
			if (!getConnection().isAuthenticated() && getConnection().isConnected()) {

				getConnection().login(account, password);

				Presence presence = new Presence(Presence.Type.available);
				//CircleConstants.USER_STATUS = presence.getStatus();
				presence.setMode(Presence.Mode.available);
				getConnection().sendPacket(presence);

				roster = XmppConnection.getInstance(ctx).getConnection().getRoster();
				return true;
			}

		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * @param account
	 * @param password
	 * @return
	 */
	public IQ regist(String account, String password) {
		if (getConnection() == null)
			return null;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(getConnection().getServiceName());
		reg.setUsername(account);
		reg.setPassword(password);
//		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));

		PacketCollector collector = getConnection().createPacketCollector(filter);
		// collector.addPacketListener(packetListener, filter);
		getConnection().sendPacket(reg);

		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();

		return result;

	}
	
	
	/**
	 * @param pwd
	 * @return
	 */
	public boolean changPwd(String pwd){
    	try {
			getConnection().getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
    }
	
	public void setRecevier(String chatName, int chatType){
		if (getConnection() == null)
			return;

		if (chatType == ChatItem.CHAT) {
			ChatManager cm = XmppConnection.getInstance(ctx).getConnection().getChatManager();
			newchat = cm.createChat(getFullUsername(chatName), null);
		} else if (chatType == ChatItem.GROUP_CHAT) {
			try {
				mulChat = new MultiUserChat(getConnection(), getFullRoomname(chatName));
				DiscussionHistory history = new DiscussionHistory();
				history.setMaxChars(0);
				history.setSince(new Date());
				//history.setMaxStanzas(3);
			} catch (Exception e) {
				Log.e("mulChat=====eee",  e+">>>>"+e.getMessage());
			}

		}
	}

	@SuppressLint("NewApi")
	public void sendMsg(Context ctx, String chatName, String msg, int chatType) throws Exception {
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		
		if (msg.isEmpty()) {
			Tool.initToast(ctx, "sbx");
		} else {
			if (chatType == ChatItem.CHAT) {
				ChatManager cm = XmppConnection.getInstance(ctx).getConnection().getChatManager();
				Chat newchat = cm.createChat(getFullUsername(chatName), null);
				newchat.sendMessage(msg);
			}
			else if (chatType == ChatItem.GROUP_CHAT) {
				mulChat.sendMessage(msg);
			}
		}
	}

	@SuppressLint("NewApi")
	public void sendMsg(Context ctx, String msg, int chatType) throws Exception {
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		
		if (msg.isEmpty()) {
			Tool.initToast(ctx, "sbx");
		}
		else {
			if (chatType == ChatItem.CHAT) {
				newchat.sendMessage(msg);
			}
			else if (chatType == ChatItem.GROUP_CHAT) {
				mulChat.sendMessage(msg);
			}
		}
	}
	

	public void sendMsgWithParms2(String msg, String bodyType, String[] datas, int chatType) throws Exception {
		if (getConnection() == null){
			throw new Exception("XmppException");
		}

		MyMessage message = new MyMessage(bodyType);
		message.setBody(msg);

		if (chatType == ChatItem.CHAT) {
			newchat.sendMessage(message);
		}else if (chatType == ChatItem.GROUP_CHAT) {
			mulChat.sendMessage(msg+":::"+datas[0]);
		}
	}

	public void sendMsgWithParms(String msg, String[] parms, Object[] datas, int chatType) throws Exception {
		if (getConnection() == null){
			throw new Exception("XmppException");
		}

		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		for (int i = 0; i < datas.length; i++) {
			message.setProperty(parms[i], datas[i]);
		}
		message.setBody(msg);
		if (chatType == ChatItem.CHAT) {
			newchat.sendMessage(message);
		}
		else if (chatType == ChatItem.GROUP_CHAT) {
			mulChat.sendMessage(msg+":::"+datas[0]);
		}
	}

	public void sendimgMsg(Context ctx, String msg, int chatType, int iswork) throws Exception {
		if (getConnection() == null){
			throw new Exception("XmppException");
		}
		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		message.setBody(msg);
		message.setProperty("iswork", iswork);
		if (msg.isEmpty()) {
			Tool.initToast(ctx, "sbx");
		}
		else {
			if (chatType == ChatItem.CHAT) {
				newchat.sendMessage(message);
			}
			else if (chatType == ChatItem.GROUP_CHAT) {
				mulChat.sendMessage(message);
			}
		}
	}

	public List<Msg> getOfflineList() {
		return offlineList;
	}

	public void closeConn() {
		clearData();
		closeXmppService();
	}
	private void clearData() {
		MessageConstants.msgMap.clear();
		this.offlineList.clear();
		MessageConstants.roster.clear();
		MessageConstants.rosterMap.clear();
		MessageConstants.friendslist.clear();
        MessageConstants.friendslist_all.clear();
		MessageConstants.listMsg.clear();
	}

	public void closeXmppService() {
		Intent intentService = new Intent(Constants.CONTEXT, XmppService.class);
		Constants.CONTEXT.stopService(intentService);
	}

	
	/**
	 * @param key
	 * @return
	 */
	public List<String> searchUser(String key){
		List<String> userList = new ArrayList<String>();
		try{
			UserSearchManager search = new UserSearchManager(getConnection());
			Form searchForm = search.getSearchForm("search."+Constants.XMPP_HOSTNAME);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", key);
			ReportedData data = search.getSearchResults(answerForm,"search."+Constants.XMPP_HOSTNAME);
			
			Iterator<Row> it = data.getRows();
			Row row=null;
			while(it.hasNext()){
				row=it.next();
				userList.add(row.getValues("name").next().toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userList;
	}

	public List<GroupUser> searchMember2(String key){
		List<GroupUser> userList = new ArrayList<GroupUser>();
		try{
			UserSearchManager search = new UserSearchManager(getConnection());
			Form searchForm = search.getSearchForm("search."+Constants.XMPP_HOSTNAME);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", key);
			ReportedData data = search.getSearchResults(answerForm,"search."+Constants.XMPP_HOSTNAME);

			Iterator<Row> it = data.getRows();
			Row row=null;
			while(it.hasNext()){
				row=it.next();

				String name = row.getValues("name").next().toString();
				if(name.trim().equals("")){
					name = row.getValues("Username").next().toString();
				}
				userList.add(new GroupUser(row.getValues("jid").next().toString(), name, false));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userList;
	}

	public List<User> searchMember(String key){
		List<User> userList = new ArrayList<User>();

		if(key==null) return userList;

		try{
			UserSearchManager search = new UserSearchManager(getConnection());
			Form searchForm = search.getSearchForm("search."+Constants.XMPP_HOSTNAME);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", key);
			ReportedData data = search.getSearchResults(answerForm,"search."+Constants.XMPP_HOSTNAME);

			Iterator<Row> it = data.getRows();
			Row row=null;
			while(it.hasNext()){
				row=it.next();
				String name = row.getValues("name").next().toString();
				if(name.trim().equals("")){
					name = row.getValues("Username").next().toString();
				}

				User user=new User();
				user.setUsername(row.getValues("jid").next().toString().split("@")[0]);
				user.setName(name);
				userList.add(user);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userList;
	}

	/**
	 * @param userName  id
	 * @param
	 * @return
	 */
	public boolean addUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(getFullUsername(userName), userName, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addUser(String id, String userName) {
		if (getConnection() == null)
			return false;
		try {
			getConnection().getRoster().createEntry(id, userName, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		if (getConnection() == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@")){
				entry = getConnection().getRoster().getEntry(userName);
			}else{
				entry = getConnection().getRoster().getEntry(userName + "@" + getConnection().getServiceName());
			}
			if (entry == null){
				entry = getConnection().getRoster().getEntry(userName);
			}
			roster.removeEntry(entry);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param
	 */
	public boolean changeVcard(VCard vcard) {
		if (getConnection() == null)
			return false;
		try {
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			vcard.save(getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * @param file
	 */
	public Bitmap changeImage(File file) {
		Bitmap bitmap = null;
		if (getConnection() == null)
			return bitmap;
		try {
			VCard vcard = Constants.loginUser.vCard;
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());

			byte[] bytes;
			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
//			vcard.setAvatar(bytes, encodedImage);
// 			vcard.setEncodedImage(encodedImage);
//			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
			vcard.setField("avatar", encodedImage);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//			bitmap = FormatTools.getInstance().InputStream2Bitmap(bais);
			vcard.save(getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public User getuser(String key) {
		List<User> userList = new ArrayList<User>();
		try{
			UserSearchManager search = new UserSearchManager(getConnection());
			Form searchForm = search.getSearchForm("search."+Constants.XMPP_HOSTNAME);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", key);
			ReportedData data = search.getSearchResults(answerForm,"search."+Constants.XMPP_HOSTNAME);

			Iterator<Row> it = data.getRows();
			Row row=null;
			while(it.hasNext()) {
				row = it.next();
				User user = new User();
				user.setName(row.getValues("name").next().toString());
				user.setEmail(row.getValues("email").next().toString());
				//user.setVarCard(row.getValues("email").next().toString());
				userList.add(user);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userList.get(0);
	}

	public VCard getUservcard(String user) {  //
		try {
			VCard vcard = new VCard();
			vcard.load(getConnection(), user + "@" + Constants.XMPP_HOSTNAME);
			if (vcard != null)
				return vcard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}


	/**
	 * 创建房间
	 * @param roomName		房间名称
	 * @param description		描述
	 */
	public MultiUserChat createRoom(Context ctx, String roomName, String description) {//String user,
		if (getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(getConnection(), roomName + "@conference." + getConnection().getServiceName());
			// 创建聊天室
			muc.create(roomName);

			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
//			List<String> owners = new ArrayList<String>();
//			owners.add(getConnection().getUser());// 用户JID
//			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			// if (!password.equals("")) {
			// // 进入是否需要密码
//			 submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",false);
			// // 设置进入密码
			// submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			// }
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 设置描述
			submitForm.setAnswer("muc#roomconfig_roomdesc", description);
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", false);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", true);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
//			muc.addMessageListener(new TaxiMultiListener());
		} catch (XMPPException e) {
			Tool.initToast(ctx, "网络不给力,请重试");
			Log.e("you wenti", "网络不给力,请重试" + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return muc;
	}


//	public List<Friend> getAllFriends() {
//		List<Friend> list = new ArrayList<Friend>();
//		if (roster == null) {
//			roster = XmppConnection.getInstance().getConnection().getRoster();
//		}
//		
//		Collection<RosterEntry> entries = roster.getEntries();
//		
//		for(RosterEntry entry : entries){
//			list.add(new Friend(XmppConnection.getUsername(entry.getUser()),entry.getType()));
//		}
//		return list;
//	}
//	

	
//	public List<Friend> getFriendListAll() {
//		return friendListAll;
//	}

	public List<Room> getMyRoom() {
		return myRooms;
	}

	/**
	 * @param user
	 * @param restart
	 * @param roomsName
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName, boolean restart) {
		if (getConnection() == null)
			return null;
		try {
			mulChat = new MultiUserChat(getConnection(), getFullRoomname(roomsName));
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			history.setSince(new Date());
			mulChat.join(user, null, history, SmackConfiguration.getPacketReplyTimeout());
			if(Constants.IS_DEBUG){
				Log.e("muc", "");
			}
			return mulChat;
		} catch (Exception e) {
			e.printStackTrace();
			if(Constants.IS_DEBUG)
			Log.e("muc", ".");
			return null;
		}
		finally{
		}
	}
	
	public void leaveMuc(String roomName){
		try {
			mulChat.leave();
			//mulChat.revokeMembership(XmppConnection.getFullUsername(Constants.XMPP_USERNAME));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(Constants.IS_DEBUG){
			Log.e("muc", "");
		}
	}

	/**
	 * @param fullUsername
	 * @return
	 */
	public static String getUsername(String fullUsername){
		return fullUsername.split("@")[0];
	}
	
	/**
	 * @param username
	 * @return
	 */
	public static String getFullUsername(String username){
		return username + "@" + Constants.XMPP_HOSTNAME;
	}
	
	/**
	 * @param fullRoomname
	 * @return
	 */
	public static String getRoomName(String fullRoomname) {
		return fullRoomname.split("@")[0];
	}
	
	/**
	 * @param fullRoomname
	 * @return
	 */
	public static String getRoomUserName(String fullRoomname) {
		return fullRoomname.split("/")[1];
	}

	/**
	 * @param roomName
	 * @return
	 */
	public static String getFullRoomname(String roomName){
		return roomName + "@conference."+ Constants.XMPP_HOSTNAME;
	}
	
	/**
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/*
	public static String requestService(String url, Map<String, String> param){
		if(CircleConstants.IS_DEBUG)
		Log.e("url", url);
		String result = "";

		try {
			DefaultHttpClient client = getNewHttpClient();

			HttpPost request = new HttpPost(url);
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			
//			if (CircleConstants.USER_NAME!="" && !param.containsKey("userName")) {
//				param.put("userName", CircleConstants.USER_NAME);
//			}

			for (Map.Entry<String, String> entry : param.entrySet()) {
				paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				if(CircleConstants.IS_DEBUG)
				Log.e("json parm", entry.getKey()+":"+entry.getValue());
			}
			HttpEntity entity1 = new UrlEncodedFormEntity(paramList, "UTF-8");

			request.setEntity(entity1);

			HttpResponse response = client.execute(request);

			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == 201 || stateCode == 200) {
				HttpEntity entity = response.getEntity();

				result = EntityUtils.toString(entity, HTTP.UTF_8);
				if(CircleConstants.IS_DEBUG)
				Log.e("json", result);
			} else {
				result = "";
			}
			request.abort();
		} catch (Exception e) {
			e.printStackTrace();
			if(CircleConstants.IS_DEBUG)
			Log.e("json", e.toString());
		} finally {
			//
			new DefaultHttpClient().getConnectionManager().shutdown();
		}
		return result;
	}

	private static DefaultHttpClient getNewHttpClient() {
		BasicHttpParams timeoutParams = new BasicHttpParams();

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

//			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			//
			HttpConnectionParams.setConnectionTimeout(timeoutParams, 30000);
			HttpConnectionParams.setSoTimeout(timeoutParams, 150000);

			HttpProtocolParams.setVersion(timeoutParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(timeoutParams, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(timeoutParams, registry);

			return new DefaultHttpClient(ccm, timeoutParams);
		} catch (Exception e) {

			return new DefaultHttpClient(timeoutParams);
		}
	}
	*/

	public static void getUserinfo(final Context ctx){
		new Thread(new Runnable() {
			@Override
			public void run() {
				User u;
				User user;
				Bitmap bmp;
				try {
					MessageConstants.friendslist.clear();
					MessageConstants.friendslist_all.clear();

					VCard vcard=XmppConnection.getInstance(ctx).getUservcard(Constants.XMPP_USERNAME);
					user=new User();
					user.setUsername(Constants.XMPP_USERNAME);
					user.setName(Constants.XMPP_NICKNAME);
					user.setMobile(Constants.XMPP_USERNAME);

					bmp= ImageUtil.getBitmapFromBase64String(vcard.getField("headimg"));
					if(bmp!=null){
						user.setBitmap(bmp);
					}

					MessageConstants.friendslist_all.add(user);

					roster = XmppConnection.connection.getRoster();
					Collection<RosterEntry> entries = roster.getEntries();
					for (final RosterEntry entry : entries) {
						if(entry.getType() == RosterPacket.ItemType.to || entry.getType() == RosterPacket.ItemType.from){
							continue;
						}

						if(entry.getType() == RosterPacket.ItemType.none){
							XmppConnection.getInstance(ctx).removeUser(entry.getUser());
							MsgDbHelper.getInstance(ctx).delChatMsg(entry.getUser().split("@")[0]);
							NewMsgDbHelper.getInstance(ctx).delNewMsg(entry.getUser().split("@")[0]);
							continue;
						}

						if (XmppConnection.connection == null || !XmppConnection.connection.isConnected())
							return;

						vcard=XmppConnection.getInstance(ctx).getUservcard(entry.getUser().split("@")[0]);
						user=new User();
						user.setUsername(entry.getUser().split("@")[0]);
						if(vcard.getField("Name")!=null && !vcard.getField("Name").equals("")){
							user.setName(vcard.getField("Name"));
						}else{
							user.setName(entry.getUser().split("@")[0]);
						}
						user.setMobile(entry.getUser().split("@")[0]);

						bmp=ImageUtil.getBitmapFromBase64String(vcard.getField("headimg"));
						if(bmp!=null){
							user.setBitmap(bmp);
						}
						MessageConstants.friendslist.add(user);
						MessageConstants.friendslist_all.add(user);
					}
					ctx.sendBroadcast(new Intent("ChatNewMsg"));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}




}
