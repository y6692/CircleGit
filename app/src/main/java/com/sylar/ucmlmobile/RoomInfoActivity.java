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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.Urls;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.model.Room;
import com.sylar.model.apimodel.APIM_getMucManager;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircularImage;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.BaseView;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.Affiliate;

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
public class RoomInfoActivity extends BaseActivity {
	@BindView(R.id.ll_back)
	LinearLayout llBack;
	@BaseView(click="onClick")
    TextView rightBtn;
	@BaseView(click="onClick")
	Button exitBtn;
	@BaseView(click="onClick")
	Button delRoom;
	@BindView(R.id.qcy)
	RelativeLayout rlqcy;
	@BindView(R.id.img2)
	CircularImage img2;
	@BindView(R.id.txtAdd)
	TextView txtAdd;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;

	public static String roomname;
	private List<String> members = new ArrayList<String>();
	public static boolean isExit = false;
	public static int f=0;
	Context ctx;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_room_info);
		ButterKnife.bind(this);
		ctx=this;
		ContextUtil.ctx=this;

		CloseActivityClass.activityList.add(this);
		roomname = getIntent().getStringExtra("roomname");
		lhTvTitle.setText("圈子资料");

		try {
			XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
			Collection<Affiliate> entries = XmppConnection.mulChat.getOwners();

			for(Affiliate entry : entries) {
				if(!Constants.XMPP_USERNAME.equals(entry.getJid().split("@")[0])){
					delRoom.setVisibility(View.GONE);
				}else{
					exitBtn.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			Log.e("fr=====eee", e+" - " + e.getMessage());
		}

		CircularImage contacthead = (CircularImage) findViewById(R.id.img);
		Bitmap bmp= MessageConstants.findAllHeadById(Constants.XMPP_USERNAME);
		if(bmp!=null){
			contacthead.setImageBitmap(bmp);
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

		for (Room room : XmppConnection.getInstance().getMyRoom()) {
			if (room.name.equals(roomname)) {
				members = room.friendList;

				if(members.size()<2){
					img2.setVisibility(View.GONE);
				}else{
					img2.setVisibility(View.VISIBLE);

					for (String mem : room.friendList) {
						if(mem.equals(Constants.XMPP_USERNAME)){
							continue;
						}

						Bitmap bmp= MessageConstants.findAllHeadById(mem);
						img2.setImageBitmap(null);
						if(bmp!=null){
							img2.setImageBitmap(bmp);
						}else{
							img2.setBackgroundResource(R.drawable.memicon);
						}

						break;
					}
				}

				txtAdd.setText(room.friendList.size()+"名成员");
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}


	private class MyContactAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return XmppConnection.myRooms.size();
		}

		@Override
		public Room getItem(int position) {
			return XmppConnection.myRooms.get(position);
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
			this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.message_contact_member, null);
			TextView contactName = (TextView) convertView.findViewById(R.id.message_contact_name);
			contactName.setText(XmppConnection.myRooms.get(position).name);
			return convertView;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NewChatActivity.isExit=false;
		NewChatActivity.chatName=roomname;
		NewChatActivity.roomname=null;
		NewChatActivity.chatType=ChatItem.GROUP_CHAT;
		finish();
	}



	@OnClick({R.id.ll_back, R.id.qcy})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
				NewChatActivity.isExit=false;
				NewChatActivity.chatName=roomname;
				NewChatActivity.roomname=null;
				NewChatActivity.chatType=ChatItem.GROUP_CHAT;
				finish();
				break;

			case R.id.qcy:
				Intent intent = new Intent();
				intent.setClass(ctx, RoomMemActivity.class);
				intent.putExtra("roomname", roomname);
				startActivity(intent);

				break;

		}
	}



	public void onClick(View v){
		switch (v.getId()) {
		case R.id.rightBtn:
			Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
			intent.putExtra("roomname", roomname);
			intent.putStringArrayListExtra("members", (ArrayList<String>) members);
			startActivity(intent);
			break;

		case R.id.exitBtn:
			Builder builder = new Builder(RoomInfoActivity.this);
			builder
			.setMessage("确定要退出吗?")
			.setTitle("提示")
			.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					StringRequest request = new StringRequest(Urls.BASE_URL + Urls.GET_DelMucMember, RequestMethod.GET);
					request.add("roomnum", roomname);
					request.add("username", Constants.XMPP_USERNAME);

					SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
						@Override
						public void onStart(int what) {
							super.onStart(what);
						}

						@Override
						public void onSucceed(int what, Response<String> response) {
							super.onSucceed(what, response);
							// 请求成功。
							APIM_getMucManager result = JsonUtil.jsonToObject(response.get(), APIM_getMucManager.class);
							//100 ：成功 101：失败 102:输入错误
							if(result.getStatus() == 100){
								XmppConnection.mulChat.leave();
								XmppConnection.getInstance().reconnect();
								NewChatActivity.isExit=true;

								Tool.initToast(ctx, "你已经退出圈子"+roomname);

								if(NewChatActivity.chatName.equals(roomname)){
									CloseActivityClass.exitClient(ctx);
								}

								sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
								sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
								MsgDbHelper.getInstance(ctx).delChatMsg(roomname);
								NewMsgDbHelper.getInstance(ctx).delNewMsg(roomname);
								sendBroadcast(new Intent("ChatNewMsg"));
								finish();
							}else{
								ToastUtil.showMessage(result.getMessage());
							}

						}

						@Override
						public void onFailed(int what, Response<String> response) {
							super.onFailed(what, response);
							ToastUtil.showMessage("网络请求失败");
						}

						@Override
						public void onFinish(int what) {
							super.onFinish(what);
							//dismissPd();
						}
					};
					CallServer.getInstance().request(0, request, listener);

				}
			})
			.setNegativeButton("取消", null)
			.show();
			break;

		case R.id.delRoom:
			builder = new Builder(RoomInfoActivity.this);
			builder.setMessage("确定要删除吗?")
					.setTitle("提示")
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								XmppConnection.mulChat.destroy("del", null);
							} catch (XMPPException e) {
								e.printStackTrace();
							}

							XmppConnection.getInstance().reconnect();
							f=0;
							XmppLoadThread.mdialog.dismiss();
							sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
							sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
							finish();
							NewChatActivity.isExit=true;
							Tool.initToast(getApplicationContext(), "圈子"+""+"删除成功");
							MsgDbHelper.getInstance(ctx).delChatMsg(roomname);
							NewMsgDbHelper.getInstance(ctx).delNewMsg(roomname);
						}
			})
			.setNegativeButton("取消", null);
			AlertDialog alert = builder.create();
			alert.setCancelable(false);
			alert.show();
			break;

		default:
			break;
		}
		
	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				f=0;
				XmppLoadThread.mdialog.dismiss();
				finish();
				NewChatActivity.isExit=true;

				Tool.initToast(getApplicationContext(), "圈子"+""+"删除成功");

				MsgDbHelper.getInstance(ctx).delChatMsg(roomname);
				NewMsgDbHelper.getInstance(ctx).delNewMsg(roomname);

			}else if (msg.what == 1) {
			}
		}
	};

}
