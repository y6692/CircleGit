package com.sylar.view.videoview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.circlegit.R;
import com.sylar.dao.MsgDbHelper;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.VideoActivity;
import com.sylar.view.LibraryCommonDialog;
import com.sylar.unit.CallServer;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：林冠宏
 * <p>
 * author: LinGuanHong,lzq is my dear wife.
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 * <p>
 * on 2017/4/26.
 */


public abstract class SurfaceVideoViewCreator
        implements
        SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, View.OnClickListener, View.OnLongClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnInfoListener {

    private SurfaceVideoView surfaceVideoView;
    private LoadingCircleView progressBar;
    private Button statusButton;
    private ImageView surface_video_screenshot;

    private File videoFile = null;
    private boolean isUseCache = false;
    private boolean mNeedResume;

    public boolean debugModel = false;
    VideoActivity activity;
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;

    LinearLayout llTimeControl;
    TextView tvCurTime;
    TextView tvTotalTime;
    SeekBar seekbar;
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    Timer mTimer;
    TimerTask mTimerTask;
    int duration;

    protected abstract Activity getActivity();

    protected abstract boolean setAutoPlay();

    protected abstract int getSurfaceWidth();

    protected abstract int geturfaceHeight();

    protected abstract void setThumbImage(ImageView thumbImageView);

    protected abstract String getSecondVideoCachePath();

    protected abstract String getVideoPath();

    public void setUseCache(boolean useCache) {
        this.isUseCache = useCache;
    }

    public SurfaceVideoViewCreator(Activity activity, ViewGroup container) {
        this.activity = (VideoActivity) activity;

        View view = LayoutInflater
                .from(activity)
                .inflate(R.layout.surface_video_view_layout, container, false);

        container.addView(view);

        surfaceVideoView = (SurfaceVideoView) view.findViewById(R.id.surface_video_view);
        progressBar = (LoadingCircleView) view.findViewById(R.id.surface_video_progress);
        statusButton = (Button) view.findViewById(R.id.surface_video_button);
        surface_video_screenshot = (ImageView) view.findViewById(R.id.surface_video_screenshot);
        setThumbImage(surface_video_screenshot);

        llTimeControl = (LinearLayout) view.findViewById(R.id.ll_time_control);
        tvCurTime = (TextView) view.findViewById(R.id.tv_curTime);
        tvTotalTime = (TextView) view.findViewById(R.id.tv_totalTime);
        seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new MySeekbar());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llTimeControl.getLayoutParams();
        params.setMargins(0, 0, 0, VideoActivity.navHeight);// 通过自定义坐标来放置你的控件
        llTimeControl.setLayoutParams(params);

        Log.e("===Duration", "===" + surfaceVideoView.getDuration());

//----------定时器记录播放进度---------//
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isChanging == true) {
                    return;
                }

                handler.sendEmptyMessage(0);

            }
        };
        mTimer.schedule(mTimerTask, 0, 2);


        int width = getSurfaceWidth();
        if (width != 0) {
            /** 默认就是手机宽度 */
            surfaceVideoView.getLayoutParams().width = (int) TypedValue.applyDimension
                    (
                            TypedValue.COMPLEX_UNIT_DIP, width, container.getContext().getResources().getDisplayMetrics()
                    );
        }
        int height = geturfaceHeight();
        if (height != 0) {
            view.findViewById(R.id.surface_video_container).getLayoutParams().height
                    =
                    (int) TypedValue.applyDimension
                            (
                                    TypedValue.COMPLEX_UNIT_DIP, geturfaceHeight(), container.getContext().getResources().getDisplayMetrics()
                            );
        }
        view.findViewById(R.id.surface_video_container).requestLayout();

        surfaceVideoView.setOnPreparedListener(this);
        surfaceVideoView.setOnPlayStateListener(this);
        surfaceVideoView.setOnErrorListener(this);
        surfaceVideoView.setOnInfoListener(this);
        surfaceVideoView.setOnCompletionListener(this);

        surfaceVideoView.setOnClickListener(this);
        surfaceVideoView.setOnLongClickListener(this);

        Log.e("===VideoPath", Environment.getExternalStorageDirectory().getAbsolutePath() + "===" + getVideoPath());

//        if (setAutoPlay()) {
//            prepareStart(getVideoPath());
//        } else {
//            statusButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    /** 点击即加载 */
//                    /** 这里进行本地是否存在判断 */
//
//                    Log.e("===statusButton", "==="+getVideoPath());
//
//                    prepareStart(getVideoPath());
//                }
//            });
//        }

        prepareStart(getVideoPath());

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 点击即加载 */
                /** 这里进行本地是否存在判断 */

                Log.e("===statusButton", "===" + getVideoPath());


                if (surfaceVideoView.isPlaying()) {
                    onPause();
                    //statusButton.setVisibility(View.VISIBLE);
                } else {
                    onResume();
                    //statusButton.setVisibility(View.GONE);
                }

//                mTimer = new Timer();
//                mTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if(isChanging==true) {
//                            return;
//                        }
//
//                        seekbar.setProgress(surfaceVideoView.getCurrentPosition());
//                    }
//                };
//                mTimer.schedule(mTimerTask, 0, 2);
//
//                prepareStart(getVideoPath());
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                seekbar.setProgress(surfaceVideoView.getCurrentPosition());
                tvCurTime.setText("00:0"+surfaceVideoView.getCurrentPosition()/1000);

            }else if (msg.what == 1) {

            }



        }
    };


    //进度条处理
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            surfaceVideoView.seekTo(seekbar.getProgress());
            isChanging = false;
        }

    }

    private void prepareStart(String videoPath) {
        try {
            String rootPath = Constants.SAVE_MOVIE_PATH + File.separator;
            File file = new File(rootPath);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    throw new NullPointerException("创建 rootPath 失败，注意 6.0+ 的动态申请权限");
                }
            }

            String[] temp = videoPath.split("/");
            videoFile =
                    new File(Constants.SAVE_MOVIE_PATH + File.separator
                            + temp[temp.length - 1]);

            if (debugModel) {
                /** 测试模式 */
                if (isUseCache) {
                    play(videoFile.getAbsolutePath());
                } else {
                    if (videoFile.exists()) {
                        videoFile.delete();
                        videoFile.createNewFile();
                    }
//                    new MyAsyncTask().execute(getVideoPath());
                    download(getVideoPath());
                }
                return;
            }
            /** 实际情况 */
            if (videoFile.exists()) {     /** 存在缓存 */
                play(videoFile.getAbsolutePath());
            } else {
                String secondCacheFilePath = getSecondVideoCachePath(); /** 第二缓存目录，应对此种情况，例如，本地上传是一个目录，那么就可能要到这个目录找一下 */
                if (secondCacheFilePath != null) {
                    play(secondCacheFilePath);
                    return;
                }
                videoFile.createNewFile();
//                new MyAsyncTask().execute(getVideoPath());         /** 下载再播放 */
                download(getVideoPath());
            }

        } catch (Exception e) {
            Log.d("zzzzz", e.toString());
        }
    }

    public void onKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!getActivity().isFinishing())
                    surfaceVideoView.dispatchKeyEvent(getActivity(), event);
                break;
        }
    }

    public void onDestroy() {
        progressBar = null;
        statusButton = null;
        if (surfaceVideoView != null) {
            surfaceVideoView.release();
            surfaceVideoView = null;
        }
    }

    public void onResume() {
        if (surfaceVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (surfaceVideoView.isRelease()) {
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging == true) {
                            return;
                        }

                        handler.sendEmptyMessage(0);
                    }
                };
                mTimer.schedule(mTimerTask, 0, 2);

                surfaceVideoView.reOpen();
            } else {
                surfaceVideoView.start();
            }

        }
    }

    public void onPause() {
        if (surfaceVideoView != null) {
            if (surfaceVideoView.isPlaying()) {
                mNeedResume = true;
                surfaceVideoView.pause();
            }
        }
    }

    private void play(String path) {
        if (!surfaceVideoView.isPlaying()) {
            progressBar.setVisibility(View.GONE);
            statusButton.setVisibility(View.GONE);
            surfaceVideoView.setVideoPath(path);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        if (!getActivity().isFinishing())
//            surfaceVideoView.reOpen();
//
//        surfaceVideoView.seekTo(0);

//        onPause();
//        mNeedResume = true;

//        mTargetState = SurfaceVideoView.STATE_PREPARED;

        mTimer.cancel();

        mNeedResume = true;
        surfaceVideoView.release();
        surfaceVideoView.seekTo(0);
        seekbar.setProgress(0);


        statusButton.setVisibility(View.VISIBLE);
        llTimeControl.setVisibility(View.GONE);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("zzzzz", "播放失败 onError " + what);
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                /** 音频和视频数据不正确 */
                Log.d("zzzzz", "音频和视频数据不正确 ");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START: /** 缓冲开始 */
                if (!getActivity().isFinishing()) {
                    surfaceVideoView.pause();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:   /** 缓冲结束 */
                if (!getActivity().isFinishing())
                    surfaceVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: /** 渲染开始 rendering */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    surfaceVideoView.setBackground(null);
                } else {
                    surfaceVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        Log.e("onPrepared===", "播放开始 onPrepared "+mp.getDuration());

        duration = mp.getDuration();

        MsgDbHelper.getInstance(activity).saveVideoMsg(getVideoPath(), videoFile.getAbsolutePath(), duration/1000);

        Log.e("finish===", duration+"==="+MsgDbHelper.getInstance(activity).getonevideoMsg(getVideoPath()).get("msgUrl")+">>>"+MsgDbHelper.getInstance(activity).getonevideoMsg(getVideoPath()).get("duration"));


        seekbar.setMax(mp.getDuration());
        tvTotalTime.setText("00:0"+mp.getDuration()/1000);

        surfaceVideoView.setVolume(SurfaceVideoView.getSystemVolumn(getActivity()));
        surfaceVideoView.start();
        //progressBar.setVisibility(View.GONE);
        surface_video_screenshot.setVisibility(View.GONE);



    }

    @Override
    public void onClick(View v) {
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
            return;
        }


        if (llTimeControl.getVisibility() == View.GONE) {
            statusButton.setVisibility(View.VISIBLE);
            llTimeControl.setVisibility(View.VISIBLE);
        } else {
            statusButton.setVisibility(View.GONE);
            llTimeControl.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onLongClick(View v) {
        Log.e("onLongClick===", "onLongClick");

        showMenu();

        return true;
    }

    protected void showMenu() {

        LibraryCommonDialog.videoMenuDialog(activity, new LibraryCommonDialog.VideoMenuCallback() {
            @Override public void onSaveVideo() {

                copyFile(videoFile.getAbsolutePath(), Constants.SAVE_MOVIE_PATH + File.separator + System.currentTimeMillis()+".mp4");

            }

//            @Override public void onChoosePoc() {
//            }
        });
    }


    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();

                Toast.makeText(activity, "视频已保存至" + newPath, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public void onBackPressed() {
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
            return;
        }

        mTimer.cancel();
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        statusButton.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    /**
     * 开始下载。
     */
    private void download(String videoPath) {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null) {// 没有开始就下载。

            mDownloadRequest = new DownloadRequest(videoPath, RequestMethod.GET,
                    Constants.SAVE_MOVIE_PATH + File.separator,
                    true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getInstance().download(0, mDownloadRequest, downloadListener);

        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            statusButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);

            String message = "下载出错了：%1$s";
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = "服务器数据错误！";
            } else if (exception instanceof NetworkError) {
                messageContent = "网络不可用，请检查网络！";
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = "存储卡错误，请检查存储卡！";
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = "存储卡空间不足！";
            } else if (exception instanceof TimeoutError) {
                messageContent = "下载超时！";
            } else if (exception instanceof UnKnownHostError) {
                messageContent = "找不到服务器。";
            } else if (exception instanceof URLError) {
                messageContent = "URL地址错误。";
            } else {
                messageContent = "未知错误。";
            }
            message = String.format(Locale.getDefault(), message, messageContent);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            if (progressBar == null)
                return;
            progressBar.setProgerss(progress, true);
        }

        @Override
        public void onFinish(int what, String filePath) {


            play(videoFile.getAbsolutePath());
        }

        @Override
        public void onCancel(int what) {

        }
    };
}
