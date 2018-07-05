package com.sylar.ucmlmobile;

//import com.sylar.ucmlmobile.ReConnectService.PingIQ;

public class XmppService {

//	public static final Random random = new Random(System.currentTimeMillis());
//	public XMPPConnection connection = null;
//	private long startTime = 0;
//	private long endTime = 0;
//	private long consumingTime = 0;
//	private boolean ProcessPacket = false;
//	private int ProcessCount=0;
//	private XmppMessageListener xmppMessageListener;
//	
//	public WakeLock mWakeLock=null;
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		Log.i("XmppDemo", "Service-onBind");
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		Log.i("XmppDemo", "Service-onDestroy");
//		super.onDestroy();
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public void onStart(Intent intent, int startId) {
//		Log.i("XmppDemo", "Service-onStart");
//		super.onStart(intent, startId);
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i("XmppDemo", "Service-onStartCommon");
//		flags = START_REDELIVER_INTENT;
//		return super.onStartCommand(intent, flags, startId);
//	}
//
//	@Override
//	public void onCreate() {
//		Log.i("XmppDemo", "Service-onCreate");
//		xmppMessageListener =new XmppMessageListener();
//
//		connection = XmppUtils.getInstance().getConnection();
//
//		// iq提供者
//		ProviderManager.getInstance().addIQProvider("ping", "urn:xmpp:ping",
//				new PingProvider());
//		connection.addPacketListener(xmppMessageListener,new PacketTypeFilter(Message.class));
//		connection.addConnectionListener(new ConnectionListener() {
//
//			@Override
//			public void reconnectionSuccessful() {
//				Log.i("connection", "XMPP连接OK");
//			}
//
//			@Override
//			public void reconnectionFailed(Exception arg0) {
//				Log.i("connection", "XMPP重连失败");
//			}
//
//			@Override
//			public void reconnectingIn(int arg0) {
//				Log.i("connection", "XMPP正在重连");
//			}
//
//			@Override
//			public void connectionClosedOnError(Exception arg0) {
//				Log.i("connection", "XMPP连接错误关闭");
//
//			}
//
//			@Override
//			public void connectionClosed() {
//
//				Log.i("connection", "XMPP连接断开！");
//			}
//		});
//
//	}
//
//	public class PingIQ extends IQ {
//
//		public static final String ELEMENT = "ping";
//		public static final String NAMESPACE = "urn:xmpp:ping";
//
//		@Override
//		public String getChildElementXML() {
//			StringBuffer sb = new StringBuffer();
//			sb.append("<").append(ELEMENT).append(" xmlns=\"")
//					.append(NAMESPACE).append("\">");
//
//			sb.append("</").append(ELEMENT).append(">");
//			return sb.toString();
//		}
//	}
//
//	public class PingIQProvider implements IQProvider {
//
//		@Override
//		public IQ parseIQ(XmlPullParser parser) throws Exception {
//			// TODO Auto-generated method stub
//			PingIQ iq = new PingIQ();
//			return iq;
//		}
//	}
//
//	public class Ping extends IQ {
//		@Override
//		public String getChildElementXML() {
//			StringBuilder buf = new StringBuilder();
//			buf.append("<ping xmlns='urn:xmpp:ping'/>");
//			return buf.toString();
//		}
//	}
//
//	public class PingProvider implements IQProvider {
//
//		@Override
//		public IQ parseIQ(XmlPullParser parser) throws Exception {
//			// TODO Auto-generated method stub
//			Ping iq = new Ping();
//			return iq;
//		}
//	}
//
//	public class PingPacketFilter implements PacketFilter {
//
//		@Override
//		public boolean accept(Packet packet) {
//			if (packet instanceof Ping) {
//				return true;
//			} else {
//				return false;
//			}
//		}
//	}
//
//	public class Pong extends IQ {
//
//		public Pong() {
//			this.setType(Type.ERROR);
//		}
//
//		@Override
//		public String getChildElementXML() {
//			StringBuilder buf = new StringBuilder();
//			buf.append("<ping xmlns='urn:xmpp:ping'/>");
//			buf.append("<error type='cancel'/>");
//			buf.append("<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>");
//			buf.append("</error>");
//			return buf.toString();
//		}
//	}
//
//	public class PingPacketListener implements PacketListener {
//		private XMPPConnection connection;
//
//		public PingPacketListener(XMPPConnection connection) {
//			this.connection = connection;
//		}
//
//		@Override
//		public void processPacket(Packet packet) {
//			if (packet == null) {
//				return;
//			}
//			Pong pong = new Pong();
//			String from = packet.getFrom();
//			String to = packet.getTo();
//			pong.setFrom(to);
//			pong.setTo(from);
//			this.connection.sendPacket(pong);
//		}
//	}
//
//	public String getTopActivity(Activity context) {
//		ActivityManager manager = (ActivityManager) context
//				.getSystemService(ACTIVITY_SERVICE);
//		return manager.getRunningTasks(1).get(0).topActivity.getClassName();
//	}
//
//	@SuppressWarnings("deprecation")
//	public void sendNotification(Message msg, String sessionID) {
//
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				this).setSmallIcon(R.drawable.ic_launcher)
//				.setContentTitle(sessionID + "发来消息")
//				.setContentText(msg.getBody());
//
//		NotificationManagerCompat managerCompat = NotificationManagerCompat
//				.from(this);
//
//		Notification notification = builder.build();
//
//		// Notification notification = new Notification();
//		notification.icon = R.drawable.notification;
//		notification.defaults = Notification.DEFAULT_LIGHTS;
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.when = System.currentTimeMillis();
//		notification.tickerText = sessionID + "发来消息";
//
//		Intent intent = new Intent(CircleConstants.CONTEXT, NewChatActivity.class);
//		intent.putExtra("SessionID", sessionID);
//		// intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
//				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		NotificationManager notificationManager = (NotificationManager) CircleConstants.CONTEXT
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		PendingIntent contentIntent = PendingIntent
//				.getActivity(CircleConstants.CONTEXT, 0, intent,
//						PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(CircleConstants.CONTEXT, "(" + sessionID
//				+ ")发来消息", msg.getBody(), contentIntent);
//
//		MessageMain.NotReadMsgCount++;
//		int msgCount = MessageMain.NotReadMsgCount;
//		try {
//			Field field = notification.getClass().getDeclaredField(
//					"extraNotification");
//
//			Object extraNotification = field.get(notification);
//
//			Method method = extraNotification.getClass().getDeclaredMethod(
//					"setMessageCount", int.class);
//
//			method.invoke(extraNotification, msgCount);
//		} catch (Exception e) {
//			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//		}
//		notificationManager.notify(CircleConstants.NOTIFICATION_ID, notification);
//
//
//	}


}
