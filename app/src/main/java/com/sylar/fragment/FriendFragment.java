package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.FriendsAdapter;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.constant.H5NativeApis;
import com.sylar.dao.GetHttpUserApplaction;
import com.sylar.model.User;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.FriendActivity;
import com.sylar.ucmlmobile.GroupUser;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.SearchFriendActivity;
import com.sylar.ucmlmobile.XmppConnection;
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
import wendu.dsbridge.OnReturnValue;

/**
 * 好友
 * created by Djy
 * 2017/6/14 8:38
 */
public class FriendFragment extends BaseFragment {

    @BindView(R.id.sidebar)
    Sidebar sidebar;
    @BindView(R.id.listview)
    SwipeListView listview;

    public static final String TAG = FriendFragment.class.getSimpleName();
    Unbinder unbinder;
    public static FriendsAdapter mAdapter;
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
        ContextUtil.ctx = getActivity();
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
        registerReceiver(new String[]{CircleConstants.FRIEND, "clear"});

        mAdapter = new FriendsAdapter(getActivity(), new ArrayList<User>());
        mAdapter.setOnChatClickListner(new FriendsAdapter.OnChatClickListner() {
            @Override
            public void onChatClick(User friend) {
                Intent i = new Intent(getActivity(), NewChatActivity.class);
                i.putExtra("SessionID", friend.getUsername());
                startActivity(i);
            }
        });
        listview.setAdapter(mAdapter);
        listview.setFooterDividersEnabled(false);
        sidebar.setListView(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent i = new Intent(getActivity(), FriendActivity.class);
                Intent i = new Intent(getActivity(), NewChatActivity.class);
                i.putExtra("SessionID", mAdapter.getItem(position).getUsername());
                startActivity(i);
            }

        });

    }


    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        // 圈设置里面删除了圈子
        if (CircleConstants.FRIEND.equals(intent.getAction())) {
            getFriends();
        }else if("clear".equals(intent.getAction())){
            mAdapter.setData(null);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {
                User user;
                Bitmap bmp;
                VCard vcard;
                try {

                    if(MessageConstants.friendschange>0){

                        MessageConstants.friendslist.clear();
                        MessageConstants.friendslist_all.clear();

                        //if(!MessageConstants.hasMe(Constants.XMPP_USERNAME)){

                            vcard=XmppConnection.getInstance().getUservcard(Constants.XMPP_USERNAME);
                            user=new User();
                            user.setUsername(Constants.XMPP_USERNAME);
                            user.setName(Constants.XMPP_NICKNAME);
                            user.setMobile(Constants.XMPP_USERNAME);

                            bmp= ImageUtil.getBitmapFromBase64String(vcard.getField("headimg"));
                            if(bmp!=null){
                                user.setBitmap(bmp);
                            }

                            if(!MessageConstants.hasMe(Constants.XMPP_USERNAME)){
                                MessageConstants.friendslist_all.add(user);
                            }

                        //}

                        Collection<RosterEntry> entries = XmppConnection.roster.getEntries();
                        for (RosterEntry entry : entries) {

                            if( MessageConstants.addfriendslist.indexOf(entry.getUser().split("@")[0])==-1 && (entry.getType() == RosterPacket.ItemType.none || entry.getType() == RosterPacket.ItemType.to || entry.getType() == RosterPacket.ItemType.from)  ){
                                continue;
                            }

                            if (XmppConnection.connection == null || !XmppConnection.connection.isConnected()){
                                return;
                            }

                            vcard=XmppConnection.getInstance().getUservcard(entry.getUser().split("@")[0]);
                            user=new User();
                            user.setUsername(entry.getUser().split("@")[0]);
                            if(vcard.getField("Name")!=null && !vcard.getField("Name").equals("")){
                                user.setName(vcard.getField("Name"));
                            }else{
                                user.setName(entry.getUser().split("@")[0]);
                            }

                            user.setMobile(entry.getUser().split("@")[0]);
                            bmp=ImageUtil.getBitmapFromBase64String(vcard.getField("headimg"));
                            if(bmp!=null){
                                user.setBitmap(bmp);
                            }
                            if(!MessageConstants.isFriend(entry.getUser().split("@")[0])){
                                MessageConstants.friendslist.add(user);
                                MessageConstants.friendslist_all.add(user);
                            }
                        }
                        MessageConstants.friendschange=0;
                    }

                    friendList.clear();
                    if(StringUtil.isBlank(key))
                        friendList.addAll(MessageConstants.friendslist);
                    else{
                        for(User u : MessageConstants.friendslist){
                            if(u.getName().contains(key))
                                friendList.add(u);
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
            }else if (msg.what == 1) {
                friendList.clear();
                if(StringUtil.isBlank(key))
                    friendList.addAll(MessageConstants.friendslist);
                else{
                    for(User user : MessageConstants.friendslist){
                        if(user.getName().contains(key))
                            friendList.add(user);
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
            }
        }
    };

}