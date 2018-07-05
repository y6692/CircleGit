package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.ImgHandler;

import java.util.ArrayList;
import java.util.List;


public class MessageActivity extends Activity {
	
	private offlineMsgAdapter offlineAdapter;
	
	private List<Msg> offlineList = new ArrayList<Msg>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_offmessage);
		CloseActivityClass.activityList.add(this);
		
		ListView listView = (ListView) findViewById(R.id.offline_listview);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		offlineList = XmppConnection.getInstance().getOfflineList();
		offlineAdapter = new offlineMsgAdapter();
		listView.setAdapter(offlineAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
				ListView listView = (ListView) parent;
				Msg msg = (Msg) listView.getItemAtPosition(position);
				
				String sessionid = msg.sessionid;
				if(!MessageConstants.msgMap.containsKey(sessionid)) {
					List<Msg> msgList = new ArrayList<Msg>();
					msgList.add(msg);
				}
				Intent i = new Intent(MessageActivity.this, NewChatActivity.class);
				i.putExtra("SessionID", sessionid);
				startActivity(i);
			}
		});

		XmppConnection.getInstance().deleteOfflineMsg();
	}
	
	public class offlineMsgAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		
		@Override
		public int getCount() {
			return offlineList.size();
		}

		@Override
		public Object getItem(int position) {
			return offlineList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) MessageActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(offlineList.get(position).from.equals("IN")) {
				convertView = inflater.inflate(R.layout.formclient_chat_in, null);
			}
			else {
				convertView = inflater.inflate(R.layout.formclient_chat_out, null);
			}
			
			TextView useridView = (TextView) convertView.findViewById(R.id.formclient_row_userid);
			ImageView head = (ImageView) convertView.findViewById(R.id.headImg);
			TextView dateView = (TextView) convertView.findViewById(R.id.formclient_row_date);
			TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
			head.setImageDrawable(ImgHandler.ToCircularBig(R.drawable.default_icon));
			useridView.setText(offlineList.get(position).userid);
			dateView.setText(offlineList.get(position).date);
			msgView.setText(offlineList.get(position).msg);
			return convertView;
		}
	}
}
