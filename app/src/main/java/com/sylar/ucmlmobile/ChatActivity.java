package com.sylar.ucmlmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.circlegit.MainActivity;
import com.example.administrator.circlegit.R;
import com.sylar.model.ChatItem;
import com.sylar.unit.CloseActivityClass;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends Activity {
	
	private static MessageAdapter adapter;
	private String sessionID;
	private String msgText;
	private EditText textView;
	private TextView labelView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		CloseActivityClass.activityList.add(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_message);
		
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		ListView listView = (ListView) findViewById(R.id.listview_message);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		textView = (EditText) findViewById(R.id.formclient_text);
		findViewById(R.id.btn_sendMessage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String msg = textView.getText().toString().trim();
				if(msg.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_SHORT).show();
					return;
				}
				msgText = msg;
				MessageConstants.listMsg.add(new Msg(Constants.XMPP_USERNAME, msg, TimeRender.getDate(), "OUT", sessionID));
				adapter.notifyDataSetChanged();

				SendMessage();
				textView.setText("");
			}
		});
		
		sessionID = getIntent().getStringExtra("SessionID");
		labelView = (TextView) findViewById(R.id.session_label);
		labelView.setText(MessageConstants.findNameById(sessionID));
		adapter = new MessageAdapter(this,null);
		listView.setAdapter(adapter);

	}
	
	@Override
	public void onNewIntent(Intent intent) {
		sessionID = intent.getStringExtra("SessionID");
		labelView.setText(sessionID);
		adapter.notifyDataSetChanged();
	}
	
	public static Handler msgHandler = new Handler() {
		public void handleMessage(Message message) {
			switch(message.what) {
			case 0:
				Log.i("XmppDemo","xxxxxxx");
				adapter.notifyDataSetChanged();
			}
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler sendMsgHandler = new Handler() {
		public void handleMessage(Message message) {
			switch(message.what){
			case 200:
				SendMessage();
			}  
		}
	};
	
	
	private void SendMessage() {
		try {
			ChatManager cm = XmppConnection.getInstance().getConnection().getChatManager();
			final Chat chat = cm.createChat(sessionID+"@"+Constants.XMPP_HOST, null);
			chat.sendMessage(msgText);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
