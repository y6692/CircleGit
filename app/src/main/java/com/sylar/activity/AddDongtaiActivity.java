package com.sylar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.nanchen.compresshelper.CompressHelper;
import com.sylar.adapter.ImagePickerAdapter;
import com.sylar.app.BaseActivity;
import com.sylar.constant.Urls;
import com.sylar.model.apimodel.APIM_trendsAdd;
import com.sylar.model.apimodel.APIM_uploadFile;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.RecorderActivity;
import com.sylar.ucmlmobile.VideoActivity;
import com.sylar.unit.CallServer;
import com.sylar.unit.CircleHelper;
import com.sylar.unit.EditFilter;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;
import com.sylar.unit.StringUtil;
import com.sylar.unit.ToastUtil;
import com.sylar.view.SelectDialog;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ListUtils;

public class AddDongtaiActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_position)
    TextView tvPosition;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    // 0是好友动态 1是圈动态
    public static final String REQ_TYPE = "REQ_TYPE";
    public static final String REQ_CIRCLE_NAME= "REQ_CIRCLE_NAME";
    public static final String REQ_CIRCLE_ID = "REQ_CIRCLE_ID";
    @BindView(R.id.et_content) EditText etContent;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    private int type;
    private int circlenum;
    private String circlename;
    private List<String> urlPath = new ArrayList<>();
    private int uploadIndex = 0;
    private String imagestr = "";
    ArrayList<ImageItem> images = null;
    private String address = "";
    private String lon = "";
    private String lat = "";
    private int hasAddressInfo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dongtai);
        ButterKnife.bind(this);
        ContextUtil.ctx=this;

        images = (ArrayList<ImageItem>) getIntent().getSerializableExtra("images");
        imagestr = getIntent().getStringExtra("imageUrl");
        type = getIntent().getIntExtra(REQ_TYPE, 0);
        circlenum = getIntent().getIntExtra(REQ_CIRCLE_ID, 0);
        circlename = getIntent().getStringExtra(REQ_CIRCLE_NAME);
        initView();

        if(images !=null){
            recyclerView.setVisibility(View.VISIBLE);
            selImageList = new ArrayList<>();
            adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
            adapter.setOnItemClickListener(this);

            if (images != null) {
                selImageList.addAll(images);
                adapter.setImages(selImageList);
            }

            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            initImagePicker(false);
        }else{
            ivVideo.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams para;
            para = ivVideo.getLayoutParams();
            para.width  = 240* ContextUtil.width/1080;
            para.height = para.width*ContextUtil.height/ContextUtil.width;
            ivVideo.setLayoutParams(para);
            ivVideo.setBackgroundColor(Color.WHITE);

            ivVideo.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                if (imagestr!=null && !imagestr.equalsIgnoreCase("")) {
                    Intent intent = new Intent(AddDongtaiActivity.this, VideoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", imagestr);
                    bundle.putBoolean("useCache", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                }
            });

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 0) {
                            Log.e("imageUrl===", imagestr+"===");
                            Map map = ImageUtil.createVideoThumbnail(imagestr, MediaStore.Images.Thumbnails.MINI_KIND);
                            BitmapDrawable drawable = new BitmapDrawable((Bitmap)map.get("bitmap"));
                            ivVideo.setBackgroundDrawable(drawable);
                        }
                    }
                };
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ContextUtil.isback=0;
        ContextUtil.ctx = this;
    }

    @Override
    protected void onPause() {
        super.onPause();

        ContextUtil.isback=1;
    }

    private void initView() {
        tvLeft.setVisibility(View.VISIBLE);
        llRight.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");
        lhTvTitle.setText("创建新动态");
        EditFilter.WordFilterNoemoji(etContent, 1000);
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @OnClick({R.id.ll_back, R.id.ll_right, R.id.ll_position})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_right:
                if(selImageList != null && !selImageList.isEmpty()){
                    uploadImage(0);
                }else{
                    if(StringUtil.isBlank(etContent.getText().toString())){
                        ToastUtil.showMessage("不能发空动态");
                        return;
                    }
                    trendsAdd();
                }
                break;
            case R.id.ll_position:
                Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
                startActivityForResult(intent, 200);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                //打开选择,本次允许选择的数量
                                imagePicker.setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(AddDongtaiActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);

                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                imagePicker.setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(AddDongtaiActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }

                    }
                }, names);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(AddDongtaiActivity.this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    hasAddressInfo = 1;
                    address = data.getStringExtra("address");
                    lon = data.getStringExtra("lon");
                    lat = data.getStringExtra("lat");
                    tvPosition.setText(address);
                    break;
                case 300:
                    int timecount = data.getIntExtra("timecount", 0);
                    String imgName = data.getStringExtra("name");
                    String path = data.getStringExtra("path");

                    if (imgName != null) {
                        try {
                            File newFile = new File(path);
                            StringRequest request = new StringRequest(Urls.BASE_URL + Urls.UPLOAD_VIDEO, RequestMethod.POST);
                            request.add("filename", new FileBinary(newFile));

                            SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
                                @Override public void onStart(int what) {
                                    super.onStart(what);
                                }

                                @Override public void onSucceed(int what, Response<String> response) {
                                    super.onSucceed(what, response);
                                    APIM_uploadFile result = JsonUtil.jsonToObject(response.get(), APIM_uploadFile.class);
                                    if (result.getStatus() == 100) {
                                    } else {
                                        ToastUtil.showMessage(result.getMessage());
                                    }
                                }

                                @Override public void onFailed(int what, Response<String> response) {
                                    super.onFailed(what, response);
                                    ToastUtil.showMessage("上传失败");

                                }

                                @Override public void onFinish(int what) {
                                    super.onFinish(what);
                                }
                            };
                            CallServer.getInstance().request(0, request, listener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//					mmr.setDataSource(path.split(".")[0]+".");

//					Bitmap bitmap = Utils.createVideoThumbnail(path);
//					Bitmap bitmap = mmr.getFrameAtTime();
//					BitmapDrawable drawable = new BitmapDrawable(bitmap);
//					drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT);
//					drawable.setDither(true);
//					btnPlay.setBackgroundDrawable(drawable);
                    break;

                default:
                    break;
            }
        }
    }

    private void trendsAdd (){
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.TRENDS_ADD, RequestMethod.POST);
        request
                .add("username", CircleHelper.userManager().getUserinfo().getUsername())
                .add("content", etContent.getText().toString())
                .add("token", CircleHelper.userManager().getUserinfo().getAccesstoken())
                .add("name", CircleHelper.userManager().getUserinfo().getName())
                .add("imagestr", imagestr)
                .add("type", type); //0 是好友动态 1是圈动态

        if(type == 1){
            request.add("circlenum", circlenum).add("circlename", circlename);
        }

        request.add("address", address);
        request.add("lon", lon);
        request.add("lat", lat);
        request.add("hasAddressInfo", hasAddressInfo);

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
            @Override
            public void onStart(int what) {
                super.onStart(what);
                showPd();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                APIM_trendsAdd result = JsonUtil.jsonToObject(response.get(), APIM_trendsAdd.class);

                //100 ：成功 101：失败 102:输入错误
                if(result.getStatus() == 100){
                    ToastUtil.showMessage("创建成功");
                    setResult(RESULT_OK);
                    finish();
                }else{
                    ToastUtil.showMessage(result.getMessage());
                }


            }

            @Override
            public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                ToastUtil.showMessage("网络请求失败");
            }

            @Override
            public void onFinish(int what) {
                super.onFinish(what);
                dismissPd();
            }
        };

        CallServer.getInstance().request(0, request, listener);
    }


    //上传头像
    private void uploadImage(int index){
        File newFile = CompressHelper.getDefault(ContextUtil.getInstance()).compressToFile(new File(images.get(index).path));
        StringRequest request = new StringRequest(Urls.BASE_URL + Urls.UPLOAD_FILE, RequestMethod.POST);
        request.add("filename", new FileBinary(newFile));

        SimpleResponseListener<String> listener = new SimpleResponseListener<String>() {
            @Override public void onStart(int what) {
                super.onStart(what);
                showPd();
            }

            @Override public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                APIM_uploadFile result = JsonUtil.jsonToObject(response.get(), APIM_uploadFile.class);
                if (result.getStatus() == 100) {
                    //重新设置用户信息
                    urlPath.add(result.getResults());
                    if (uploadIndex < images.size() - 1) {
                        uploadIndex++;
                        uploadImage(uploadIndex);
                    } else {
                        dismissPd();
                        imagestr = ListUtils.join(urlPath, ",");
                        trendsAdd();
                    }
                } else {
                    ToastUtil.showMessage(result.getMessage());
                }
            }

            @Override public void onFailed(int what, Response<String> response) {
                super.onFailed(what, response);
                dismissPd();
                ToastUtil.showMessage("上传失败");
            }

            @Override public void onFinish(int what) {
                super.onFinish(what);
            }
        };
        CallServer.getInstance().request(0, request, listener);
    }
}
