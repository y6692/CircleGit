package com.sylar.ucmlmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.ChatAdapter;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.Urls;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.model.apimodel.APIM_uploadFile;
import com.sylar.unit.CallServer;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.FileUtil;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.Utils;
import com.sylar.view.BaseView;
import com.sylar.view.MyListView;
import com.sylar.view.RecordButton;
import com.sylar.view.RecordButton.OnFinishedRecordListener;
import com.sylar.view.expression.ExpressionView;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sylar.ucmlmobile.ContextUtil.isLeaving;


public class NewChatActivity extends BaseActivity{
	@BaseView(click="onClick")
    ImageView moreBtn,sendBtn;
	@BaseView(click="onClick")
    Button fileBtn;
	@BaseView
    LinearLayout moreLayout;
	@BaseView
    RecordButton recordBtn;
	@BaseView
    ExpressionView expView;
	@BaseView
    EditText msgText;
	@BaseView
	MyListView listView;
	@BindView(R.id.iv_right)
	ImageView ivRight;
	@BindView(R.id.tv_right)
	TextView tvRight;
	@BindView(R.id.ll_right)
	LinearLayout llRight;
	@BindView(R.id.lh_tv_title)
	TextView lhTvTitle;

	public static String chatName;
	public static String roomname;
	private static ChatAdapter adapter;
	public static boolean isExit = false;
	private UpMessageReceiver mUpMessageReceiver;
	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	public static int chatType = ChatItem.CHAT;
	public static NewChatActivity instance;
	Context ctx;
	private String path = "";		// 文件路径
	JSONObject obj;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_chat);
		ButterKnife.bind(this);
		isExit = false;
		ctx=this;
		ContextUtil.ctx=this;
		instance=this;

		CloseActivityClass.activityList.add(this);
		chatName = getIntent().getStringExtra("SessionID");
		roomname = getIntent().getStringExtra("roomname");
		chatType = getIntent().getIntExtra("chatType", ChatItem.CHAT);
		initView();
		initData();

		if(chatType==0){
			VCard vcard= XmppConnection.getInstance().getUservcard(chatName);
			lhTvTitle.setText(vcard.getField("Name"));
		}else{
			lhTvTitle.setText(chatName);
		}

		llRight.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.GONE);
		if(chatType==ChatItem.CHAT){
			ivRight.setBackgroundResource(R.drawable.chaticon);
		}else{
			ivRight.setBackgroundResource(R.drawable.gchaticon);
		}
		fileBtn.setVisibility(View.GONE);
	}



	private void initView() {
		adapter = new ChatAdapter(this, getApplicationContext(), chatName);
		listView.setAdapter(adapter);
		MsgDbHelper.getInstance(getApplicationContext()).updateChatMsg(chatName);
		listView.setonRefreshListener(new MyListView.OnRefreshListener() {
	    
			@Override
			public void onRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						MsgDbHelper.getInstance(getApplicationContext()).updateChatMsg(chatName);
						handler.sendEmptyMessage(0);
					}
				}).start();
			}
		});
		
		msgText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				expView.setVisibility(View.GONE);
			}
		});
		msgText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(msgText.getText().toString())){
					moreBtn.setId(R.id.moreBtn);
					moreBtn.setImageResource(R.drawable.icon_pic);
				}else{
					moreBtn.setId(R.id.sendBtn);
					moreBtn.setImageResource(R.drawable.icon_send_w);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		recordBtn.setOnFinishedRecordListener(new OnFinishedRecordListener() {
			@Override
			public void onFinishedRecord(String audioPath, int time) {
				if (audioPath != null) {
					try {
						obj = new JSONObject();
						obj.put("msg", ImageUtil.getBase64StringFromFile(audioPath));
						obj.put("bodyType", "3");
						XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
					} catch (Exception e) {
						autoSendIfFail(FileUtil.getFileName(audioPath),new String[]{"imgData"}, new Object[]{ImageUtil.getBase64StringFromFile(audioPath)});
						e.printStackTrace();
					}
				} else {
					Tool.initToast(NewChatActivity.this, "发送失败");
				}

			}
		});
		expView.setEditText(msgText);
		mUpMessageReceiver = new UpMessageReceiver();
		registerReceiver(mUpMessageReceiver, new IntentFilter("ChatNewMsg"));
		registerReceiver(mUpMessageReceiver, new IntentFilter("LeaveRoom"));
		XmppConnection.getInstance().setRecevier(chatName,chatType);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ContextUtil.isback=0;
		ContextUtil.ctx = this;

		msgText.clearFocus();
		if (isExit) {
			sendBroadcast(new Intent(CircleConstants.QUAN_GO_HOME));
			sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
			finish();
		}else{
			XmppConnection.getInstance().setRecevier(chatName, chatType);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		ContextUtil.isback=1;
	}
	
	private void initData(){
		chatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsg(chatName);
		adapter.clear();
		adapter.addAll(chatItems);
		listView.setSelection(adapter.getCount() + 1);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				listView.onRefreshComplete();
				List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsgMore(listView.getCount()-1, chatName);

				for (int i = 0; i < moreChatItems.size(); i++) {
					chatItems.add(i, moreChatItems.get(i));
				}

				adapter.clear();
				adapter.addAll(chatItems);
				adapter.notifyDataSetChanged();
			}else if(msg.what == 1) {
				Tool.initToast_Short(ContextUtil.getInstance(), "发送失败，请检查网络!");
			}else if(msg.what == 2) {
				Tool.initToast_Short(ContextUtil.getInstance(), "发送中..");
			}else if(msg.what == 3) {
				Tool.initToast_Short(ContextUtil.getInstance(), "发送成功");
			}

		}
	};

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(chatType==ChatItem.CHAT){
            RoomMemActivity.isExit=true;
            RoomInfoActivity.isExit=true;
            NewChatActivity.isExit=true;
        }

        finish();
    }

	@OnClick({R.id.ll_back, R.id.ll_right, R.id.iv_right})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ll_back:
                if(chatType==ChatItem.CHAT){
                    RoomMemActivity.isExit=true;
                    RoomInfoActivity.isExit=true;
                    NewChatActivity.isExit=true;
                }

				finish();
				break;

			case R.id.ll_right:
				break;

			case R.id.iv_right:
				Intent intent = new Intent();
				if (chatType == ChatItem.CHAT) {
					intent.setClass(ctx, FriendActivity.class);
					intent.putExtra("SessionID", chatName);
					intent.putExtra("roomname", roomname);
					startActivity(intent);
				}
				else if(chatType == ChatItem.GROUP_CHAT){
					intent.setClass(ctx, RoomInfoActivity.class);
					intent.putExtra("roomname", chatName);
					startActivity(intent);
				}
				break;
		}
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;

		case R.id.sendBtn:
			String msg = msgText.getText().toString(); // 获取text文本
			if(!msg.isEmpty()){     //文本不为空，直接发文本消息
				try {
					obj = new JSONObject();
					obj.put("msg", msg);
					obj.put("bodyType", "0");
					XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
				} catch (Exception e) {
					autoSendIfFail(msg);
					e.printStackTrace();
				}
				msgText.setText("");
			}else if(recordBtn.getVisibility() == View.GONE){   //文本为空，从文本输入模式切换到语音输入模式
				msgText.setVisibility(View.GONE);
				recordBtn.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
				sendBtn.setImageResource(R.drawable.icon_keyboard);
			}else if(msgText.getVisibility() == View.GONE){ //文本为空，从语音输入模式切换到文本输入模式
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);
				sendBtn.setImageResource(R.drawable.icon_voice);
			}
			break;

		case R.id.msgText:    //选中文本框，聊天记录弹到最后一条
			expView.setVisibility(View.GONE);
			listView.setSelection(adapter.getCount()); // 去到最后一行
			break;

		case R.id.moreBtn:    //弹出更多发送内容,选图，照相，发送位置
			if (moreLayout.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				moreLayout.setVisibility(View.VISIBLE);
				expView.setVisibility(View.GONE);
			} else {
				moreLayout.setVisibility(View.GONE);
			}
			break;

		case R.id.takePicBtn:    //拍照

			/*
			ContextCompat.checkSelfPermission(ContextUtil.getInstance(), Manifest.permission.CAMERA);

			if (Build.VERSION.SDK_INT >= 23) {

				Log.e("permission=====", "=======" + NewChatActivity.instance);

				int permission = ContextCompat.checkSelfPermission(ContextUtil.getInstance(), Manifest.permission.CAMERA);

				if (permission != PackageManager.PERMISSION_GRANTED) {
					//list.add(Manifest.permission.CAMERA);

					NewChatActivity.instance.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);

					return;
				}


				Log.e("permission=====222", "=======" + permission);

			}

			Intent intent1 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent1.setClass(this, PicSrcPickerActivity.class);
			intent1.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
			startActivityForResult(intent1,PicSrcPickerActivity.CROP);
			*/

			Intent intent = new Intent(this, RecorderActivity.class);
			startActivityForResult(intent,200);

//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			intent.putExtra("camerasensortype", 2); // 调用前置摄像头
//			intent.putExtra("autofocus", true); // 自动对焦
//			intent.putExtra("fullScreen", false); // 全屏
//			intent.putExtra("showActionIcons", false);
//			startActivityForResult(intent, 1);

			break;

		case R.id.chosePicBtn:  //图库
			Intent intent2 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent2.setClass(this, PicSrcPickerActivity.class);
			intent2.putExtra("type", PicSrcPickerActivity.CHOSE_PIC);
			startActivityForResult(intent2,PicSrcPickerActivity.CROP);
			break;
			
		case R.id.adrBtn:   //发送位置
			Intent intent3 = new Intent(this, MyMapActivity.class);
			startActivityForResult(intent3,300);
			break;
			
		case R.id.expBtn:  //点击表情icon
			if (expView.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				expView.setVisibility(View.VISIBLE);
				moreLayout.setVisibility(View.GONE);
			} else {
				expView.setVisibility(View.GONE);
			}
			//Determine the voice
			if(msgText.getVisibility() == View.GONE){
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);

				if("".equals(msgText.getText().toString())){
					moreBtn.setId(R.id.moreBtn);
					moreBtn.setImageResource(R.drawable.icon_pic);
				}else{
					moreBtn.setId(R.id.sendBtn);
					moreBtn.setImageResource(R.drawable.icon_send_w);
				}
			}
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String imgName = "";
		String base64String = "";
		if (RESULT_OK == resultCode) {
			switch (requestCode) { //选图回调
			case PicSrcPickerActivity.CROP:
				imgName = data.getStringExtra("imgName");
				base64String = data.getStringExtra("base64String");

				if (imgName != null) {
					try {
						obj = new JSONObject();
						obj.put("msg", base64String);
						obj.put("bodyType", "2");
						XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
					} catch (Exception e) {
						autoSendIfFail(imgName,new String[]{"imgData"}, new Object[]{base64String});
						e.printStackTrace();
					}
				}
				break;

			case 200:
				if(resultCode == RESULT_OK) {
					// 成功
					int timecount = data.getIntExtra("timecount", 0);
					imgName = data.getStringExtra("name");
					path = data.getStringExtra("path");

					Toast.makeText(this,"存储路径为:"+path,Toast.LENGTH_SHORT).show();
					// 通过路径获取第一帧的缩略图并显示

					if (imgName != null) {
						try {
							File newFile = new File(path);
							StringRequest request = new StringRequest(Urls.BASE_URL + Urls.UPLOAD_VIDEO, RequestMethod.POST);
							request.add("filename", new FileBinary(newFile));

							SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
								@Override public void onStart(int what) {
									super.onStart(what);
								}

								@Override public void onSucceed(int what, Response<String> response) {
									super.onSucceed(what, response);
									APIM_uploadFile result = JsonUtil.jsonToObject(response.get(), APIM_uploadFile.class);
									if (result.getStatus() == 100) {
										//重新设置用户信息
										ToastUtil.showMessage(result.getMessage()+"==="+result.getResults());

										try {
											obj = new JSONObject();
											obj.put("msg", result.getResults());
											obj.put("bodyType", "5");
											XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										ToastUtil.showMessage(result.getMessage());
									}
								}

								@Override public void onFailed(int what, Response<String> response) {
									super.onFailed(what, response);
									ToastUtil.showMessage("上传失败");
								}

								@Override public void onFinish(int what) {
									super.onFinish(what);
								}
							};
							CallServer.getInstance().request(0, request, listener);
						} catch (Exception e) {
							autoSendIfFail(imgName, new String[]{"imgData"}, new Object[]{base64String});
							e.printStackTrace();
						}
					}
				}
				break;

			case 300:
				imgName = data.getStringExtra("imgName");
				path = data.getStringExtra("imgPath");
				final String lat = data.getStringExtra("lat");
				final String lon = data.getStringExtra("lon");
				final String name = data.getStringExtra("name");
				final String address = data.getStringExtra("address");

				if (imgName != null) {
					try {
								File newFile = new File(path);
								StringRequest request = new StringRequest(Urls.BASE_URL + Urls.UPLOAD_IMAGE, RequestMethod.POST);
								request.add("filename", new FileBinary(newFile));

								SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
									@Override public void onStart(int what) {
										super.onStart(what);
									}

									@Override public void onSucceed(int what, Response<String> response) {
										super.onSucceed(what, response);
										APIM_uploadFile result = JsonUtil.jsonToObject(response.get(), APIM_uploadFile.class);

										if (result.getStatus() == 100) {
											ToastUtil.showMessage(result.getMessage()+"==="+result.getResults());

											try {
												obj = new JSONObject();
												obj.put("lat", lat);
												obj.put("lon", lon);
												obj.put("name", name);
												obj.put("address", address);
												obj.put("imageUrl", result.getResults());
												obj.put("bodyType", "6");
												XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
											} catch (Exception e) {
												e.printStackTrace();
											}
										} else {
											ToastUtil.showMessage(result.getMessage());
										}
									}

									@Override public void onFailed(int what, Response<String> response) {
										super.onFailed(what, response);
										ToastUtil.showMessage("上传失败");
									}

									@Override public void onFinish(int what) {
										super.onFinish(what);
									}
								};
								CallServer.getInstance().request(0, request, listener);
					} catch (Exception e) {
						autoSendIfFail(imgName, new String[]{"imgData"}, new Object[]{base64String});
						e.printStackTrace();
					}
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		try {
			adapter.mping.stop();
			if (ContextUtil.getInstance() != null) {
				unregisterReceiver(mUpMessageReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearMsgCount();
		finish();
		super.onDestroy();
	}

	private void clearMsgCount() {
		NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(chatName);
		ContextUtil.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
	}
	
	private class UpMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到廣播更新我们的界面
			if (intent.getAction().equals("LeaveRoom")) {
				finish();
			}else{
				initData();
				MsgDbHelper.getInstance(getApplicationContext()).updateChatMsg(chatName);
			}
		}
	}

	public void autoSendIfFail(final String msg){
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {   //1秒后开始
			int count = 0;
			@Override
			public void run() {
				try {

					if (XmppConnection.getInstance().getConnection().isConnected()) {
						XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
						handler.sendEmptyMessage(3);
						timer.cancel();
					}else{
						if (count > 8) {
							if(count%4==0){
								handler.sendEmptyMessage(1);
							}
						}else{
							if(count%4==0){
								handler.sendEmptyMessage(2);
							}
						}
					}
					count++;
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					Log.e("muc", "autosend      "+count);
				}
			}
		}, 1000,1000);
	}
	
	public void autoSendIfFail(final String msg, final String[] s, final Object[] obj){
		Tool.initToast(ContextUtil.getInstance(), "发送中..");
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {  //发送失败
			int count = 0;
			@Override
			public void run() {
				try {
					count++;
					if (!isLeaving) {
						XmppConnection.getInstance().setRecevier(chatName, chatType);
						XmppConnection.getInstance().sendMsg(obj.toString(), chatType);
						timer.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (count > 8) {
						Tool.initToast(ContextUtil.getInstance(), "发送失败");
						timer.cancel();
					}
				}
			}
		}, 1000,1000);
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
}
