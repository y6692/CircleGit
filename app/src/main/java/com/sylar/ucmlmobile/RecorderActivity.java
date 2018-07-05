package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.circlegit.R;
import com.sylar.view.MovieRecorderView;

import java.io.IOException;

public class RecorderActivity extends Activity {
    private MovieRecorderView mRecorderView;
    private Button mShootBtn;
    private Button mShootBtn2;
    private Button mShootBtn3;
    private boolean isFinish = true;
    private boolean isRecording = false;
    private boolean initSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);
        mShootBtn2 = (Button) findViewById(R.id.shoot_button2);
        mShootBtn3 = (Button) findViewById(R.id.shoot_button3);

        mRecorderView.setmCameraFacing(1);

        mShootBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorderView.setmCameraFacing(1-mRecorderView.getmCameraFacing());

                try {
                    mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
                    mRecorderView.setCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mShootBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording){
                    if (mRecorderView.getTimeCount() > 1) {
                        handler.sendEmptyMessage(1);
                    } else {
                        if (mRecorderView.getmRecordFile() != null){
                            mRecorderView.getmRecordFile().delete();
                        }
                        mRecorderView.stop();
                        Toast.makeText(RecorderActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    isRecording = true;
                    initCameraLayout();
                }

            }
        });

        mShootBtn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRecorderView.getTimeCount() > 1) {
                        handler.sendEmptyMessage(1);
                    } else {
                        if (mRecorderView.getmRecordFile() != null){
                            mRecorderView.getmRecordFile().delete();
                        }
                        mRecorderView.stop();
                        Toast.makeText(RecorderActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }


    private void initCameraLayout() {
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                boolean result = false;

                if (!initSuccess) {
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });

                    initSuccess = true;
                }else{
                    result = mRecorderView.setCamera();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
            }

        }.execute("start");
    }



    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    public static Camera getCameraInstance(int cameraId){
        Camera c = null;
        try{
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }catch (Exception e){
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };

    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
            Intent intent = new Intent();
            Log.d("TAG", mRecorderView.getmRecordFile().getAbsolutePath());
            intent.putExtra("timecount", mRecorderView.getTimeCount());
            intent.putExtra("name", mRecorderView.getmRecordFile().getName());
            intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
            setResult(RESULT_OK,intent);
        }
        // isFinish = false;
        finish();
    }

    /**
     * 录制完成回调
     *
     * @author liuyinjun
     *
     * @date 2015-2-9
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);
        public void OnShootFailure();
    }


}
