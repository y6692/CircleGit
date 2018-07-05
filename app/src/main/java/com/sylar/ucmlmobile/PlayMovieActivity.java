package com.sylar.ucmlmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.SearchRoomAdapter;
import com.sylar.constant.Urls;
import com.sylar.fragment.VideoFragment;
import com.sylar.model.Room;
import com.sylar.model.apimodel.APIM_getCircles;
import com.sylar.model.apimodel.APIM_getMucManager;
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
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlayMovieActivity extends com.sylar.app.BaseActivity {

    private static final String VIDEO_PATH = "video_path";

    private String videoPath;

    private VideoView mVideoView;

    private Button btnClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.fragment_video);
        ButterKnife.bind(this);


        mVideoView = (VideoView) findViewById(R.id.video_view);
        btnClose = (Button) findViewById(R.id.btn_close);
        videoPath = getIntent().getStringExtra("path");


        // 播放相应的视频
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse(videoPath));
        mVideoView.start();
        //mVideoView.requestFocus();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
