package com.sylar.Receiver;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PustDemoActivity extends Activity {

    public final static String TAG = "PushDemo";

    private GridView gridView;

    private static TextView textView;

    public static String[] type = null;

    // 接收Push消息
    public static final int RECEIVE_PUSH_MSG = 0x100;
    // 接收Push Token消息
    public static final int RECEIVE_TOKEN_MSG = 0x101;
    // 接收Push 自定义通知消息内容
    public static final int RECEIVE_NOTIFY_CLICK_MSG = 0x102;
    // 接收Push LBS 标签上报响应
    public static final int RECEIVE_TAG_LBS_MSG = 0x103;


    private static final String CLOSE_NORMAL = "关闭-接收透传消息";
    private static final String CLOSE_NOTIFY = "关闭-接收自呈现消息";
    /*
     * 处理提示消息，更新界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RECEIVE_PUSH_MSG:
                showMsg((String) msg.obj);
                break;
            case RECEIVE_TOKEN_MSG:
                showMsg((String) msg.obj);
                break;
            case RECEIVE_NOTIFY_CLICK_MSG:
                showMsg((String) msg.obj);
                break;
            case RECEIVE_TAG_LBS_MSG:
                showToast((String) msg.obj);
                break;
            default:
                break;
            }
        }
    };

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "run into PustDemoActivity onCreate");

        super.onCreate(savedInstanceState);

        MyApplication.instance().setMainActivity(this);

    }

    /*
     * 显示接收到的Push消息，在页面上
     */
    public void showMsg(final String msg) {
        try {

            mHandler.post(new Runnable() {

                public void run() {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String dateStr = formatter.format(curDate);
                    textView.postInvalidate();
                    String str = "接收时间：" + dateStr + " , 消息内容：" + msg;
                    textView.setText(str);
                    Log.d(TAG, "showMsg:" + str);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * 显示接收到的Push消息，弹出Toast
     */
    public void showToast(String msg) {
        Log.d(TAG, "showToast:" + msg);
        Toast.makeText(PustDemoActivity.this, msg, Toast.LENGTH_LONG).show();
    }

}
