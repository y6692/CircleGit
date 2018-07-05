package com.sylar.unit.imagePicker;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lzy.imagepicker.ui.ImagePreviewDelActivity;

public class ImageBrowserActivity extends ImagePreviewDelActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ImageView mBtnDel = (ImageView) findViewById(com.lzy.imagepicker.R.id.btn_del);
    mBtnDel.setVisibility(View.GONE);
  }
}
