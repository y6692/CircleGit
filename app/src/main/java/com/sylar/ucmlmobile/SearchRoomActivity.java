package com.sylar.ucmlmobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.SearchRoomAdapter;
import com.sylar.constant.Urls;
import com.sylar.model.Room;
import com.sylar.model.apimodel.APIM_getCircles;
import com.sylar.model.apimodel.APIM_getMucManager;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.unit.CallServer;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.view.SearchView;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchRoomActivity extends com.sylar.app.BaseActivity {
    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.searchview)
    SearchView searchview;

    private SearchRoomAdapter mAdapter;
    private List<Room> roomList;
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchrooms);
        ButterKnife.bind(this);

        ctx = this;
        ContextUtil.ctx = this;
        CloseActivityClass.activityList.add(this);

        init();
        showPd();
        getCircles("");
    }

    private void init() {
        lhTvTitle.setText("加入圈子");
        roomList = new ArrayList<>();
        mAdapter = new SearchRoomAdapter(ctx, roomList);
        listview.setAdapter(mAdapter);
        mAdapter.setOnAddClickListner(new SearchRoomAdapter.OnAddClickListner() {
            @Override
            public void onAddClick(Room room) {
                getMucManager(room);
            }
        });
        searchview.setBgColor(0xfff2f2f2);
        searchview.setTvCancelVisible(View.GONE);
        searchview.setMaxWordNum(15);
        searchview.setOnThinkingClickListener(new SearchView.OnThinkingClickListener() {
            @Override
            public void onThinkingClick(String text) {
                getCircles(text);
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

    // 查找圈子
    private void getCircles(String q) {
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.GET_CIRCLES, RequestMethod.POST);
        request.add("page", 1);
        if(!StringUtil.isBlank(q)){
            request.add("rows", 1000)
            .add("q", q);
        }else {
            request.add("rows", 20);
        }

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                // 请求成功。
                APIM_getCircles result = JsonUtil.jsonToObject(response.get(), APIM_getCircles.class);
                //100 ：成功 101：失败 102:输入错误
                if(result.getStatus() == 100){
                    roomList.clear();
                    roomList.addAll(result.getResults().getRows());
                    mAdapter.notifyDataSetChanged();
                }else
                    ToastUtil.showMessage(result.getMessage());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                ToastUtil.showMessage("网络请求失败");
            }

            @Override
            public void onFinish(int what) {
                super.onFinish(what);
                dismissPd();
            }
        };

        CallServer.getInstance().request(0, request, listener);
    }

    @OnClick(R.id.ll_back)
    public void onViewClicked() {
        finish();
    }


    private void getMucManager(final Room room){
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.GET_MUCMANAGER, RequestMethod.GET);
        request.add("Mucnum", room.getRoomID());

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
            @Override
            public void onStart(int what) {
                super.onStart(what);
                showPd();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                // 请求成功。
                APIM_getMucManager result = JsonUtil.jsonToObject(response.get(), APIM_getMucManager.class);
                //100 ：成功 101：失败 102:输入错误
                if(result.getStatus() == 100){
                    enterCircle(result.getResults().getJid(), room.name);
                }else
                    ToastUtil.showMessage(result.getMessage());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                ToastUtil.showMessage("网络请求失败");
            }

            @Override
            public void onFinish(int what) {
                super.onFinish(what);
                dismissPd();
            }
        };
        CallServer.getInstance().request(0, request, listener);
    }


    /**
     * 加入圈子
     * @param jid 群主jid
     * @param name  房间名字

     */
    private void enterCircle(String jid, String name){
        if(MessageConstants.isMyroom(name)){
            Tool.initToast(ctx, "你已经是该圈成员，不能重复加入！");
            return;
        }

        ChatManager cm = XmppConnection.getInstance().getConnection().getChatManager();
        Chat newchat = cm.createChat(jid, null);

        try {
            Message msg= new Message();
            msg.setBody("我想加入圈子"+name);
            msg.setProperty("join", name);
            newchat.sendMessage(msg);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
