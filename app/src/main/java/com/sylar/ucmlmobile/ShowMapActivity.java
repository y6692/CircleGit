package com.sylar.ucmlmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.administrator.circlegit.R;
import com.sylar.unit.AMapUtil;
import com.sylar.unit.ColorUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.view.BaseView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import cn.trinea.android.common.util.StringUtils;

/**
 * 我的位置
 * created by yy
 * 2018/1/16 0016 下午 8:00
 */
public class ShowMapActivity extends BaseActivity
    implements AMap.OnMyLocationChangeListener {
  @BaseView(click="onClick")
  LinearLayout ll_back;
  @BindView(R.id.lh_tv_title)
  TextView lhTvTitle;

  public static final String INTENT_CAR = "INTENT_CAR";
  private static final int REQ_CHANGE_CAR = 0x110;
  private static final int REQ_APPLY = 0x120;
  private static final int REQ_POINT = 0x130;

  private MapView mapView;
  private AMap aMap;
  private UiSettings mUiSettings;
  private MyLocationStyle myLocationStyle;
  private float zoom = 15;
  private int type = 1; // 1 拖车 2 换胎 3 泵电
  private LatLng llMyPos;
  private LatLng llServicePos;
  public Context mContext;
  public LayoutInflater layoutInflater;
  AMapLocation aMapLoc;
  double lat;
  double lon;

  @Override
  public void onCreate(Bundle arg0) {
    super.onCreate(arg0);
    mContext = this;
    setContentView(R.layout.activity_show_map);


    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(arg0);
    lhTvTitle = (TextView) findViewById(R.id.lh_tv_title);
    lhTvTitle.setText("位置信息");
    lat = Double.parseDouble(getIntent().getStringExtra("lat"));
    lon = Double.parseDouble(getIntent().getStringExtra("lon"));
    layoutInflater = getLayoutInflater();
    initLocation();

    if (aMap == null) {
      aMap = mapView.getMap();
      setUpMap();
    }

    TextView tvAddress = (TextView) findViewById(R.id.tv_address);
    tvAddress.setText(getIntent().getStringExtra("address"));
  }

  /**
   * 设置一些amap的属性
   */
  private void setUpMap() {
    mUiSettings = aMap.getUiSettings();
    mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
    //是否显示地图中放大缩小按钮
    mUiSettings.setZoomControlsEnabled(false);
    mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
    mUiSettings.setCompassEnabled(false);// 是否显示指南针
    mUiSettings.setRotateGesturesEnabled(false);
    mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    aMap.setTrafficEnabled(false);// 显示实时交通状况
    aMap.setMapType(AMap.MAP_TYPE_NORMAL);

    myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//    myLocationStyle.strokeColor(ColorUtil.getColor(mContext, R.color.transparent));//设置定位蓝点精度圆圈的边框颜色的方法。
//    myLocationStyle.radiusFillColor(0x558291b6);//设置定位蓝点精度圆圈的填充颜色的方法。
//    myLocationStyle.interval(10 * 1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//
//    View view = layoutInflater.inflate(R.layout.auto_custom_info_window, null);
//    myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromView(view));
//    myLocationStyle.showMyLocation(true);
//
//    aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
    aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点

    aMap.setOnMyLocationChangeListener(this);// 设置位置改变事件监听器

    UiSettings uiSettings =  aMap.getUiSettings();
    uiSettings.setLogoBottomMargin(-50);

  }


  @Override public void onMyLocationChange(Location location) {

    // 定位回调监听
    if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
      llMyPos = new LatLng(location.getLatitude(), location.getLongitude());

      if (myLocationStyle.getMyLocationType() != MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER) {
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llMyPos, zoom));
      }

      llServicePos = new LatLng(lat, lon);
      addServiceMarker();

      Log.e("定位成功", location.getLatitude() + "===" + location.getLongitude() + "===" );
      getAddress();
    } else {
      Log.e("yy", "定位失败");
    }
  }

  private void initLocation() {
    boolean bLocationPermission = AndPermission.hasPermission(mContext, Permission.LOCATION);
    if (!bLocationPermission) {
      requestLocationPermission();
    } else {
      moveToMyPos();
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
    LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      ToastUtil.showMessage("您未开启位置服务，请开启！");
    }

  }

  private void moveToMyPos() {
    if (llMyPos != null) {
      aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llMyPos, zoom));
    }
  }

  private void getAddress() {
    AMapUtil.getInstence().init(this, true, new AMapUtil.GetAMapListener() {
      @Override
      public void onMapListener(String cityName, AMapLocation aMapLocation, boolean location) {

        if (location) {
          if (!StringUtils.isBlank(aMapLocation.getCityCode()) && !StringUtils.isBlank((aMapLocation.getAddress()))) {
            aMapLoc = aMapLocation;
            Log.e("定位成功===222", aMapLocation.getLatitude()+"==="+aMapLocation.getLongitude()+"==="+aMapLocation.getAddress() + "===" +aMapLocation.getAoiName() + "===" +aMapLocation.getPoiName());
          }
        } else {
          ToastUtil.showMessage("定位失败");
        }
      }
    });
  }

  /**
   * 加标记
   */
  private void addServiceMarker() {
    MarkerOptions options = new MarkerOptions();
    options.position(llServicePos);
    View view = layoutInflater.inflate(R.layout.auto_custom_info_window_service, null);
    //把view转换为图片
    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(view);
    if (descriptor != null && descriptor.getHeight() != 0 && descriptor.getWidth() != 0) {
      options.icon(descriptor);
    }
    // 从地图上删除所有的覆盖物（marker，circle，polyline 等对象），但myLocationOverlay（内置定位覆盖物）除外。
    aMap.clear(true);
    aMap.addMarker(options);
  }

  private void requestLocationPermission() {
    AndPermission.with(this).requestCode(1001).permission(Permission.LOCATION).callback(this)
        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
        // 这样避免用户勾选不再提示，导致以后无法申请权限。
        // 你也可以不设置。
        .rationale(new RationaleListener() {
          @Override
          public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
            AndPermission.rationaleDialog(mContext, rationale).show();
          }
        }).start();
  }

  @PermissionYes(1001) private void getLocationYes(@NonNull List<String> grantedPermissions) {
    moveToMyPos();
  }

  @PermissionNo(1001) private void getLocationNo(@NonNull List<String> grantedPermissions) {
    ToastUtil.showMessage( "为了app能正常使用，请打开定位权限");
  }


  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case REQ_APPLY:
          finish();
          break;
      }
    }
  }

  public void onClick(View v){
    int id = v.getId();
    if (id == R.id.ll_back) {
      finish();
    } else if (id == R.id.iv_dingwei) {
      initLocation();
    } else if (id == R.id.iv_showmap) {
      Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat="+llMyPos.latitude+"&slon="+llMyPos.longitude+"&sname=A&did=BGVIS2&dlat="+lat+"&dlon="+lon+"&dname=B&dev=0&t=0"));
      intent.setPackage("com.autonavi.minimap");
      startActivity(intent);
    }
  }
}
