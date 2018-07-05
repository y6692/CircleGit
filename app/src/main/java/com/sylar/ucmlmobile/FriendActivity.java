/**
 * 
 */
package com.sylar.ucmlmobile;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.constant.CircleConstants;
import com.sylar.dao.GetHttpUserApplaction;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.model.User;
import com.sylar.unit.CircularImage;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.BaseView;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.packet.MUCAdmin;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author MZH
 *
 */
public class FriendActivity extends BaseActivity {
	@BindView(R.id.ll_back)
	LinearLayout llBack;
	@BindView(R.id.tv_right)
	TextView tvRight;
	@BindView(R.id.ll_right)
	LinearLayout llRight;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;
	@BindView(R.id.emailView)
	TextView emailView;
	@BaseView(click="onClick")
    Button operBtn, addFriend;
	@BaseView(click="onClick")
	ImageView chat;
	@BaseView
    TextView nameView, sexView, phoneView;
	@BindView(R.id.headView)
	CircularImage headView;

	private String username;
	private String roomname;
	public static int f=0;
	public static int kick=0;
	Context ctx;
	User user;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_friend);
		ButterKnife.bind(this);
		ctx=this;
		ContextUtil.ctx=this;
		CloseActivityClass.activityList.add(this);

		username = getIntent().getStringExtra("SessionID");
		roomname = getIntent().getStringExtra("roomname");
		VCard vcard= XmppConnection.getInstance().getUservcard(username);
		nameView.setText(vcard.getField("Name"));

		try {
			Collection<Affiliate> entries = XmppConnection.mulChat.getOwners();

			for(Affiliate entry : entries) {
				if(!Constants.XMPP_USERNAME.equals(entry.getJid().split("@")[0]) && roomname!=null && !roomname.equals("") ){
					operBtn.setVisibility(View.GONE);
				}else{
					addFriend.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			Log.e("fr=====eee", e+" - " + e.getMessage());
		}

		if (username.equals(Constants.XMPP_USERNAME)) {
			operBtn.setVisibility(View.GONE);
			addFriend.setVisibility(View.GONE);
			chat.setVisibility(View.GONE);
		}

		if(roomname==null || roomname.equals("")){//个人
			lhTvTitle.setText("好友资料");
			operBtn.setText("删除好友");
			addFriend.setVisibility(View.GONE);
		}else{//群
			lhTvTitle.setText("成员资料");
		}

		phoneView.setText(username);
		Bitmap  bmp= ImageUtil.getBitmapFromBase64String(vcard.getField("headimg"));
		if(bmp!=null){
			headView.setImageBitmap(bmp);
		}
		sexView.setText(vcard.getField("sex"));
		emailView.setText(vcard.getField("email"));
	}


	@OnClick({R.id.ll_back})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
				finish();
				break;
		}
	}
	
	public void onClick(View v){
		switch (v.getId()) {

		case R.id.operBtn:
			operBtn.setBackgroundResource(R.drawable.btndel2);

			AlertDialog.Builder normalDialog = new AlertDialog.Builder(ContextUtil.ctx);
			normalDialog.setTitle("邀请！");
			normalDialog.setMessage(roomname==null?"是否删除该好友":"是否删除该成员");
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							if(roomname==null){
								XmppPresenceListener.del=1;
								XmppConnection.getInstance().removeUser(username);
								MsgDbHelper.getInstance(ctx).delChatMsg(username);
								NewMsgDbHelper.getInstance(ctx).delNewMsg(username);
								sendBroadcast(new Intent("ChatNewMsg"));
								sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
								Intent intent = new Intent(CircleConstants.FRIEND);
								sendBroadcast(intent);
								operBtn.setBackgroundResource(R.drawable.btndel);
								NewChatActivity.isExit=true;
								finish();
							}else{
								try {
									f=1;
									new XmppLoadThread(FriendActivity.this) {

										@Override
										protected Object load() {
											try {
												XmppConnection.mulChat.revokeMembership(XmppConnection.getFullUsername(username));
												XmppConnection.mulChat.kickParticipant(username, "看你不爽就 踢了你");
											} catch (Exception e) {
												e.printStackTrace();
											}

											return  null;
										}

										@Override
										protected void result(Object object) {
											XmppConnection.getInstance().reconnect();

											new Thread(){
												@Override
												public void run() {
													try {
														sleep(1*1000);
														handler.sendEmptyMessage(0);
													} catch (Exception e) {
														e.printStackTrace();
													}
													super.run();
												}
											}.start();
										}
									};

								} catch (Exception e) {

									Log.e("kick=======e", "======="+e.getStackTrace());
									e.printStackTrace();
								}
							}

						}
					});
			normalDialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

			AlertDialog alert = normalDialog.create();
			alert.setCancelable(false);
			alert.show();
			break;

		case R.id.addFriend:
			addFriend.setBackgroundResource(R.drawable.btn2);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			normalDialog = new AlertDialog.Builder(ContextUtil.ctx);
			normalDialog.setTitle("邀请！");
			normalDialog.setMessage("是否添加为好友");
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							try {
								Collection<RosterEntry> entries = XmppConnection.roster.getEntries();

								for(RosterEntry entry : entries) {
									Presence presence = XmppConnection.roster.getPresence(entry.getUser());

									if(entry.getUser().equals(XmppConnection.getFullUsername(username)) && entry.getType() == RosterPacket.ItemType.both) {
										Tool.initToast(getApplicationContext(), "此人已是好友，不能重复添加！");
										return;
									}else if(entry.getUser().equals(XmppConnection.getFullUsername(username)) && entry.getType() != RosterPacket.ItemType.both){
										Tool.initToast(getApplicationContext(), "已向此人发过邀请，请等待对方回复！");
										return;
									}

								}

								Tool.initToast(getApplicationContext(), "好友邀请已发送！");
								addFriend.setBackgroundResource(R.drawable.btn);
								XmppConnection.getInstance().addUser(XmppConnection.getFullUsername(username), MessageConstants.getNameById(username));
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
			normalDialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

			alert = normalDialog.create();
			alert.setCancelable(false);
			alert.show();
			break;

		case R.id.chat:
			Intent i = new Intent(this, NewChatActivity.class);
			i.putExtra("SessionID", username);
			startActivity(i);
			break;

		default:
			break;
		}
	}

	public boolean addToPrivacyList(String name) { // 添加到黑名单
		try {
			PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(XmppConnection.connection);
			if (privacyManager == null) {
				return false;
			}

			PrivacyList[] plists = privacyManager.getPrivacyLists();
			if (plists.length == 0) {// 没有黑名单或是名单中没有列，直接getPrivacyList会出错
				List items = new ArrayList();
				PrivacyItem newitem = new PrivacyItem("jid", false, 100);
				newitem.setValue("BLACKNAME" + "@" + Constants.XMPP_HOST);
				items.add(newitem);

				privacyManager.updatePrivacyList("bl", items);
				privacyManager.setActiveListName("bl");
				return true;
			}

			PrivacyList plist = privacyManager.getPrivacyList("bl");
			if (plist != null) {
				String ser = "@" + Constants.XMPP_HOST;

				List<PrivacyItem> items = plist.getItems();
				for (PrivacyItem item : items) {
					String from = item.getValue().substring(0, item.getValue().indexOf(ser));
					if (from.equalsIgnoreCase(name)) {
						items.remove(item);
						break;
					}
				}

				PrivacyItem newitem = new PrivacyItem("jid", false, 100);
				newitem.setValue(name + "@" + Constants.XMPP_HOST);
				items.add(newitem);
				privacyManager.updatePrivacyList("bl", items);
				privacyManager.setActiveListName("bl");

			}
			return true;
		} catch (XMPPException ex) {
		}
		return false;
	}
	
	private class FriendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}



	@Override
	protected void onResume() {
		super.onResume();

		ContextUtil.isback=0;
		ContextUtil.ctx = this;

		if(roomname!=null){
			XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Tool.initToast(ctx, "成员"+username+"踢出成功");

				f=0;
				kick=0;
				NewChatActivity.isExit=true;
				RoomMemActivity.isExit=true;
				RoomInfoActivity.isExit=true;
				operBtn.setBackgroundResource(R.drawable.btndel);
				finish();
			}else if (msg.what == 1) {
				phoneView.setText(username);

				if(user.getEmail()!=null){
					emailView.setText(user.getEmail());
				}

				if(user.getVarCardObj()!=null){
					sexView.setText(user.getVarCardObj().getvCard().getSex());
					headView.setImageBitmap(ImageUtil.getBitmapFromBase64String(user.getVarCardObj().getvCard().getHeadimg()));
				}
			}
		}
	};
}
