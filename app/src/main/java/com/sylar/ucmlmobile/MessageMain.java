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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.MainActivity;
import com.example.administrator.circlegit.R;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.model.User;
import com.sylar.unit.CloseActivityClass;
import com.sylar.view.BaseView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MessageMain extends Activity {
	@BaseView(click="onClick")
    ImageView leftBtn;
	
	private MyMessageAdapter myMessageAdapter;
	private MyContactAdapter myContactAdapter;
	private MyRecentlyAdapter myRecentlyAdapter;
	private List<Msg> offlineList = new ArrayList<Msg>();
	private ListView listView;
	private TextView titleView,imagecontact,imagerecently,imagenotice;
	private FrameLayout page4;
	private LinearLayout Layout_notice;
	private ImageView linenotice,linecontact,linerecently,searchImg;
	public EditText searchText;
	private TextView countView;
	public static int NotReadMsgCount=0;
	Context ctx;
	public static int stop=0;
	public static int flag=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_main);
		CloseActivityClass.activityList.add(this);

		flag=0;

		ctx=this;
		listView = (ListView) findViewById(R.id.mymsg_listview);
		titleView = (TextView) findViewById(R.id.title_listview);
		imagecontact = (TextView) findViewById(R.id.image_contact);
		imagerecently = (TextView) findViewById(R.id.image_recently);
		imagenotice = (TextView) findViewById(R.id.image_notice);
		linenotice= (ImageView) findViewById(R.id.linenotice);
		linecontact= (ImageView) findViewById(R.id.linecontact);
		linerecently =(ImageView) findViewById(R.id.linerecently);
		searchImg =(ImageView) findViewById(R.id.searchImg);
		countView = (TextView) findViewById(R.id.countView);
		searchText = (EditText) findViewById(R.id.searchText);
		page4 =(FrameLayout) findViewById(R.id.page4);
		Layout_notice = (LinearLayout) findViewById(R.id.Layout_notice);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		Constants.loginUser = new User(XmppConnection.getInstance().xmppConnection.getUserInfo(null, XmppConnection.getInstance()));

		MessageConstants.roster.clear();
		MessageConstants.recently.clear();
		MessageConstants.group_user.clear();
		MessageConstants.group_user_all.clear();

		init();
		leftBtn =(ImageView) findViewById(R.id.leftBtn);
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(flag==0){
						handler.sendEmptyMessage(0);
						Thread.sleep(1000);

						while(stop==1){
							Thread.sleep(1000);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if(MainActivity.isconf==1){
					CloseActivityClass.exitClient(ctx);
				}

				MessageConstants.roster.clear();
				MessageConstants.recently.clear();
				updateCount();
				getRosterList();
				getRecentlyList();
				myMessageAdapter.notifyDataSetChanged();
				myRecentlyAdapter.notifyDataSetChanged();

			}
		}
	};

	private void init(){
		getRosterList();
		getRecentlyList();
		getGroupMember(null);
		
		myMessageAdapter = new MyMessageAdapter();
		myContactAdapter = new MyContactAdapter();
		myRecentlyAdapter = new MyRecentlyAdapter();
		
		updateCount();
		
		if(MessageConstants.roster.size() > 0) {
			linecontact.setImageResource(R.drawable.chatline);
			linenotice.setImageResource(R.drawable.chatlinevisibily); 
			linerecently.setImageResource(R.drawable.chatlinevisibily); 
			imagecontact.setTextSize(18);
			imagerecently.setTextSize(16);
			imagenotice.setTextSize(16);
			Layout_notice.setVisibility(View.GONE);
			listView.setAdapter(myMessageAdapter);
		}else {
			linecontact.setImageResource(R.drawable.chatlinevisibily); 
			linenotice.setImageResource(R.drawable.chatlinevisibily); 
			linerecently.setImageResource(R.drawable.chatline);
			imagecontact.setTextSize(16);
			imagerecently.setTextSize(18);
			imagenotice.setTextSize(16);
			Layout_notice.setVisibility(View.GONE);
			listView.setAdapter(myRecentlyAdapter);
		}
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
				ListView thisListView = (ListView) parent;
				String sessionid = null;
				if(listView.getAdapter().equals(myContactAdapter)) {
					GroupUser groupUser = (GroupUser) thisListView.getItemAtPosition(position);
					if(groupUser.isGroup) {
						return;
					}
					sessionid = groupUser.id;
				}else{
					String sessionID = (String) thisListView.getItemAtPosition(position);
					sessionid = sessionID;
				}
				
				
				if(!MessageConstants.msgMap.containsKey(sessionid)) {
					List<ChatItem> msgList = new ArrayList<ChatItem>();
					if(MessageConstants.rosterMap.containsKey(sessionid))
					{
						msgList = MessageConstants.rosterMap.get(sessionid);
					}
					else if(MessageConstants.recentlyMap.containsKey(sessionid))
					{
						msgList = MessageConstants.recentlyMap.get(sessionid);
					}
					MessageConstants.msgMap.put(sessionid, msgList);
				}
				
				Intent i = new Intent(MessageMain.this, NewChatActivity.class);
				i.putExtra("SessionID", sessionid);
				startActivity(i);
			}
		});

		XmppConnection.getInstance().deleteOfflineMsg();

		findViewById(R.id.image_contact).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
					updateCount();
					linecontact.setImageResource(R.drawable.chatline);
					linenotice.setImageResource(R.drawable.chatlinevisibily); 
					linerecently.setImageResource(R.drawable.chatlinevisibily); 
					imagecontact.setTextSize(18);
					imagerecently.setTextSize(16);
					imagenotice.setTextSize(16);
					Layout_notice.setVisibility(View.GONE);
					listView.setAdapter(myMessageAdapter);
					myMessageAdapter.notifyDataSetChanged();
			}
		});
		
		findViewById(R.id.image_recently).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				linecontact.setImageResource(R.drawable.chatlinevisibily); 
				linenotice.setImageResource(R.drawable.chatlinevisibily); 
				linerecently.setImageResource(R.drawable.chatline);
				imagecontact.setTextSize(16);
				imagerecently.setTextSize(18);
				imagenotice.setTextSize(16);				
				Layout_notice.setVisibility(View.GONE);
				listView.setAdapter(myRecentlyAdapter);
				myRecentlyAdapter.notifyDataSetChanged();
			}
		});
		
		
		findViewById(R.id.image_notice).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
					linecontact.setImageResource(R.drawable.chatlinevisibily); 
					linenotice.setImageResource(R.drawable.chatline);
					linerecently.setImageResource(R.drawable.chatlinevisibily);
					imagecontact.setTextSize(16);
					imagerecently.setTextSize(16);
					imagenotice.setTextSize(18);	
					Layout_notice.setVisibility(View.VISIBLE);
					listView.setAdapter(myContactAdapter);
					myContactAdapter.notifyDataSetChanged();
			}
		});	
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
					listView.setAdapter(null);
					MessageConstants.group_user.clear();
					getGroupMember(null);
					myContactAdapter =new MyContactAdapter();
					listView.setAdapter(myContactAdapter);
					myContactAdapter.notifyDataSetChanged();
				}else{
					listView.setAdapter(null);
					MessageConstants.group_user.clear();
					getGroupMember(s.toString());
					myContactAdapter =new MyContactAdapter();
					listView.setAdapter(myContactAdapter);
					myContactAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	float x1 = 0;  
	float x2 = 0;  
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		 if(event.getAction() == MotionEvent.ACTION_DOWN) {
			 x1 = event.getX();  
		 }
		 if(event.getAction() == MotionEvent.ACTION_UP) {
			 x2 = event.getX();  
			 if(x1 - x2 > 300) { 
				 
				 if(listView.getAdapter().equals(myContactAdapter)){
						linecontact.setImageResource(R.drawable.chatline);
						linenotice.setImageResource(R.drawable.chatlinevisibily); 
						linerecently.setImageResource(R.drawable.chatlinevisibily); 
						imagecontact.setTextSize(18);
						imagerecently.setTextSize(16);
						imagenotice.setTextSize(16);
						Layout_notice.setVisibility(View.GONE);
						listView.setAdapter(myMessageAdapter);
						myMessageAdapter.notifyDataSetChanged();
				 }else if(listView.getAdapter().equals(myMessageAdapter)){
						linecontact.setImageResource(R.drawable.chatlinevisibily); 
						linenotice.setImageResource(R.drawable.chatlinevisibily); 
						linerecently.setImageResource(R.drawable.chatline);
						imagecontact.setTextSize(16);
						imagerecently.setTextSize(18);
						imagenotice.setTextSize(16);				
						Layout_notice.setVisibility(View.GONE);
						listView.setAdapter(myRecentlyAdapter);
						myRecentlyAdapter.notifyDataSetChanged();
				 }else if(listView.getAdapter().equals(myRecentlyAdapter)){
						linecontact.setImageResource(R.drawable.chatlinevisibily); 
						linenotice.setImageResource(R.drawable.chatline);
						linerecently.setImageResource(R.drawable.chatlinevisibily);
						imagecontact.setTextSize(16);
						imagerecently.setTextSize(16);
						imagenotice.setTextSize(18);	
						Layout_notice.setVisibility(View.VISIBLE);
						listView.setAdapter(myContactAdapter);
						myContactAdapter.notifyDataSetChanged();
				 }				  
			 } else if(x2 - x1 > 300) {  
				 if(listView.getAdapter().equals(myContactAdapter)){
						linecontact.setImageResource(R.drawable.chatlinevisibily); 
						linenotice.setImageResource(R.drawable.chatlinevisibily); 
						linerecently.setImageResource(R.drawable.chatline);
						imagecontact.setTextSize(16);
						imagerecently.setTextSize(18);
						imagenotice.setTextSize(16);				
						Layout_notice.setVisibility(View.GONE);
						listView.setAdapter(myRecentlyAdapter);
						myRecentlyAdapter.notifyDataSetChanged();
				 }else if(listView.getAdapter().equals(myMessageAdapter)){
						linecontact.setImageResource(R.drawable.chatlinevisibily); 
						linenotice.setImageResource(R.drawable.chatline);
						linerecently.setImageResource(R.drawable.chatlinevisibily);
						imagecontact.setTextSize(16);
						imagerecently.setTextSize(16);
						imagenotice.setTextSize(18);	
						Layout_notice.setVisibility(View.VISIBLE);
						listView.setAdapter(myContactAdapter);
						myContactAdapter.notifyDataSetChanged();

				 }else if(listView.getAdapter().equals(myRecentlyAdapter)){
						linecontact.setImageResource(R.drawable.chatline);
						linenotice.setImageResource(R.drawable.chatlinevisibily); 
						linerecently.setImageResource(R.drawable.chatlinevisibily); 
						imagecontact.setTextSize(18);
						imagerecently.setTextSize(16);
						imagenotice.setTextSize(16);
						Layout_notice.setVisibility(View.GONE);
						listView.setAdapter(myMessageAdapter);
						myMessageAdapter.notifyDataSetChanged();

				 }				 
			 }
		 }
		 return super.dispatchTouchEvent(event);
	}

	public void getRosterList() {
		List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getOutMsg();
		if(moreChatItems.size() == 0)
			return;

		for(int i=0; i<moreChatItems.size(); i++) {
			List<ChatItem> rosterList= new ArrayList<ChatItem>();
			rosterList = MsgDbHelper.getInstance(getApplicationContext()).getoneMsg(moreChatItems.get(i).chatName);

			if(rosterList.size()>0){
				if(MessageConstants.roster.indexOf(rosterList.get(0).chatName)==-1){
					MessageConstants.roster.add(rosterList.get(0).chatName);
				}

				MessageConstants.rosterMap.put(rosterList.get(0).chatName, rosterList);
			}
		}
	}
	private void getRecentlyList() {
		List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getrecentlyMsg();
		if(moreChatItems.size() == 0)
			return;

		for(int i=0; i<moreChatItems.size(); i++) {
			List<ChatItem> rosterList= new ArrayList<ChatItem>();
			rosterList = MsgDbHelper.getInstance(getApplicationContext()).getonerecentlyMsg(moreChatItems.get(i).chatName);
			if(rosterList.size()>0) {
				if(MessageConstants.recently.indexOf(rosterList.get(0).chatName)==-1){
					MessageConstants.recently.add(rosterList.get(0).chatName);
				}

				MessageConstants.recentlyMap.put(rosterList.get(0).chatName, rosterList);
			}
		}
		
	}
	
	private void getGroupMember(String s) {
		if(MessageConstants.group_user.size() > 0)
			return;
		
		Intent intent = getIntent();
		String manList=null;
		if(intent.getExtras()!=null){
			manList= intent.getExtras().getString("manList");
		}else{
			manList=ContextUtil.manList;
		}

		if(manList!=null){
			JSONArray arr = new JSONArray();
			try {
				arr = new JSONArray(manList);
			}catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(arr != null) {
				List<String> groupList = new ArrayList<String>();
				for(int i=0;i<arr.length();i++) {
					JSONObject jObj = new JSONObject();
					try {
						jObj = arr.getJSONObject(i);
						String dept = jObj.getString("dept");
						if (!groupList.contains(dept)) {
							groupList.add(dept);
						}
					} catch(JSONException e) {
						e.printStackTrace();
					} 
				}
				if(s!=null){
					for(int g=0;g<groupList.size();g++) {
						MessageConstants.group_user.add(new GroupUser("", groupList.get(g), true));
						
						for(int j=0;j<arr.length();j++) {
							JSONObject jObjUser = new JSONObject();
							try {
								jObjUser = arr.getJSONObject(j);
								String id = jObjUser.getString("oid");
								String name = jObjUser.getString("name");
								String dept = jObjUser.getString("dept");
								if(groupList.get(g).equals(dept)) {
									if(name.contains(s)){
										MessageConstants.group_user.add(new GroupUser(id, name, false));
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}else{
					for(int g=0;g<groupList.size();g++) {
						MessageConstants.group_user.add(new GroupUser("", groupList.get(g), true));
						MessageConstants.group_user_all.add(new GroupUser("", groupList.get(g), true));
						
						for(int j=0;j<arr.length();j++) {
							JSONObject jObjUser = new JSONObject();
							try {
								jObjUser = arr.getJSONObject(j);
								String id = jObjUser.getString("oid");
								String name = jObjUser.getString("name");

								String dept = jObjUser.getString("dept");
								if(groupList.get(g).equals(dept)) {
									MessageConstants.group_user.add(new GroupUser(id, name, false));

									if(MessageConstants.group_user_all.size()<(groupList.size()+arr.length())){
										MessageConstants.group_user_all.add(new GroupUser(id, name, false));
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	private class MyMessageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		
		@Override
		public int getCount() {
			return MessageConstants.roster.size();
		}

		@Override
		public Object getItem(int position) {
			return MessageConstants.roster.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) MessageMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.message_roster, null);
			TextView rosterName = (TextView) convertView.findViewById(R.id.message_roster_name);
			TextView rostermsg= (TextView) convertView.findViewById(R.id.message_roster_last);
			TextView rostertime= (TextView) convertView.findViewById(R.id.message_roster_time);
			String namebyid=MessageConstants.roster.get(position);
			String msg=MessageConstants.rosterMap.get(namebyid).get(0).msg;
			String time=MessageConstants.rosterMap.get(namebyid).get(0).sendDate.substring(0, 12);

			if(msg.contains(Constants.SAVE_IMG_PATH)){
				msg ="[图片]";
			}else if(msg.contains(Constants.SAVE_SOUND_PATH)){
				msg ="[语音]";	
			}else if(msg.contains(Constants.SAVE_MOVIE_PATH)){
				msg ="[视频]";
			}else if(msg.contains("[/g0")){
				msg=("[动画表情]");
			}
			if(msg != null && msg.contains("[/f0")){ // 适配表情
				//rosterName.setText(ExpressionUtil.getText(ctx, StringUtil.Unicode2GBK(msg)));
				msg=("[表情]");
			}			
			rosterName.setText(MessageConstants.findNameById(namebyid));
			rostermsg.setText(msg);
			rostertime.setText(time);
			return convertView;
		}
		
	}
	
	private class MyRecentlyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		@Override
		public int getCount() {
			return MessageConstants.recently.size();
		}
		@Override
		public Object getItem(int position) {
			return MessageConstants.recently.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) MessageMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.message_recently, null);

			TextView rosterName = (TextView) convertView.findViewById(R.id.message_recently_name);
			TextView recentlymsg= (TextView) convertView.findViewById(R.id.message_recently_last);
			TextView rostertime =(TextView) convertView.findViewById(R.id.message_recently_time);
			TextView unreadcnt= (TextView) convertView.findViewById(R.id.unreadcnt);
			String namebyid=MessageConstants.recently.get(position);
			List<ChatItem> items=MessageConstants.recentlyMap.get(namebyid);
			String msg=items.get(items.size()-1).msg;
			String time =items.get(items.size()-1).sendDate.substring(0, 12);
			if(getrecently(namebyid)){
				unreadcnt.setVisibility(View.VISIBLE);
			}else{
				unreadcnt.setVisibility(View.GONE);
			}

			rosterName.setText(MessageConstants.findNameById(namebyid));
			if(msg.contains(Constants.SAVE_IMG_PATH)){
				msg ="[图片]";
			}else if(msg.contains(Constants.SAVE_SOUND_PATH)){
				msg ="[语音]";	
			}else if(msg.contains(Constants.SAVE_MOVIE_PATH)){
				msg ="[视频]";
			}else if(msg.contains("[/g0")){
				msg=("[动画表情]");
			}
			if(msg != null && msg.contains("[/f0")){ // 适配表情
				//recentlymsg.setText(ExpressionUtil.getText(ctx, StringUtil.Unicode2GBK(msg)));
				msg=("[表情]");
			}

			recentlymsg.setText(msg);
			rostertime.setText(time);
			return convertView;
		}
		
	}
	
	private boolean getrecently(String namebyid){
		for(int i=0;i<MessageConstants.recentlyMap.get(namebyid).size();i++){
			if(MessageConstants.recentlyMap.get(namebyid).get(i).isRead==0){
				return true;
			}
		}
		return false;
	}
	
	private class MyContactAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		
		@Override
		public int getCount() {
			return MessageConstants.group_user.size();
		}

		@Override
		public Object getItem(int position) {
			return MessageConstants.group_user.get(position);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			this.inflater = (LayoutInflater) MessageMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(MessageConstants.group_user.get(position).isGroup) {
				convertView = inflater.inflate(R.layout.message_contact_group, null);
			}else {
				convertView = inflater.inflate(R.layout.message_contact_member, null);
			}
			TextView contactName = (TextView) convertView.findViewById(R.id.message_contact_name);
			contactName.setText(MessageConstants.group_user.get(position).name);
			return convertView;
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case PicSrcPickerActivity.CROP:
				if (page4.getVisibility() == View.VISIBLE) {
				}
				break;
				
			default:
				break;
			}
		}
	}
	@Override
	public void onResume(){
		super.onResume();
	}
	public void updateCount() {
		// 更新界面
		int count = NewMsgDbHelper.getInstance(getApplicationContext()).getMsgCount();
		
		if (count>0) {
			countView.setVisibility(View.VISIBLE);
			countView.setText(""+count);
		}
		else {
			countView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		flag=1;
		super.onBackPressed();
	}

}
