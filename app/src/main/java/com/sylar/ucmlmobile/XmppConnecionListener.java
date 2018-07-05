package com.sylar.ucmlmobile;

import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StreamError;

public class XmppConnecionListener implements ConnectionListener {
	//ExchangeService exchangeservice;
	@Override
	public void connectionClosed() {
		Log.e("smack xmpp===", "close");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
        Log.e("smack xmpp===", e.getMessage());
        if (e instanceof XMPPException) {
            XMPPException xe = (XMPPException) e;
            final StreamError error = xe.getStreamError();
            String errorCode = "";
            if (error != null) {  
                errorCode = error.getCode();// larosn 0930  
                Log.e("IMXmppManager====", "连接断开，错误码" + errorCode);
                if (errorCode.equalsIgnoreCase("conflict")) {// 被踢下线  
  
                	ContextUtil.getInstance().sendBroadcast(new Intent("conflict"));
                }
			}
        }
	}

	@Override
	public void reconnectingIn(int arg0) {
		Log.e("smack xmpp===", "reconnectingIn");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Log.e("smack xmpp===", "reconnectionFailed");
	}

	@Override
	public void reconnectionSuccessful() {
		Log.e("smack xmpp===", "reconnectionSuccessful");
	}

}
