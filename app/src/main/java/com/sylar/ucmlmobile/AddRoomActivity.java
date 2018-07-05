package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.model.ChatItem;
import com.sylar.unit.CloseActivityClass;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddRoomActivity extends Activity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_description)
    EditText etDescription;

    Context ctx;
    Object obj;
    public static AddRoomActivity instance;
    public static int f = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addroom);
        ButterKnife.bind(this);
        ctx = this;
        ContextUtil.ctx = this;
        instance = this;

        init();
        CloseActivityClass.activityList.add(this);
    }

    private void init() {
        lhTvTitle.setText("创建圈子");
        llRight.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("创建");
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

    private void createQuan() {
        f = 1;
        new XmppLoadThread(AddRoomActivity.this) {

            @Override
            protected Object load() {
                obj = XmppConnection.getInstance().createRoom(etName.getText().toString(), etDescription.getText().toString());
                XmppConnection.getInstance().setRecevier(etName.getText().toString(), ChatItem.GROUP_CHAT);
                return obj;
            }

            @Override
            protected void result(Object object) {

                MultiUserChat muc = (MultiUserChat) object;
                if (muc != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(2 * 1000);
                                XmppConnection.getInstance().reconnect();
                                handler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            super.run();
                        }
                    }.start();
                }
            }
        };
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Tool.initToast(ContextUtil.ctx, "圈子" + etName.getText().toString() + "创建成功");
                f = 0;
                XmppLoadThread.mdialog.dismiss();
                setResult(RESULT_OK);
                finish();
            } else if (msg.what == 1) {
            }
        }
    };




    @OnClick({R.id.ll_back, R.id.ll_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_right:
                if(TextUtils.isEmpty(etName.getText().toString().trim())){
                    ToastUtil.showMessage("请输入圈名");
                    return;
                }

                if(MessageConstants.isMyroom(etName.getText().toString())){
                    ToastUtil.showMessage("该圈名已存在，请重起一个名字！");
                    return;
                }

                createQuan();
                break;
        }
    }
}
