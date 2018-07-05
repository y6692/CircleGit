package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.model.ChatItem;
import com.sylar.model.Room;
import com.sylar.ucmlmobile.AddRoomActivity;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.SearchRoomActivity;
import com.sylar.ucmlmobile.XmppConnection;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 圈子
 * created by Djy
 * 2017/6/14 8:38
 */
public class CircleFragment extends Fragment {
    public static final String TAG = CircleFragment.class.getSimpleName();
    Unbinder unbinder;
    private String mTitle;

    private ImageView searchRoomView;
    private ImageView addRoomView;
    private ListView listView;
    MyContactAdapter myContactAdapter;

    public static int flag = 0;

    public static CircleFragment getInstance(String title) {
        CircleFragment sf = new CircleFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_circle, null);
        unbinder = ButterKnife.bind(this, v);
        ContextUtil.ctx = getActivity();

        addRoomView = (ImageView)v.findViewById(R.id.addBtn);
        searchRoomView = (ImageView)v.findViewById(R.id.rightBtn);
        listView = (ListView)v.findViewById(R.id.listview);

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

        myContactAdapter=new MyContactAdapter();
        listView.setAdapter(myContactAdapter);
        myContactAdapter.notifyDataSetChanged();

        addRoomView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddRoomActivity.class);
                startActivity(i);

            }
        });

        searchRoomView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchRoomActivity.class);
                startActivity(i);

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent i = new Intent(getActivity(), NewChatActivity.class);
                i.putExtra("SessionID", myContactAdapter.getItem(position).name);
                i.putExtra("chatType", ChatItem.GROUP_CHAT);
                startActivity(i);
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(flag==0){
                        handler.sendEmptyMessage(0);
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        return v;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                myContactAdapter.notifyDataSetChanged();
            }else if (msg.what == 1) {
            }
        }
    };


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
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_contact_member, null);

            TextView contactName = (TextView) convertView.findViewById(R.id.message_contact_name);
            contactName.setText(XmppConnection.myRooms.get(position).name);
            return convertView;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
    }

    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net(){

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // show自己页面时，才去网络操作
        if (!hidden) {
            net();
            Log.i("circle", "show" + mTitle);
        }else{
            Log.i("circle", "hidden" + mTitle);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        for (Room room : XmppConnection.getInstance().getMyRoom()) {
            Log.e(">>>>Room>>>>", XmppConnection.myRooms.size() +"=====" + room.name+"=====" +room.friendList);
        }

        myContactAdapter.notifyDataSetChanged();

    }

}