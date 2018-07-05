package com.sylar.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.activity.HomeActivity;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.dao.MsgDbHelper;
import com.sylar.dao.NewMsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.StringUtil;
import com.sylar.view.expression.ExpressionUtil;

import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import q.rorbin.badgeview.QBadgeView;

/**
 * 消息
 * created by Djy
 * 2017/6/14 8:38
 */
public class MsgFragment extends BaseFragment {

    public static final String TAG = MsgFragment.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;
    private String mTitle;

    private ListView listView;
    private MyRecentlyAdapter myRecentlyAdapter;
    public static int flag = 0;
    private ImageView head;
    private String namebyid;

    public static MsgFragment getInstance(String title) {
        MsgFragment sf = new MsgFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_msg, null);
        unbinder = ButterKnife.bind(this, v);
        ContextUtil.ctx = getActivity();
        registerReceiver(new String[]{"ChatNewMsg"});

        flag = 0;

        listView = (ListView) v.findViewById(R.id.listview);
        myRecentlyAdapter = new MyRecentlyAdapter();
        listView.setAdapter(myRecentlyAdapter);
        myRecentlyAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView thisListView = (ListView) parent;
                String sessionid = (String) thisListView.getItemAtPosition(position);

                String namebyid = MessageConstants.recently.get(position);
                List<ChatItem> items = MessageConstants.recentlyMap.get(namebyid);

                Intent i = new Intent(getActivity(), NewChatActivity.class);
                i.putExtra("SessionID", sessionid);
                i.putExtra("chatType", items.get(items.size() - 1).chatType);
                startActivity(i);
            }
        });

        getActivity().sendBroadcast(new Intent("ChatNewMsg"));
        return v;
    }


    public void updateCount() {
        // 更新界面
        int count = NewMsgDbHelper.getInstance(getActivity()).getMsgCount();
        Intent intent = new Intent(CircleConstants.SHOW_MSG_NUM);
        intent.putExtra(HomeActivity.INTENT_MSG_COUNT, count);
        getActivity().sendBroadcast(intent);
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
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_recently, null);

            head = (ImageView) convertView.findViewById(R.id.recentlyhead);
            LinearLayout ll_head = (LinearLayout) convertView.findViewById(R.id.ll_head);
            TextView rosterName = (TextView) convertView.findViewById(R.id.message_recently_name);
            TextView recentlymsg = (TextView) convertView.findViewById(R.id.message_recently_last);
            TextView rostertime = (TextView) convertView.findViewById(R.id.message_recently_time);
            TextView unreadcnt = (TextView) convertView.findViewById(R.id.unreadcnt);
            namebyid = MessageConstants.recently.get(position);
            List<ChatItem> items = MessageConstants.recentlyMap.get(namebyid);
            String msg = items.get(items.size() - 1).msg;
            String bodyType = items.get(items.size() - 1).bodyType;
            String time = items.get(items.size() - 1).sendDate.substring(0, 12);
            QBadgeView qBadgeView = new QBadgeView(getActivity());
            Bitmap bmp=MessageConstants.findAllHeadById(namebyid);
            if(bmp!=null){
                head.setImageBitmap(bmp);
            }
            if(items.get(items.size() - 1).chatType==0){
                VCard vcard= XmppConnection.getInstance().getUservcard(namebyid);
                rosterName.setText(vcard.getField("Name"));
            }else{
                rosterName.setText(namebyid);
            }

            JSONObject obj = null;
            try {
                obj = new JSONObject(msg);

                if ("0".equals(bodyType)) {
                    msg = obj.getString("msg");
                } else if ("2".equals(bodyType)) {
                    msg = "[图片]";
                } else if ("3".equals(bodyType)) {
                    msg = "[语音]";
                } else if("5".equals(bodyType)) {
                    msg ="[视频]";
                } else if("6".equals(bodyType)) {
                    msg ="[位置]";
                }
                if (msg != null && msg.contains("[")) { // 适配表情
                    recentlymsg.setText(ExpressionUtil.getText(getActivity(),StringUtil.Unicode2GBK(msg)));
                }else{
                    recentlymsg.setText(msg);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            rostertime.setText(time);
            int count = NewMsgDbHelper.getInstance(getActivity()).getMsgCount(namebyid);
            unreadcnt.setText("" + count);
            qBadgeView.bindTarget(ll_head).setBadgeNumber(count).setGravityOffset(5, true);
            if (getrecently(namebyid)) {
                unreadcnt.setVisibility(View.VISIBLE);
            } else {
                qBadgeView.hide(true);
                unreadcnt.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    private boolean getrecently(String namebyid) {
        for (int i = 0; i < MessageConstants.recentlyMap.get(namebyid).size(); i++) {
            if (MessageConstants.recentlyMap.get(namebyid).get(i).isRead == 0) {
                return true;
            }
        }
        return false;
    }

    private void getRecentlyList() {
        List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getActivity()).getrecentlyMsg();
        if (moreChatItems.size() == 0)
            return;

        for (int i = 0; i < moreChatItems.size(); i++) {
            List<ChatItem> rosterList = new ArrayList<ChatItem>();
            rosterList = MsgDbHelper.getInstance(getActivity()).getonerecentlyMsg(moreChatItems.get(i).chatName);

            if(rosterList.get(0).chatType==1 && !MessageConstants.isMyroom(rosterList.get(0).chatName)){
                MsgDbHelper.getInstance(getActivity()).delChatMsg(rosterList.get(0).chatName);
                NewMsgDbHelper.getInstance(getActivity()).delNewMsg(rosterList.get(0).chatName);
            }

            if (rosterList.size() > 0) {
                if (MessageConstants.recently.indexOf(rosterList.get(0).chatName) == -1 && (rosterList.get(0).chatType==0 || (rosterList.get(0).chatType==1 && MessageConstants.isMyroom(rosterList.get(0).chatName)))) {
                    MessageConstants.recently.add(rosterList.get(0).chatName);
                }
                MessageConstants.recentlyMap.put(rosterList.get(0).chatName, rosterList);
            }
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
    private void init() {
        llBack.setVisibility(View.GONE);
        lhTvTitle.setText("消息");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net() {
        ContextUtil.curtab = 0;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // show自己页面时，才去网络操作
        if (!hidden) {
            net();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());

        // 圈设置里面删除了圈子
        if("ChatNewMsg".equals(intent.getAction())){
            MessageConstants.recently.clear();
            updateCount();
            getRecentlyList();
            myRecentlyAdapter.notifyDataSetChanged();
        }
    }

}