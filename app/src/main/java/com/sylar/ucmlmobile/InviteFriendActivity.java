package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.model.ChatItem;
import com.sylar.model.Room;
import com.sylar.model.User;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InviteFriendActivity extends Activity {

	@BindView(R.id.ll_back)
	LinearLayout llBack;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;

	public EditText searchText;
	ListView listView;
	MyContactAdapter myContactAdapter;
	List<User> memberlist=new ArrayList<User>();
	private String roomname;
	public static int f=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitefriends);
		ButterKnife.bind(this);
		ContextUtil.ctx = this;

		roomname = getIntent().getStringExtra("roomname");
		CloseActivityClass.activityList.add(this);
		lhTvTitle.setText("添加成员");
		searchText = (EditText) findViewById(R.id.searchText);
		listView = (ListView) findViewById(R.id.listview);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		myContactAdapter=new MyContactAdapter();
		listView.setAdapter(myContactAdapter);
		myContactAdapter.notifyDataSetChanged();

		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals("")) {
				}else{
					memberlist = XmppConnection.getInstance().searchMember(s.toString());
					myContactAdapter.notifyDataSetChanged();
				}
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				for (Room room : XmppConnection.getInstance().getMyRoom()) {
					if(room.name.equals(roomname) && room.friendList.indexOf(XmppConnection.getFullUsername(myContactAdapter.getItem(position).getUsername()).split("@")[0])!=-1){
						Tool.initToast(getApplicationContext(), "此人已是圈子成员，不能重复邀请！");
						return;
					}
				}

				XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);

				try {
					XmppConnection.mulChat.grantMembership(XmppConnection.getFullUsername(myContactAdapter.getItem(position).getUsername()));
					XmppConnection.mulChat.invite(XmppConnection.getFullUsername(myContactAdapter.getItem(position).getUsername()), roomname);;
					XmppConnection.getInstance().reconnect();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("invite===eee", e+"==="+e.getMessage());
				}
				Tool.initToast(getApplicationContext(), "邀请成功");
			}
		});
	}

	@OnClick({R.id.ll_back, R.id.ll_right})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
				finish();
				break;
		}
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Tool.initToast(getApplicationContext(), "邀请成功");
			}else if (msg.what == 1) {
			}
		}
	};

	private class MyContactAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return memberlist.size();
		}

		@Override
		public User getItem(int position) {
			return memberlist.get(position);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.member_add, null);
			TextView contactName = (TextView) convertView.findViewById(R.id.message_contact_name);
			contactName.setText(memberlist.get(position).getName());
			return convertView;
		}

	}

	
	public static Handler msgHandler = new Handler() {
		public void handleMessage(Message message) {
			switch(message.what) {
			case 0:
			}
		}
	};

	
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

	@Override
	protected void onResume() {
		super.onResume();

		ContextUtil.isback=0;
		ContextUtil.ctx = this;
		XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}
}
