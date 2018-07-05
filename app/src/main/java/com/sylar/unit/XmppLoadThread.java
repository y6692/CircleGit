package com.sylar.unit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.flyco.tablayout.CommonTabLayout;
import com.sylar.activity.HomeActivity;
import com.sylar.ucmlmobile.AddRoomActivity;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.FriendActivity;
import com.sylar.ucmlmobile.InviteFriendActivity;
import com.sylar.ucmlmobile.RoomInfoActivity;
import com.sylar.ucmlmobile.RoomMemActivity;

import org.jivesoftware.smackx.muc.RoomInfo;


public abstract class XmppLoadThread {

	boolean isHint;
	public static ProgressDialog mdialog;
	private Context c;
//	private ExecutorService FULL_TASK_EXECUTOR;

	@SuppressLint("NewApi")
	public XmppLoadThread(Context _mcontext) {
		isHint = true;
		c = _mcontext;
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
						//if (HomeActivity.f==0 && ContextUtil.f==0 && InviteFriendActivity.f==0 && AddRoomActivity.f==0  && RoomMemActivity.f==0 && FriendActivity.f==0 && isHint && (mdialog != null && mdialog.isShowing())) {
                        if (ContextUtil.f==0 && InviteFriendActivity.f==0 && AddRoomActivity.f==0  && RoomMemActivity.f==0 && FriendActivity.f==0 && isHint && (mdialog != null && mdialog.isShowing())) {

                                mdialog.dismiss();
						}
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


						//ProgressBarText
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


					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.execute();
	}

	protected abstract Object load();

	protected abstract void result(Object object);

}
