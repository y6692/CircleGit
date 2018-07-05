package com.sylar.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.baidu.mapapi.PoiOverlay;
import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.nanchen.compresshelper.CompressHelper;
import com.sylar.adapter.ImagePickerAdapter;
import com.sylar.app.BaseActivity;
import com.sylar.constant.Urls;
import com.sylar.model.LocationBean;
import com.sylar.model.apimodel.APIM_uploadFile;
import com.sylar.model.apimodel.CommonResult;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.MyMapActivity;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.unit.AMapUtil;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.ColorUtil;
import com.sylar.unit.EditFilter;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.view.SelectDialog;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;
import cn.trinea.android.common.util.StringUtils;

public class PositionActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {

    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;

    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private MyLocationStyle myLocationStyle;
    private float zoom = 15;
    private LatLng llMyPos;
    public Context mContext;
    public LayoutInflater layoutInflater;
    private AMapLocation aMapLoc;
    private String city="";
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private ListView lv_data;
    private PoiAdapter poiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        layoutInflater = getLayoutInflater();
        ButterKnife.bind(this);
        ContextUtil.ctx = this;
        mContext=this;
        initLocation();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        ContextUtil.isback=0;
        ContextUtil.ctx = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        ContextUtil.isback=1;
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


    private void initView() {
        tvLeft.setVisibility(View.VISIBLE);
        lhTvTitle.setText("所在位置");
        lv_data = (ListView) findViewById(R.id.lv_data);
        poiAdapter = new PoiAdapter(this);
        lv_data.setOnItemClickListener(this);
        lv_data.setAdapter(poiAdapter);
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
        myLocationStyle.strokeColor(ColorUtil.getColor(mContext, R.color.transparent));//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(0x558291b6);//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.interval(10 * 1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        View view = layoutInflater.inflate(R.layout.auto_custom_info_window, null);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromView(view));
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点
        aMap.setOnMyLocationChangeListener(this);// 设置位置改变事件监听器

    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            llMyPos = new LatLng(location.getLatitude(), location.getLongitude());
            getAddress();
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
                    }
                } else {
                    ToastUtil.showMessage("定位失败");
                }
            }
        });
    }

    protected void doSearchQuery() {
        aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        int currentPage = 0;
        query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        LatLonPoint lp = new LatLonPoint(llMyPos.latitude, llMyPos.longitude);

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
                    LatLonPoint llp = item.getLatLonPoint();    //获取经纬度对象
                    double lon = llp.getLongitude();
                    double lat = llp.getLatitude();
                    String title = item.getTitle();     //获取标题
                    String text = item.getSnippet();    //获取内容
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
            PoiAdapter.ViewHolder vh = null;
            if(convertView == null){
                vh = new PoiAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(RESOURCE, null);
                vh.tv_title = (TextView) convertView.findViewById(R.id.address);
                vh.tv_text = (TextView) convertView.findViewById(R.id.addressDesc);
                convertView.setTag(vh);
            }else{
                vh = (PoiAdapter.ViewHolder) convertView.getTag();
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

    @OnClick({R.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                aMap.stopAnimation();
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LocationBean bean = (LocationBean) poiAdapter.getItem(i);
        Intent intent = new Intent();
        intent.putExtra("address", bean.getTitle());
        intent.putExtra("lon", ""+bean.getLon());
        intent.putExtra("lat", ""+bean.getLat());
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
