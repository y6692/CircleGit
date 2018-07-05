package com.sylar.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.administrator.circlegit.R;

import cn.trinea.android.common.util.StringUtils;


public final class LibraryCommonDialog {

  private LibraryCommonDialog() {
  }

  public interface OnYesCallback {
    void onYesClick();
  }

  public interface OnNoCallback {
    void onNoClick();
  }

  /**
   * 确认弹出框
   * @param mContext   上下文
   * @param title     标题，没有传空就会不显示
   * @param msg       内容
   * @param leftTxt       左边按钮文字
   * @param rightTxt      右边按钮文字
   * @param yesCallback   回调
   * @param noCallback  回调
   * @return
   */
  public static Dialog showYesOrNoDialog(Context mContext, String title, String msg, String leftTxt,
      String rightTxt, final OnYesCallback yesCallback, final OnNoCallback noCallback) {
    final Dialog dlg = new Dialog(mContext, R.style.MMTheme_DataSheet);
    dlg.getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    LayoutInflater inflater =
        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout layout =
        (LinearLayout) inflater.inflate(R.layout.library_alert_dialog_yes_no_layout, null);
    final int cFullFillWidth = 10000;
    layout.setMinimumWidth(cFullFillWidth);

    TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
    TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
    TextView tvYes = (TextView) layout.findViewById(R.id.tv_yes);
    TextView tvNo = (TextView) layout.findViewById(R.id.tv_no);
    tvYes.getPaint().setFakeBoldText(true);

    if (!StringUtils.isBlank(title)) {
      tvTitle.setVisibility(View.VISIBLE);
      tvTitle.setText(title);
    }else {
      tvTitle.setVisibility(View.GONE);
    }
    if (!StringUtils.isBlank(msg)) tvMsg.setText(msg);
    if (!StringUtils.isBlank(leftTxt)) tvNo.setText(leftTxt);
    if (!StringUtils.isBlank(rightTxt)) tvYes.setText(rightTxt);

    tvYes.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        dlg.dismiss();
        if (yesCallback != null) yesCallback.onYesClick();
      }
    });
    tvNo.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View arg0) {
        dlg.dismiss();
        if (noCallback != null) noCallback.onNoClick();
      }
    });
    dlg.setCanceledOnTouchOutside(false);
    dlg.setContentView(layout);
    dlg.show();

    return dlg;
  }


  /**
   * 选择照片
   */
  public interface AddPhotoCallback {
    void onTakePic();

    void onChoosePoc();
  }

  public static Dialog addPhotoDialog(Context context, final AddPhotoCallback callback) {
    final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
    dlg.getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout layout =
        (LinearLayout) inflater.inflate(R.layout.library_alert_dialog_addphoto_layout, null);
    final int cFullFillWidth = 10000;
    layout.setMinimumWidth(cFullFillWidth);
    TextView tvTakePhoto = (TextView) layout.findViewById(R.id.tv_take_photo);
    TextView tvGetPhoto = (TextView) layout.findViewById(R.id.tv_get_photo);
    TextView tvCancle = (TextView) layout.findViewById(R.id.tv_cancle);
    tvGetPhoto.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        callback.onChoosePoc();
        dlg.dismiss();
      }
    });
    tvTakePhoto.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        callback.onTakePic();
        dlg.dismiss();
      }
    });
    tvCancle.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        dlg.dismiss();
      }
    });
    // set a large value put it in bottom
    Window w = dlg.getWindow();
    WindowManager.LayoutParams lp = w.getAttributes();
    lp.x = 0;
    final int cMakeBottom = -1000;
    lp.y = cMakeBottom;
    lp.gravity = Gravity.BOTTOM;
    dlg.onWindowAttributesChanged(lp);
    dlg.setCanceledOnTouchOutside(true);
    dlg.setContentView(layout);
    dlg.show();

    return dlg;
  }


  public interface VideoMenuCallback {
    void onSaveVideo();

//    void onChoosePoc();
  }

  public interface AddDongtaiMenuCallback {
    void onVideo();

    void onPic();
  }

  /**
   * 显示加动态菜单
   */
  public static Dialog addDongtaiMenuDialog(Context context, final AddDongtaiMenuCallback callback) {
    final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
    dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.library_alert_dialog_adddongtaimenu_layout, null);
    final int cFullFillWidth = 10000;
    layout.setMinimumWidth(cFullFillWidth);
    TextView tvVideo = (TextView) layout.findViewById(R.id.tv_take_photo);
    TextView tvPic = (TextView) layout.findViewById(R.id.tv_get_photo);
    tvVideo.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        callback.onVideo();
        dlg.dismiss();
      }
    });
    tvPic.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        callback.onPic();
        dlg.dismiss();
      }
    });
    // set a large value put it in bottom
    Window w = dlg.getWindow();
    WindowManager.LayoutParams lp = w.getAttributes();
    lp.x = 0;
    final int cMakeBottom = -1000;
    lp.y = cMakeBottom;
    lp.gravity = Gravity.BOTTOM;
    dlg.onWindowAttributesChanged(lp);
    dlg.setCanceledOnTouchOutside(true);
    dlg.setContentView(layout);
    dlg.show();

    return dlg;
  }

  /**
   * 显示视频菜单
   */
  public static Dialog videoMenuDialog(Context context, final VideoMenuCallback callback) {
    final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
    dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.library_alert_dialog_videomenu_layout, null);
    final int cFullFillWidth = 10000;
    layout.setMinimumWidth(cFullFillWidth);
    TextView tvSaveVideo = (TextView) layout.findViewById(R.id.tv_take_photo);
//    TextView tvGetPhoto = (TextView) layout.findViewById(R.id.tv_get_photo);
    tvSaveVideo.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View arg0) {
        callback.onSaveVideo();
        dlg.dismiss();
      }
    });
//    tvTakePhoto.setOnClickListener(new OnClickListener() {
//
//      @Override public void onClick(View arg0) {
//        callback.onTakePic();
//        dlg.dismiss();
//      }
//    });
    // set a large value put it in bottom
    Window w = dlg.getWindow();
    WindowManager.LayoutParams lp = w.getAttributes();
    lp.x = 0;
    final int cMakeBottom = -1000;
    lp.y = cMakeBottom;
    lp.gravity = Gravity.BOTTOM;
    dlg.onWindowAttributesChanged(lp);
    dlg.setCanceledOnTouchOutside(true);
    dlg.setContentView(layout);
    dlg.show();

    return dlg;
  }

}
