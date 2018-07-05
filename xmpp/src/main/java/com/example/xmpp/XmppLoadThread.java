package com.example.xmpp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;


public abstract class XmppLoadThread {

	boolean isHint;
	public static ProgressDialog mdialog;
	private Context c;
	OnTaskListener mListener;
//	private ExecutorService FULL_TASK_EXECUTOR;

	@SuppressLint("NewApi")
	public XmppLoadThread(Context _mcontext, OnTaskListener listener) {
		isHint = true;
		c = _mcontext;
		mListener = listener;

//		FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
		new AsyncTask<Void, Integer, Object>() {

			@Override
			protected Object doInBackground(Void... arg0) {
				return load();
			}

			@Override
			protected void onPostExecute(Object result) {
				if (isHint && (mdialog == null || !mdialog.isShowing())) {
					return;
				} else {
					try {
						result(result);

//                      if (ContextUtil.f==0 && InviteFriendActivity.f==0 && AddRoomActivity.f==0  && RoomMemActivity.f==0 && FriendActivity.f==0 && isHint && (mdialog != null && mdialog.isShowing())) {
//                                mdialog.dismiss();
//						}

						mdialog.dismiss();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			protected void onPreExecute() {
				if (isHint) {
					try {

						//if(LoginActivity.f==1 || AddRoomActivity.f==1 || InviteFriendActivity.f==1 || RoomInfoActivity.f==1 || FriendActivity.f==1 || ContextUtil.f>0){
							mdialog =  ProgressDialog.show(c, c.getResources().getString(R.string.dialog_title), c.getResources().getString(R.string.dialog_load_content));
							mdialog.setCancelable(true);

							mdialog.setContentView(R.layout.dialog_loadding);

							mdialog.setIndeterminateDrawable(c.getResources().getDrawable(R.drawable.progress_dialog_style));
						//}

						/*
						if(AddRoomActivity.f==1){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("创建中，请稍后……");
						}

						if(InviteFriendActivity.f==1){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("邀请中，请稍后……");
						}

						if(RoomInfoActivity.f==1){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("删除中，请稍后……");
						}

						if(FriendActivity.f==1){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("踢出中，请稍后……");
						}

						if(ContextUtil.f==1){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("加入中，请稍后……");
						}

						if(ContextUtil.f==2){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("被踢出中，请稍后……");
						}

						if(ContextUtil.f==3){
							TextView pt=(TextView) mdialog.findViewById(R.id.ProgressBarText);
							pt.setText("圈子销毁中，请稍后……");
						}
						*/


					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.execute();
	}

	protected abstract Object load();

	protected  void result(Object object){
		boolean isSuccess = (Boolean) object;
		if (isSuccess) {
			mListener.onSuccess();
		} else {
			mListener.onFailed();
		}
	}

	public interface OnTaskListener{
		void onSuccess();
		void onFailed();
	}

}
