package com.sylar.Receiver;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.administrator.circlegit.MainActivity;

/**
 * @author
 * @version 1.0
 * @date 2017/7/7
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    public static int h=0;

    private Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(Message msg) {

            switch(msg.what){
                case 1:

                    //Toast.makeText(MyJobService.this, "1-MyJobService=="+msg.obj, Toast.LENGTH_SHORT).show();
                    JobParameters param = (JobParameters) msg.obj;
                    jobFinished(param, true);

                    break;

                case 2:

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);


                    Intent serviceOne = new Intent();
                    //serviceOne.setClass(getApplicationContext(), MyService.class);
                    //startService(serviceOne);


                    //Toast.makeText(MyJobService.this, "2-MyJobService=="+msg.obj, Toast.LENGTH_SHORT).show();
                    param = (JobParameters) msg.obj;
                    jobFinished(param, true);

                    break;

                default:
                    break;
            }








            return true;
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(MyJobService.this, "onStartCommand=="+h, Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {


        //Toast.makeText(MyJobService.this, "onStartJob=="+h, Toast.LENGTH_SHORT).show();

        if (h==1){

            Message m = Message.obtain();
            m.obj = params;
            m.what=1;
            handler.sendMessage(m);

            return true;
        }




        h=1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){



                    Message message=new Message();
                    message.what=2;
                    mHandler.sendMessage(message);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }
            }
        });//.start();


        Message m = Message.obtain();
        m.obj = params;
        m.what=2;
        handler.sendMessage(m);


        return true;
    }

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what){
                case 1:

                    break;

                case 2:

                    Toast.makeText(getApplicationContext(), "==MyJobService run", Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        return false;
    }
}
