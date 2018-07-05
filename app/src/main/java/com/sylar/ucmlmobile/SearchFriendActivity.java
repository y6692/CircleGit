package com.sylar.ucmlmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.circlegit.MainActivity;
import com.example.administrator.circlegit.R;
import com.sylar.fragment.FriendFragment;
import com.sylar.fragment.FriendsFragment;
import com.sylar.fragment.QunFragment;
import com.sylar.fragment.SearchFriendFragment;
import com.sylar.model.ChatItem;
import com.sylar.model.User;
import com.sylar.unit.CircularImage;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.Tool;
import com.sylar.view.SearchView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchFriendActivity extends BaseActivity {
	@BindView(R.id.ll_back)
	LinearLayout llBack;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;
	@BindView(R.id.search_view)
	SearchView searchView;
	@BindView(R.id.tab)
	TabLayout tab;
	@BindView(R.id.viewpager)
	ViewPager vp;

	private SearchFriendFragment searchfriendFragment;
	private QunFragment qunFragment;
	private MyPagerAdapter myPagerAdapter;
	List<GroupUser> memberlist=new ArrayList<GroupUser>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends);
		ButterKnife.bind(this);

		ContextUtil.ctx = this;
		CloseActivityClass.activityList.add(this);

		lhTvTitle.setText("添加");
		myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		vp.setAdapter(myPagerAdapter);
		vp.setOffscreenPageLimit(2);
		tab.setupWithViewPager(vp);

		searchView.setBgColor(0xffeeeeee);
		searchView.setTvCancelVisible(View.GONE);
		searchView.setMaxWordNum(15);
		searchView.setOnThinkingClickListener(new SearchView.OnThinkingClickListener() {
			@Override
			public void onThinkingClick(String text) {
				switch (tab.getSelectedTabPosition()){
					case 0:
						searchfriendFragment.filter(text);
						break;
					case 1:
						break;
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		ContextUtil.isback=0;
		ContextUtil.ctx = this;
	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}


	class MyPagerAdapter extends FragmentPagerAdapter {
		private String[] titles = new String[]{"好友", "群"};
		private List<Fragment> fragmentList;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			searchfriendFragment = new SearchFriendFragment();
			qunFragment = new QunFragment();

			fragmentList = new ArrayList<>();
			fragmentList.add(searchfriendFragment);
			fragmentList.add(qunFragment);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}


	@OnClick({R.id.ll_back})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
				finish();
				break;
		}
	}


	private class MyContactAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return memberlist.size();
		}

		@Override
		public Object getItem(int position) {
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
			String nickName = MessageConstants.getNameById(memberlist.get(position).id.split("@")[0]);
			contactName.setText(nickName);
			ImageView addView = (ImageView) convertView.findViewById(R.id.add);
			CircularImage contacthead = (CircularImage) convertView.findViewById(R.id.contacthead);
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
}
