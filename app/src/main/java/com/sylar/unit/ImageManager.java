package com.sylar.unit;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.circlegit.R;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.view.GlideCircleBorderTransform;
import com.sylar.view.GlideCircleTransform;
import com.sylar.view.GlideRoundTransform;

import cn.trinea.android.common.util.StringUtils;

/**
 * Glide图片公共类
 * created by Djy
 * 2017/7/20 0020 下午 4:17
 */
public class ImageManager {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";
    private Context mContext;
    private static ImageManager instance;

    public ImageManager(Context context) {
        this.mContext = context;
    }



    public void clearDiskImageCash() {
        Glide.get(mContext).clearDiskCache();
    }

    // 将资源ID转为Uri
    public Uri resourceIdToUri(int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + FOREWARD_SLASH + resourceId);
    }


    // 加载网络图片
    public void loadUrlImage(String url, ImageView imageView) {
        Glide.with(ContextUtil.getInstance())
                .load(url)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }
    // 加载网络图片
    public void loadUrlImageWithDefaultImg(String url, ImageView imageView, int defaultImg) {
        Glide.with(ContextUtil.getInstance())
                .load(url)
                .placeholder(defaultImg)
                .error(defaultImg)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }

    // 加载drawable图片
    public void loadResImage(int resId, ImageView imageView) {
        Glide.with(mContext)
                .load(resourceIdToUri(resId))
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .crossFade()
                .into(imageView);
    }

    // 加载本地图片
    public void loadLocalImage(String path, ImageView imageView) {
        Glide.with(mContext)
                .load("file://" + path)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .crossFade()
                .into(imageView);
    }

    // 加载网络圆型图片
    public void loadCircleImage(String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.xml_circle_grey_bg)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
    }


    // 加载网络圆型图片-带边框
    public void loadCircleHasBorderImage(String url, ImageView imageView, int bordercolor, int borderwidth) {
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.xml_circle_grey_bg)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleBorderTransform(mContext, bordercolor, borderwidth))
                .into(imageView);
    }

    // 加载drawable圆型图片
    public void loadCircleResImage(int resId, ImageView imageView) {
        Glide.with(mContext)
                .load(resourceIdToUri(resId))
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
    }

    // 加载本地圆型图片
    public void loadCircleLocalImage(String path, ImageView imageView) {
        Glide.with(mContext)
                .load("file://" + path)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
    }


    // 加载圆型头像
    public void loadCircleHead(String url, ImageView imageView) {
        if(StringUtils.isBlank(url)){
            imageView.setImageResource(R.mipmap.head);
        }else {
            Glide.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
        }
    }

    // 加载圆角图片
    public void loadRoundImage(String url, ImageView imageView, float roundPx) {
        Glide.with(mContext)
                .load(url)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .bitmapTransform(new GlideRoundTransform(mContext, roundPx))
                .into(imageView);
    }
    // 加载圆角图片带边框
    public void loadRoundImage(String url, ImageView imageView, float roundPx, int bordercolor, int borderwidth) {

        Glide.with(mContext)
                .load(url)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .bitmapTransform(new GlideRoundTransform(mContext, roundPx, bordercolor, borderwidth))
                .into(imageView);
    }
}
