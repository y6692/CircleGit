package com.sylar.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.FriendsAdapter;
import com.sylar.adapter.SearchFriendsAdapter;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.User;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.FriendActivity;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.ucmlmobile.XmppPresenceListener;
import com.sylar.unit.HanziToPinyin;
import com.sylar.unit.HanziToPinyin.Token;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.Sidebar;
import com.sylar.view.swipelistview.SwipeListView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 好友
 * created by Djy
 * 2017/6/14 8:38
 */
public class SearchFriendFragment extends BaseFragment {

    @BindView(R.id.sidebar)
    Sidebar sidebar;
    @BindView(R.id.listview)
    SwipeListView listview;


    public static final String TAG = SearchFriendFragment.class.getSimpleName();
    Unbinder unbinder;
    private SearchFriendsAdapter mAdapter;
    private List<User> friendList = new ArrayList<User>();
    private String key;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_friend, null);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        registerReceiver(new String[]{CircleConstants.FRIEND});

        mAdapter = new SearchFriendsAdapter(getActivity(), new ArrayList<User>());
        listview.setAdapter(mAdapter);
        listview.setFooterDividersEnabled(false);
        sidebar.setListView(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(ContextUtil.ctx);

                normalDialog.setTitle("邀请！");
                normalDialog.setMessage("是否添加为好友");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    if(mAdapter.getItem(position).getUsername().equals(Constants.XMPP_USERNAME)) {
                                        Tool.initToast(getActivity(), "不能添加自己为好友！");
                                        return;
                                    }

                                    Thread.sleep(100);

                                    Roster roster = XmppConnection.connection.getRoster();
                                    Collection<RosterEntry> entries = roster.getEntries();
                                    for(RosterEntry entry : entries) {
                                        Presence presence = roster.getPresence(entry.getUser());

                                        if(entry.getUser().equals(XmppConnection.getFullUsername(friendList.get(position).getUsername())) && entry.getType() == RosterPacket.ItemType.both) {
                                            Tool.initToast(getActivity(), "此人已是好友，不能重复添加！");
                                            return;
                                        }else if(entry.getUser().equals(XmppConnection.getFullUsername(friendList.get(position).getUsername())) && entry.getType() != RosterPacket.ItemType.both){
                                            Tool.initToast(getActivity(), "已向此人发过邀请，请等待对方回复！");
                                            return;
                                        }
                                    }

                                    Tool.initToast(getActivity(), "好友邀请已发送！");
                                    XmppConnection.getInstance().addUser(XmppConnection.getFullUsername(friendList.get(position).getUsername()), friendList.get(position).getName());
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

                AlertDialog alert = normalDialog.create();
                alert.setCancelable(false);
                alert.show();
            }

        });

    }


    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());

        // 圈设置里面删除了圈子
        if (CircleConstants.FRIEND.equals(intent.getAction())) {
            MessageConstants.friendschange=1;
            getFriends();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriends();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void filter(String key){
        this.key = key;
        getFriends();
    }

    private void getContactList() {
        for (int i = 0; i < friendList.size(); i++) {
            User f = friendList.get(i);
            setPinYinAndHearder(f);
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param friend
     */
    protected void setPinYinAndHearder(User friend) {
        String nickName2 = friend.getName();

        if (Character.isDigit(nickName2.charAt(0))) {
            friend.setHeader("#");
        } else {
            ArrayList<Token> tokens = HanziToPinyin.getInstance().get(nickName2);
            StringBuilder sb = new StringBuilder();
            if (tokens != null && tokens.size() > 0) {
                for (Token token : tokens) {
                    if (Token.PINYIN == token.type) {
                        sb.append(token.target);
                    } else {
                        sb.append(token.source);
                    }
                }
            }
            String pingyin = sb.toString().toUpperCase();
            friend.setNicknamepinyin(pingyin);
            String header = pingyin.substring(0, 1);
            friend.setHeader(header);
            char headerChar = header.toLowerCase().charAt(0);
            if (headerChar < 'a' || headerChar > 'z') {
                friend.setHeader("#");
            }
        }
    }

    private void getFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        if (msg.what == 0) {
            try {
                MessageConstants.searchlist = XmppConnection.getInstance().searchMember(key);
                friendList.clear();
                if(StringUtil.isBlank(key)){
                    friendList.addAll(MessageConstants.searchlist);
                }else{
                    for(User u : MessageConstants.searchlist){
                        if(u.getUsername().contains(key)){
                            friendList.add(u);
                        }
                    }
                }

                getContactList();
                Collections.sort(friendList, new Comparator<User>() {
                    @Override
                    public int compare(User lhs, User rhs) {
                        return lhs.getHeader().compareTo(rhs.getHeader());
                    }
                });
                mAdapter.setData(friendList);

            } catch (Exception e) {
                Log.e("fr===eee", e+"===="+e.getMessage());
            }
        }
        }
    };

}