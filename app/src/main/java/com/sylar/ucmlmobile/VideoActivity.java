package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.circlegit.R;
import com.sylar.view.videoview.SurfaceVideoViewCreator;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class VideoActivity extends AppCompatActivity {
    private SurfaceVideoViewCreator surfaceVideoViewCreator;
    private int width = 0;
    private int height = 0;
    public static int navHeight = 0;
    private boolean isAutoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindowManager().getDefaultDisplay().getHeight();
        navHeight = getNavigationBarHeight();

        setContentView(R.layout.activity_video);
        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);
        isAutoPlay = true;

        ActivityCompat.requestPermissions(
                VideoActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1
        );
        final String videoPath = getIntent().getStringExtra("videoPath");
        surfaceVideoViewCreator =
                new SurfaceVideoViewCreator(this, (RelativeLayout) findViewById(R.id.activity_video)) {
                    @Override
                    protected Activity getActivity() {
                        return VideoActivity.this;     /** 当前的 Activity */
                    }

                    @Override
                    protected boolean setAutoPlay() {
                        return isAutoPlay;                 /** true 适合用于，已进入就自动播放的情况 */
                    }

                    @Override
                    protected int getSurfaceWidth() {
                        /** Video 的显示区域宽度，0 就是适配手机宽度 */
                        return width;
                    }

                    @Override
                    protected int geturfaceHeight() {
                        /** Video 的显示区域高度，dp 为单位 */
                        return height;
                    }

                    @Override
                    protected void setThumbImage(ImageView thumbImageView) {
                        if (width > 0 && height > 0) {
                            Glide.with(VideoActivity.this)
                                    .load(getIntent().getStringExtra("img"))
                                    .override((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics()),
                                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()))
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.default_icon)
                                    .into(thumbImageView);
                        } else {
                            Glide.with(VideoActivity.this)
                                    .load(getIntent().getStringExtra("img"))
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.default_icon)
                                    .into(thumbImageView);
                        }
                    }

                    /** 这个是设置返回自己的缓存路径，
                     * 应对这种情况：
                     *     录制的时候是在另外的目录，播放的时候默认是在下载的目录，所以可以在这个方法处理返回缓存
                     * */
                    @Override
                    protected String getSecondVideoCachePath() {
                        return null;
                    }

                    @Override
                    protected String getVideoPath() {
                        return videoPath;
                    }
                };
        surfaceVideoViewCreator.debugModel = false;
        surfaceVideoViewCreator.setUseCache(getIntent().getBooleanExtra("useCache", false));
    }

    public int getNavigationBarHeight() {
        if (!isNavigationBarShow()){
            return 0;
        }
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public boolean isNavigationBarShow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y!=size.y;
        }else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if(menu || back) {
                return false;
            }else {
                return true;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceVideoViewCreator.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceVideoViewCreator.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceVideoViewCreator.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        surfaceVideoViewCreator.onKeyEvent(event); /** 声音的大小调节 */
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        surfaceVideoViewCreator.onBackPressed();
        super.onBackPressed();
    }
}
