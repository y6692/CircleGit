package com.sylar.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.circlegit.R;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.sylar.constant.CircleConstants;
import com.sylar.fragment.DynamicFragment;
import com.sylar.fragment.FriendsFragment;
import com.sylar.fragment.MineFragment;
import com.sylar.fragment.MsgFragment;
import com.sylar.fragment.QuanFragment;
import com.sylar.model.ChatItem;
import com.sylar.model.Room;
import com.sylar.model.TabEntity;
import com.sylar.ucmlmobile.BaseActivity;
import com.sylar.ucmlmobile.ConfigInfo;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.yanzhenjie.permission.Permission;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页
 * created by Djy
 * 2017/6/13 4:23
 */
public class HomeActivity extends BaseActivity{
    public static final String INTENT_MSG_COUNT = "INTENT_MSG_COUNT";

    @BindView(R.id.fl_change)
    FrameLayout flChange;
    @BindView(R.id.ll_tab)
    LinearLayout llTab;
    @BindView(R.id.tab)
    CommonTabLayout tab;
    public static Context ctx;

    ChatManager chatmanager;
    public static MultiUserChat muc;
    public static int f;
    public static Activity act;
    String jid;
    String roomname;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"消息", "动态", "圈子", "好友", "我的"};
    private int[] mIconUnselectIds = {
            R.mipmap.tab_msg_normal, R.mipmap.tab_dongtai_normal,
            R.mipmap.tab_circle_normal, R.mipmap.tab_friend_normal, R.mipmap.tab_mine_normal};
    private int[] mIconSelectIds = {
            R.mipmap.tab_msg_selected, R.mipmap.tab_dongtail_selected,
            R.mipmap.tab_circle_selected, R.mipmap.tab_friend_selected, R.mipmap.tab_mine_selected};
    Bundle sis;
    private long firstTime = 0; //记录第一次点击的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState != null){
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sis=savedInstanceState;
        ctx = this;
        ContextUtil.ctx = this;
        act=this;
        ButterKnife.bind(this);
        tab = (CommonTabLayout) findViewById(R.id.tab);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        NewMsgReceiver newMsgReceiver = new NewMsgReceiver();
        registerReceiver(newMsgReceiver, new IntentFilter("AddUser"));
        registerReceiver(newMsgReceiver, new IntentFilter("quit"));
        registerReceiver(newMsgReceiver, new IntentFilter("invite"));
        registerReceiver(newMsgReceiver, new IntentFilter("join"));
        mFragments.add(MsgFragment.getInstance(mTitles[0]));
        mFragments.add(DynamicFragment.getInstance(mTitles[1]));
        mFragments.add(QuanFragment.getInstance(mTitles[2]));
        mFragments.add(FriendsFragment.getInstance(mTitles[3]));
        mFragments.add(MineFragment.getInstance(mTitles[4]));

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tab.setTabData(mTabEntities, HomeActivity.this, R.id.fl_change, mFragments);
        tab.setCurrentTab(0);

        loginAccount(CircleHelper.userManager().getUserinfo().getUsername(), CircleHelper.userManager().getUserinfo().getPlainPassword());
        registerReceiver(new String[]{CircleConstants.HIDE_TAB_BAR_FROM_JS, CircleConstants.SHOW_TAB_BAR_FROM_JS, CircleConstants.SHOW_MSG_NUM, CircleConstants.EXIT});
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMainActivityDestroy",true);
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

    public void loginAccount(final String userName, final String password) {
        f=1;
        new XmppLoadThread(this) {

            @Override
            protected Object load() {
                String url = ConfigInfo.getReqHost(ctx);
                Constants.XMPP_HOST = url.split("//")[1].split("/")[0].split(":")[0];
                Constants.XMPP_HOSTNAME = Constants.XMPP_HOST;

                boolean isSuccess = XmppConnection.getInstance().login(userName, password);
                if (isSuccess) {
                    Constants.XMPP_USERNAME = userName;
                    Constants.XMPP_PASSWORD = password;
                    Constants.XMPP_NICKNAME = MessageConstants.getNameById(userName);
                }
                return isSuccess;
            }

            @Override
            protected void result(Object o) {
                boolean isSuccess = (Boolean) o;
                if (isSuccess) {
                    f=0;
                    handler.sendEmptyMessage(0);
                } else {
                }
            }
        };
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (CircleConstants.HIDE_TAB_BAR_FROM_JS.equals(intent.getAction())) {
            llTab.setVisibility(View.GONE);
        }else if(CircleConstants.SHOW_TAB_BAR_FROM_JS.equals(intent.getAction())) {
            llTab.setVisibility(View.VISIBLE);
        }else if(CircleConstants.SHOW_MSG_NUM.equals(intent.getAction())){
            int count = intent.getIntExtra(INTENT_MSG_COUNT, 0);
            if (count > 0){
                tab.showMsg(0, count);
                tab.setMsgMargin(0, -8, 5);
            } else {
                tab.hideMsg(0);
            }
        }else if(CircleConstants.EXIT.equals(intent.getAction())) {
          finish();
        }
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 800) {//如果两次按键时间间隔大于800毫秒，则不退出
            Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;//更新firstTime
        } else {
            System.exit(0);//否则退出程序
        }
    }

    private class NewMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            jid = intent.getStringExtra("jid");
            roomname=intent.getStringExtra("roomname");

            if (intent.getAction().equals("ChatNewMsg")) {
                Log.e("ChatNewMsg=====", " - ");
            } else if (intent.getAction().equals("AddUser")) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(ContextUtil.ctx);
                final String id=jid;

                normalDialog.setTitle("邀请！");
                normalDialog.setMessage(id.split("@")[0]+"想加你为好友，同意吗?");
                normalDialog.setPositiveButton("同意",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = XmppConnection.getInstance().searchMember(id.split("@")[0]).get(0).getName();

                                try {
                                    XmppConnection.getInstance().addUser(id, name);
                                    MessageConstants.friendschange=2;
                                    MessageConstants.addfriendslist.add(id.split("@")[0]);
                                    Intent intent = new Intent(CircleConstants.FRIEND);
                                    sendBroadcast(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                normalDialog.setNegativeButton("拒绝",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                XmppConnection.getInstance().removeUser(id);
                            }
                        });

                AlertDialog alert = normalDialog.create();
                alert.setCancelable(false);
                alert.show();
            }else if (intent.getAction().equals("invite")) {

                new XmppLoadThread(ContextUtil.ctx) {
                    @Override
                    protected Object load() {
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
                                    handler.sendEmptyMessage(2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                super.run();
                            }
                        }.start();
                    }
                };
            }else if (intent.getAction().equals("quit")) {
                new XmppLoadThread(ContextUtil.ctx) {
                    @Override
                    protected Object load() {
                        try {
                            XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
                            XmppConnection.mulChat.kickParticipant(jid.split("@")[0], "看你不爽就 踢了你");
                            XmppConnection.mulChat.revokeMembership(jid);
                        } catch (Exception e) {
                            Log.e("quit=======eee", e+"====="+e.getMessage());
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
                                    handler.sendEmptyMessage(4);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                super.run();
                            }
                        }.start();
                    }
                };

            }else if (intent.getAction().equals("join")) {
				new XmppLoadThread(ContextUtil.ctx) {
					@Override
					protected Object load() {
						try {
							XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
							XmppConnection.mulChat.grantMembership(jid);
							XmppConnection.mulChat.invite(jid, roomname);
						} catch (Exception e) {
							Log.e("eeeee========", e+"===="+e.getMessage());
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
									handler.sendEmptyMessage(3);
								} catch (Exception e) {
									e.printStackTrace();
								}
								super.run();
							}
						}.start();
					}
				};
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try{
                    for (Room room : XmppConnection.getInstance().getMyRoom()) {
                        MultiUserChat muc = new MultiUserChat(XmppConnection.getInstance().getConnection(), XmppConnection.getFullRoomname(room.name));
                        DiscussionHistory history = new DiscussionHistory();
                        history.setMaxChars(0);
                        history.setSince(new Date());
                        muc.join(Constants.XMPP_USERNAME, null, history, SmackConfiguration.getPacketReplyTimeout());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                XmppConnection.getUserinfo();
            }else if (msg.what == 1) {
            }else if (msg.what == 2) {
                XmppConnection.getInstance().setRecevier(XmppConnection.getRoomName(jid), ChatItem.GROUP_CHAT);
                XmppLoadThread.mdialog.dismiss();
                Tool.initToast(ContextUtil.ctx, "你被邀请加入圈子"+XmppConnection.getRoomName(jid));
                sendBroadcast(new Intent(CircleConstants.REFRESH_QUAN));
            } else if (msg.what == 3) {
                XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
                Tool.initToast(ContextUtil.ctx, jid.split("@")[0]+"加入圈子"+roomname);
            }else if (msg.what == 4) {
                XmppConnection.getInstance().setRecevier(roomname, ChatItem.GROUP_CHAT);
                Tool.initToast(ContextUtil.ctx, "成员"+jid.split("@")[0]+"退出圈子"+roomname);
            }

        }
    };



}
