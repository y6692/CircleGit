package com.sylar.unit;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by Wikison on 2017/3/30.
 */

public class AMapUtil {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static AMapUtil amap;
    private GetAMapListener getAMapListener;
    private Context context;

    public static AMapUtil getInstence() {
        if (amap == null) {
            amap = new AMapUtil();
        }
        return amap;
    }

    /**
     *
     * @param context
     * @param onceLocation 是否只定位一次
     * @param aMapListener
     */
    public void init(final Context context, boolean onceLocation, GetAMapListener aMapListener) {
        this.context = context;
        this.getAMapListener = aMapListener;
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    if (getAMapListener != null)
                        getAMapListener.onMapListener(aMapLocation.getCity(), aMapLocation, true);


                } else {
                    if (getAMapListener != null)
                        getAMapListener.onMapListener("定位失败", null, false);
                }

            }
        };
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        if(onceLocation){
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(true);
        }

        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(15 * 1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public interface GetAMapListener {

        void onMapListener(String cityName, AMapLocation aMapLocation, boolean location);
    }
}
