package com.sylar.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.example.administrator.circlegit.R;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.NewChatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频播放控件
 *
 */
@SuppressLint("NewApi")
public class MovieRecorderView extends LinearLayout implements OnErrorListener, Callback, Camera.PreviewCallback {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
    private int mWidth = ContextUtil.height;// 视频分辨率宽度
    private int mHeight = ContextUtil.width;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件
    private File mRecordFile1 = null;// 文件
    private File mRecordFile2 = null;// 文件
    private int mCameraFacing;

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public int getmCameraFacing() {
        return mCameraFacing;
    }

    public void setmCameraFacing(int mCameraFacing) {
        this.mCameraFacing = mCameraFacing;
    }

    public void stopPreview(){
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void startPreview(){
        if (mCamera != null){
            mCamera.startPreview();
            System.out.println("cccc");
        }
    }

    public void setCameraId(){
        try{
            mCamera = Camera.open(mCameraFacing);
            if (mCamera != null){
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);

                Parameters parameters = mCamera.getParameters();
                //请通过parameters.getSupportedPreviewSizes();设置预览大小,否则设置了一个摄像头不支持大小,将会报错.
                parameters.setPreviewSize(mWidth, mHeight); //如果设置了一个不支持的大小,会崩溃.坑2


                parameters.getSupportedPictureSizes();//设置拍照图片大小,这一步对于录像来说是非必须的.
                parameters.setPictureSize(mWidth, mHeight);
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    //设置对焦模式
                    parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                mCamera.setParameters(parameters);

                mCamera.setPreviewCallback(this);
                startPreview();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressLint("NewApi")
	public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 初始化各项组件
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);

        isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// 默认为10

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isOpenCamera)
            return;
        try {
            initCamera();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!isOpenCamera)
            return;

        freeCameraResource();
    }


    /**
     * 初始化摄像头
     * @date 2015-2-5
     * @throws IOException
     */
    @SuppressLint("NewApi")
	public void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open(mCameraFacing);
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;

        // setCameraParams();
//        if(mCameraFacing == 1){
//            mCamera.setDisplayOrientation(270);
//        } else {
//            mCamera.setDisplayOrientation(90);
//        }

        mCamera.setDisplayOrientation(90);


        Parameters parameters = mCamera.getParameters();
        //请通过parameters.getSupportedPreviewSizes();设置预览大小,否则设置了一个摄像头不支持大小,将会报错.
        parameters.setPreviewSize(mWidth, mHeight); //如果设置了一个不支持的大小,会崩溃.坑2


        parameters.getSupportedPictureSizes();//设置拍照图片大小,这一步对于录像来说是非必须的.
        parameters.setPictureSize(mWidth, mHeight);
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            //设置对焦模式
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
        mCamera.setPreviewCallback(this);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
//        mCamera.unlock();
    }

    /**
     * 初始化
     * @date 2015-2-5
     * @throws IOException
     */
    private void initRecord(File file) throws IOException {
        try {
            mCamera.unlock();

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            if (mCamera != null){
                mMediaRecorder.setCamera(mCamera);
            }

//          mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
//          mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//          mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
//          mMediaRecorder.setVideoEncodingBitRate(1 * 1280 * 720);// 设置帧频率，然后就清晰了
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式

//          CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW); //中间参数看你项目需要视频什么样的品质，具体可以看源码；
//          mMediaRecorder.setProfile(cProfile);
//          mMediaRecorder.setVideoFrameRate(20);

            if(mCameraFacing == 1){
                mMediaRecorder.setOrientationHint(270);// 输出旋转270度，保持竖屏录制
            } else {
                mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
            }

            mMediaRecorder.setOutputFile(file.getAbsolutePath());
//          mMediaRecorder.setMaxDuration(3000);
            mMediaRecorder.setVideoEncodingBitRate(mWidth * mHeight);
            mMediaRecorder.setVideoSize(mWidth, mHeight);
            mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//          mSurfaceHolder.setFixedSize(640,480);//最高只能设置640x480
            mMediaRecorder.setOnErrorListener(this);
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            if (Build.VERSION.SDK_INT >= 14) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                    }
                });
            } else {
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void revertCamera() throws IOException {
//        mMediaRecorder.pause();
//        mMediaRecorder.setOnErrorListener(null);
//        mMediaRecorder.setPreviewDisplay(null);
//        try {
//            mMediaRecorder.stop();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            mMediaRecorder.release();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        freeCameraResource();

        stop();

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.unlock();
            mCamera.release();
            mCamera = null;
        }
        try {
//          mCamera = Camera.open(mCameraFacing);
            mCamera = Camera.open(1);
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }


        if (mCamera == null)
            return;

        // setCameraParams();
//        if(mCameraFacing == 1){
//            mCamera.setDisplayOrientation(270);
//        } else {
//            mCamera.setDisplayOrientation(90);
//        }

        mCamera.setDisplayOrientation(180);

        Parameters parameters = mCamera.getParameters();
        //请通过parameters.getSupportedPreviewSizes();设置预览大小,否则设置了一个摄像头不支持大小,将会报错.
        parameters.setPreviewSize(mWidth, mHeight); //如果设置了一个不支持的大小,会崩溃.坑2

        parameters.getSupportedPictureSizes();//设置拍照图片大小,这一步对于录像来说是非必须的.
        parameters.setPictureSize(mWidth, mHeight);
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            //设置对焦模式
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(parameters);
        mCamera.setPreviewCallback(this);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();

        try {
            createRecordDir2();
            mCamera.unlock();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setOutputFile(mRecordFile2.getAbsolutePath());
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//          mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
//          mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式

//            if(mCameraFacing == 1){
//                mMediaRecorder.setOrientationHint(180);// 输出旋转270度，保持竖屏录制
//            } else {
//                mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
//            }

            mMediaRecorder.setOrientationHint(270);


            mMediaRecorder.setVideoEncodingBitRate(mWidth * mHeight);
            mMediaRecorder.setVideoSize(mWidth, mHeight);
//          mMediaRecorder.setVideoEncoder(VideoEncoder.H264);
            mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);

            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setOnErrorListener(this);

            mMediaRecorder.prepare();
            mMediaRecorder.start();


            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean setCamera() {
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                int numberOfCameras = Camera.getNumberOfCameras();

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == mCameraFacing) {
                        mCameraFacing= i;
                    }
                }
            }

            freeCameraResource();
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);

            Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mWidth, mHeight); //如果设置了一个不支持的大小,会崩溃.坑2
            parameters.getSupportedPictureSizes();//设置拍照图片大小,这一步对于录像来说是非必须的.
            parameters.setPictureSize(mWidth, mHeight);
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                //设置对焦模式
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
            mCamera.cancelAutoFocus();
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            startPreview();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void changeCamara() {
        // 切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
            if (mCameraFacing == 1) {
                // 现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    mCamera.stopPreview();// 停掉原来摄像头的预览
                    mCamera.release();// 释放资源
                    mCamera = null;// 取消原来摄像头
//                    frontCameraRotate();// 前置旋转摄像头度数
                    mCamera = Camera.open(i);// 打开当前选中的摄像头
//                    camaraType = i;
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);// 通过surfaceview显示取景画面
                        mCamera.setDisplayOrientation(90);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();// 开始预览
                    mCameraFacing = 0;
//                    isCheck = true;
                    break;
                }
            } else {
                // 现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    // CAMERA_FACING_BACK后置
                    mCamera.stopPreview();// 停掉原来摄像头的预览
                    mCamera.release();// 释放资源
                    mCamera = null;// 取消原来摄像头
                    mCamera = Camera.open(1);// 打开当前选中的摄像头

//                    camaraType = i;
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);// 通过surfaceview显示取景画面
                        mCamera.setDisplayOrientation(90);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();// 开始预览
                    mCameraFacing = 1;
//                    isCheck = false;
//                    doChange(surfaceView.getHolder());
                    break;
                }
            }
        }
    }

    public void combine0() {
        File[] tempFiles = new File[2];
        tempFiles[0] = mRecordFile1;
        tempFiles[1] = mRecordFile2;

//        if (tempFiles.isEmpty()) return;      //如果还没录制则，不进行合并
        File storagePath = new File(Constants.SAVE_MOVIE_PATH + File.separator);
        mRecordFile = new File(storagePath, "qz_camera_"+System.currentTimeMillis()+"_0.mp4");
        FileChannel mFileChannel;
        try {
            FileOutputStream fos=new FileOutputStream(mRecordFile);
            mFileChannel=fos.getChannel();
            FileChannel inFileChannel;
            for(File file:tempFiles){
                inFileChannel=new FileInputStream(file).getChannel();
                //下面应该根据不同文件减去相应的文件头（这里没有剪去文件头，实际应用中应当减去）
                inFileChannel.transferTo(0, inFileChannel.size(), mFileChannel);
                inFileChannel.close();
            }
            fos.close();
            mFileChannel.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void combine1() {
        String[] tempFiles = new String[2];
        tempFiles[0] = mRecordFile1.getAbsolutePath();
        tempFiles[1] = mRecordFile2.getAbsolutePath();

//      if (tempFiles.isEmpty()) return;//如果还没录制则，不进行合并
        File storagePath = new File(Constants.SAVE_MOVIE_PATH + File.separator);
//      storagePath.mkdirs();
        mRecordFile = new File(storagePath, "qz_camera_"+System.currentTimeMillis()+"_0.mp4");
//      File realFile=getFile(false);
        try {
            FileOutputStream fos=new FileOutputStream(mRecordFile);
            for (int i = 0; i < tempFiles.length; i++) {//遍历tempFiles集合，合并所有临时文件
                FileInputStream fis=new FileInputStream(tempFiles[i]);
                byte[] tmpBytes = new byte[fis.available()];
                int length = tmpBytes.length;//文件长度
                //头文件
                if(i==0){
                    while(fis.read(tmpBytes)!=-1){
                        fos.write(tmpBytes,0,length);
                    }
                }
                //之后的文件，去掉头文件就可以了.amr格式的文件的头信息为 6字节
                else{
                    while(fis.read(tmpBytes)!=-1){
                        fos.write(tmpBytes,0,length);
                    }
                }
                fos.flush();
                fis.close();
            }
            fos.close();//所有的文件合并结束，关闭输出流
            Log.i("info", "此次录音文件："+mRecordFile.getName()+" 已保存到："+ mRecordFile.getAbsolutePath()+"目录下");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //删除合并过的临时文件
//        for (File file:tempFiles) {
//            if (file.exists()) {
//                file.delete();
//            }
//        }
    }

    public void combine(){

        String[] videosToMerge = new String[2];
        videosToMerge[0] = mRecordFile1.getAbsolutePath();
        videosToMerge[1] = mRecordFile2.getAbsolutePath();

        int count = videosToMerge.length;
        try {
            Movie[] inMovies = new Movie[count];
//            for (int i = 0; i < count; i++) {
////                inMovies[i] = MovieCreator.build(videosToMerge[i]);
////                if(i==1){
////                    inMovies[i].setMatrix(Matrix.ROTATE_180);
////                }
//
////                FileInputStream fis = new FileInputStream(videosToMerge[i]);
////                inMovies[i] = MovieCreator.build(fis.getChannel());
//
//                inMovies[i] = MovieCreator.build(videosToMerge[i]);
//                if(i==1){
//                    inMovies[i].setMatrix(Matrix.ROTATE_180);
//                }
//
//                Log.e("Matrix===", "==="+inMovies[i].getMatrix());
//            }

            for (int i = 0; i < count; i++) {
                IsoFile isoFile = new IsoFile(videosToMerge[i]);
                Movie m = new Movie();

                List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);

                for (TrackBox trackBox : trackBoxes) {
                    trackBox.getTrackHeaderBox().setMatrix(Matrix.ROTATE_180);
                    m.addTrack(new Mp4TrackImpl(trackBox, isoFile));
                }
                inMovies[i] = m;
            }

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            //提取所有视频和音频的通道
            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                    if (t.getHandler().equals("")) {
                    }
                }
            }

            //添加通道到新的视频里
            Movie result = new Movie();
            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }
            Container mp4file = new DefaultMp4Builder().build(result);

            //开始生产mp4文件
            File storagePath = new File(Constants.SAVE_MOVIE_PATH + File.separator);
//          storagePath.mkdirs();
            mRecordFile = new File(storagePath, "qz_camera_"+System.currentTimeMillis()+"_0.mp4");
            FileOutputStream fos =  new FileOutputStream(mRecordFile);
            FileChannel fco = fos.getChannel();
//          FileChannel fco = new RandomAccessFile(String.format(Constants.SAVE_MOVIE_PATH + File.separator+"qz_camera_"+System.currentTimeMillis()+"_0.mp4"), "rw").getChannel();
            mp4file.writeContainer(fco);


            fco.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combine2(){
        File[] videoPartArr = new File[2];
        videoPartArr[0] = mRecordFile1;
        videoPartArr[1] = mRecordFile2;

        File sampleDir = new File(Constants.SAVE_MOVIE_PATH + File.separator);
        mRecordFile = new File(sampleDir, "qz_camera_"+System.currentTimeMillis()+"_0.mp4");
        try (FileOutputStream writer = new FileOutputStream(mRecordFile)) {
            byte buffer[] = new byte[5*1024*1024];
            for (File part : videoPartArr) {
                try (FileInputStream reader = new FileInputStream(part)) {
                    while (reader.read(buffer) != -1) {
                        writer.write(buffer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (paths == null || 1>= paths.size()) {
//            return false;
//        }else {
//            String combineMp3Path = targetDir +"/"+ getCurrentDetailTime() + "-combine.mp4";
//            String[] strings = new String[paths.size()];
//            for (int i = 0; i < paths.size(); i++) {
//                strings[i] = paths.get(i);
//            }
//            VideoSplicing videoSplicing = new VideoSplicing(activity,strings,combineMp3Path);
//            videoSplicing.videoSplice();
//            targetFile = new File(combineMp3Path);
//            if (targetFile.exists()) {
//                for (int i = 0; i < paths.size(); i++) {
//                    File f = new File(paths.get(i));
//                    f.delete();
//                }
//                paths.clear();
//            }
//            paths.add(combineMp3Path);
//            return true;
//        }
    }

    /**
     * 释放摄像头资源
     * @date 2015-2-5
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createRecordDir() {
        File sampleDir = new File(Constants.SAVE_MOVIE_PATH + File.separator);

        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        // 创建文件
        try {
            mRecordFile = new File(sampleDir, "qz_camera_"+System.currentTimeMillis()+".mp4");
            Log.i("TAG", mRecordFile.getAbsolutePath());
        } catch (Exception e) {
        }
    }

    private void createRecordDir1() {
        File sampleDir = new File(Constants.SAVE_MOVIE_PATH + File.separator);

        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        // 创建文件
        try {
            mRecordFile1 = new File(sampleDir, "qz_camera_"+System.currentTimeMillis()+".mp4");
            Log.i("TAG", mRecordFile1.getAbsolutePath());
        } catch (Exception e) {
        }
    }

    private void createRecordDir2() {
        File sampleDir = new File(Constants.SAVE_MOVIE_PATH + File.separator);

        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        // 创建文件
        try {
            mRecordFile2 = new File(sampleDir, "qz_camera_"+System.currentTimeMillis()+"_2.mp4");
            Log.i("TAG", mRecordFile2.getAbsolutePath());
        } catch (Exception e) {
        }
    }



    /**
     * 开始录制视频
     *
     * 
     * @date 2015-2-5
     * @param
     *
     * @param onRecordFinishListener
     *            达到指定时间之后回调接口
     */
    public void record(final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir1();
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            initRecord(mRecordFile1);
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     *
     * 
     * @date 2015-2-5
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     *
     * 
     * @date 2015-2-5
     */
    public void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseRecord() {
        if (mMediaRecorder != null) {
            // 设置后不会崩
            try {
                mMediaRecorder.pause();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeRecord() {
        if (mMediaRecorder != null) {
            // 设置后不会崩
            try {
                mMediaRecorder.resume();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 释放资源
     *
     * 
     * @date 2015-2-5
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile1;
    }

    /**
     * 录制完成回调接口
     *
     * 
     *
     * @date 2015-2-5
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}