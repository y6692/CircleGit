package com.sylar.ucmlmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.baidu.mapapi.PoiOverlay;
import com.example.administrator.circlegit.R;
import com.flyco.roundview.RoundTextView;
import com.sylar.dao.MsgDbHelper;
import com.sylar.model.ChatItem;
import com.sylar.model.LatLngEntity;
import com.sylar.model.LocationBean;
import com.sylar.unit.AMapUtil;
import com.sylar.unit.ColorUtil;
import com.sylar.unit.GeoCoderUtil;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.BaseView;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * 我的位置
 * created by yy
 * 2018/1/16 0016 下午 8:00
 */
public class MyMapActivity extends BaseActivity
    implements AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener, TextWatcher, AMap.OnCameraChangeListener{
  @BaseView(click="onClick")
  LinearLayout ll_back, ll_right;
  @BindView(R.id.lh_tv_title)
  TextView lhTvTitle;


  private static final int REQ_APPLY = 0x120;
  private int mWidth = ContextUtil.height;// 视频分辨率宽度
  private int mHeight = ContextUtil.width;// 视频分辨率高度
  private MapView mapView;
  private AMap aMap;
  private UiSettings mUiSettings;
  private MyLocationStyle myLocationStyle;
  private float zoom = 15;
  private LatLng llMyPos;
  public Context mContext;
  public LayoutInflater layoutInflater;
  AMapLocation aMapLoc;
  private String city="";
  private PoiSearch.Query query;// Poi查询条件类
  private PoiSearch poiSearch;
  double curLat;
  double curLon;
  private ListView lv_data;
  private PoiAdapter poiAdapter;

  @Override
  public void onCreate(Bundle arg0) {
    super.onCreate(arg0);
    mContext = this;

    setContentView(R.layout.activity_my_map);
    mapView = (MapView) findViewById(R.id.mapView);
    mapView.onCreate(arg0);
    lhTvTitle = (TextView) findViewById(R.id.lh_tv_title);
    lhTvTitle.setText("位置");
    layoutInflater = getLayoutInflater();
    initLocation();

    if (aMap == null) {
      aMap = mapView.getMap();
      setUpMap();
      aMap.setOnCameraChangeListener(this);
    }

    ll_right = (LinearLayout) findViewById(R.id.ll_right);
    ll_right.setVisibility(View.VISIBLE);
    TextView tv_right = (TextView) findViewById(R.id.tv_right);
    tv_right.setText("发送");
    lv_data = (ListView) findViewById(R.id.lv_data);
    poiAdapter = new PoiAdapter(this);
    lv_data.setOnItemClickListener(this);
    lv_data.setAdapter(poiAdapter);

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

//    View view = layoutInflater.inflate(R.layout.auto_custom_info_window, null);
//    myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromView(view));
//    myLocationStyle.showMyLocation(true);

//    aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
    aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点
    aMap.setOnMyLocationChangeListener(this);// 设置位置改变事件监听器

    UiSettings uiSettings =  aMap.getUiSettings();
    uiSettings.setLogoBottomMargin(-50);
  }

  @Override public void onMyLocationChange(Location location) {
    // 定位回调监听
    if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {

      curLat = location.getLatitude();
      curLon = location.getLongitude();
      llMyPos = new LatLng(location.getLatitude(), location.getLongitude());


      if (myLocationStyle.getMyLocationType() != MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER) {
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llMyPos, zoom));
      }

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

            city = aMapLoc.getStreet();
            doSearchQuery();

            Log.e("定位成功===222", aMapLocation.getLatitude()+"==="+aMapLocation.getLongitude()+"==="+aMapLocation.getAddress() + "===" +aMapLocation.getAoiName() + "===" +aMapLocation.getPoiName());
          }
        } else {
          ToastUtil.showMessage("定位失败");
        }
      }
    });
  }

  @Override
  public void onCameraChangeFinish(final CameraPosition cameraPosition) {
    curLat = cameraPosition.target.latitude;
    curLon = cameraPosition.target.longitude;
    doSearchQuery();
  }

  /**
   * 开始进行poi搜索
   */
  protected void doSearchQuery() {
    aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
    int currentPage = 0;
    query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
    query.setPageSize(20);// 设置每页最多返回多少条poiitem
    query.setPageNum(currentPage);// 设置查第一页
    LatLonPoint lp = new LatLonPoint(curLat, curLon);

    poiSearch = new PoiSearch(this, query);
    poiSearch.setOnPoiSearchListener(this);
    poiSearch.setBound(new PoiSearch.SearchBound(lp, 2000, true));
    // 设置搜索区域为以lp点为圆心，其周围2000米范围
    poiSearch.searchPOIAsyn();// 异步搜索
  }

  @Override
  public void onPoiSearched(PoiResult poiResult, int rCode) {
    if(rCode == 1000) {
      if (poiResult != null && poiResult.getQuery() != null) {
        ArrayList<LocationBean> datas = new ArrayList<>();
        ArrayList<PoiItem> items = poiResult.getPois();
        for (PoiItem item : items) {
          //获取经纬度对象
          LatLonPoint llp = item.getLatLonPoint();
          double lon = llp.getLongitude();
          double lat = llp.getLatitude();
          //获取标题
          String title = item.getTitle();
          //获取内容
          String text = item.getSnippet();

          Log.e("onPoiSearched===", title+"==="+text);

          datas.add(new LocationBean(lon, lat, title, text));
        }
        poiAdapter.setData(datas);
        poiAdapter.notifyDataSetChanged();
      }
    }
  }

  @Override
  public void onPoiItemSearched(PoiItem poiItem, int i) {
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

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    mapView.onDestroy();
    super.onDestroy();
  }


  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {

  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    //POI的地址的listview的item的点击
    LocationBean bean = (LocationBean) poiAdapter.getItem(i);;
    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bean.getLat(),bean.getLon()), 15));
  }

  @Override
  public void onCameraChange(CameraPosition cameraPosition) {

  }

  class PoiAdapter extends BaseAdapter {

    private List<LocationBean> datas = new ArrayList<>();

    private static final int RESOURCE = R.layout.app_list_item_poi;

    public PoiAdapter(Context context) {}

    @Override
    public int getCount() {
      return datas.size();
    }

    @Override
    public Object getItem(int position) {
      return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder vh = null;
      if(convertView == null){
        vh = new ViewHolder();
        convertView = getLayoutInflater().inflate(RESOURCE, null);
        vh.tv_title = (TextView) convertView.findViewById(R.id.address);
        vh.tv_text = (TextView) convertView.findViewById(R.id.addressDesc);
        convertView.setTag(vh);
      }else{
        vh = (ViewHolder) convertView.getTag();
      }
      LocationBean bean = (LocationBean) getItem(position);
      vh.tv_title.setText(bean.getTitle());
      vh.tv_text.setText(bean.getContent());
      return convertView;
    }

    private class ViewHolder{
      public TextView tv_title;
      public TextView tv_text;
    }

    public void setData(List<LocationBean> datas){
      this.datas = datas;
      if(datas.size()>0){
      }else{
        ToastUtil.showMessage("没有搜索结果");
      }
    }
  }

  public void onClick(View v){
    int id = v.getId();
    if (id == R.id.ll_back) {
      aMap.stopAnimation();
      finish();
    }else if (id == R.id.iv_dingwei) {
      Log.e("定位===", "===");
      initLocation();
    }else if (id == R.id.ll_right) {
      aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {

        public void onMapScreenShot(Bitmap bitmap) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          try {
            // 保存在SD卡根目录下，图片为png格式。
            String img_Name = "screenshot"+ String.valueOf(System.currentTimeMillis()).substring(5)+".jpg";
            String path = Environment.getExternalStorageDirectory() + "/GZDMobile/images/" + img_Name;

            FileOutputStream fos = new FileOutputStream(path);
            int w=600*ContextUtil.width/1080;
            int h=w/2;
            bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth()/2-w/2, bitmap.getHeight()/2-h/2, w, h);
            boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            try {
              fos.flush();
            } catch (IOException e) {
              e.printStackTrace();
            }
            try {
              fos.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
            if (ifSuccess){
              ToastUtil.showMessage("截屏成功");
              Intent intent = new Intent();
              intent.putExtra("imgName", img_Name);
//            intent.putExtra("base64String", ImageUtil.getBase64StringFromFile(path).substring(0,1000));
              intent.putExtra("imgPath", path);
              intent.putExtra("lat", aMapLoc.getLatitude()+"");
              intent.putExtra("lon", aMapLoc.getLongitude()+"");
              intent.putExtra("name", aMapLoc.getAoiName());
              intent.putExtra("address", aMapLoc.getAddress());

              Log.e("ScreenShot===", img_Name+"==="+path+"===");

              setResult(RESULT_OK,intent);
              finish();
            } else {
              ToastUtil.showMessage("截屏失败");
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {
        }
      });
    }
  }

}
