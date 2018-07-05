/**
 * 
 */
package com.sylar.ucmlmobile;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.activity.HomeActivity;
import com.sylar.adapter.SearchAdapter;
import com.sylar.constant.CircleConstants;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.fragment.CircleFragment;
import com.sylar.model.ChatItem;
import com.sylar.model.Room;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.BaseView;
import com.xiaomi.mipush.sdk.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCOwner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sylar.ucmlmobile.XmppConnection.getFullUsername;

/**
 * @author MZH
 *
 */
public class RoomMemActivity extends BaseActivity {
	@BindView(R.id.ll_back)
	LinearLayout llBack;
	@BindView(R.id.tv_right)
	TextView tvRight;
	@BindView(R.id.ll_right)
	LinearLayout llRight;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;
	@BaseView
    ListView listView;

	private SearchAdapter adapter;
	public static String roomname;
	private List<String> members = new ArrayList<String>();
	public static boolean isExit = false;
	public static int f=0;
	Context ctx;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_room_mem);
		ButterKnife.bind(this);

		ctx=this;
		ContextUtil.ctx=this;
		CloseActivityClass.activityList.add(this);

		roomname = getIntent().getStringExtra("roomname");
		lhTvTitle.setText("圈子成员");
		llRight.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setText("添加");
	}

	@OnClick({R.id.ll_back, R.id.ll_right})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
				finish();
				break;

			case R.id.ll_right:
				Collection<Affiliate> entries = null;
				try {
					entries = XmppConnection.mulChat.getOwners();
				} catch (XMPPException e) {
					e.printStackTrace();
				}

				for(Affiliate entry : entries) {
					if(!Constants.XMPP_USERNAME.equals(entry.getJid().split("@")[0]) ){
						Tool.initToast(getApplicationContext(), "只有圈主才能添加成员！");
						return;
					}
				}

				Intent intent = new Intent(ctx, InviteFriendActivity.class);
				intent.putExtra("roomname", roomname);
				intent.putStringArrayListExtra("members", (ArrayList<String>) members);
				startActivity(intent);
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		ContextUtil.isback=0;
		ContextUtil.ctx = this;

		if (isExit) {
			isExit = false;
			finish();
			return;
		}

		XmppConnection.getInstance().reconnect();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		adapter = new SearchAdapter(ctx);
		for (Room room : XmppConnection.getInstance().getMyRoom()) {
			if (room.name.equals(roomname)) {
				members = room.friendList;
				for (String mem : room.friendList) {
					adapter.add(mem);
				}
			}
		}

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(adapter.getItem(position).equals(Constants.XMPP_USERNAME)){
					Tool.initToast(ctx, "不能和自己聊天！");
					return;
				}

				Intent intent = new Intent(ctx, NewChatActivity.class);
				intent.putExtra("SessionID", adapter.getItem(position));
				intent.putExtra("roomname", roomname);
				startActivity(intent);
			}
		});
		adapter.notifyDataSetChanged();

		new XmppLoadThread(ctx) {

			@Override
			protected Object load() {
				return  null;
			}

			@Override
			protected void result(Object object) {
				XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
			}

		};

	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}

	public void onClick(View v){
		switch (v.getId()) {

		default:
			break;
		}
	}

}
