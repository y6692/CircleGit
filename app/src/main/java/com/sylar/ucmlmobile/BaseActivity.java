package com.sylar.ucmlmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;

import com.sylar.view.BaseView;
import com.sylar.view.EventListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity implements IHandler{
	private InternalReceiver internalReceiver;
	protected BaseHandler<BaseActivity> handler = new BaseHandler<BaseActivity>(this);
	protected AppManager manager = AppManager.getAppManager();


	@Override
	protected void onCreate(Bundle arg0) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		manager.addActivity(this);
	}

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initInjectedView(this);
	}

	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initInjectedView(this);
	}

	public void setContentView(View view) {
		super.setContentView(view);
		initInjectedView(this);
	}

//	protected abstract void init();

	private void initInjectedView(Activity activity) {
		initInjectedView(activity, activity.getWindow().getDecorView());
	}

	private void initInjectedView(Object activity, View sourceView) {
		Field[] fields = activity.getClass().getDeclaredFields(); // 获取字段
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				try {
					field.setAccessible(true); // 设为可访问

					if (field.get(activity) != null)
						continue;

					BaseView baseView = field.getAnnotation(BaseView.class);
					if (baseView != null) {

						int viewId = baseView.id();
						if (viewId == 0){
							viewId = getResources().getIdentifier(field.getName(), "id", getPackageName());
						}

						if (viewId == 0){
							Log.e("BaseActivity", "field " + field.getName() + "not found");
						}


						// 关键,注解初始化，相当于 backBtn = (TextView)
						// findViewById(R.id.back_btn);
						field.set(activity, sourceView.findViewById(viewId));
						// 事件
						setListener(activity, field, baseView.click(), Method.Click);
						setListener(activity, field, baseView.longClick(), Method.LongClick);
						setListener(activity, field, baseView.itemClick(), Method.ItemClick);
						setListener(activity, field, baseView.itemLongClick(), Method.itemLongClick);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setListener(Object activity, Field field, String methodName, Method method) throws Exception {
		if (methodName == null || methodName.trim().length() == 0)
			return;

		Object obj = field.get(activity);

		switch (method) {
		case Click:
			if (obj instanceof View) {
				((View) obj).setOnClickListener(new EventListener(activity).click(methodName));
			}
			break;
		case ItemClick:
			if (obj instanceof AbsListView) {
				((AbsListView) obj).setOnItemClickListener(new EventListener(activity).itemClick(methodName));
			}
			break;
		case LongClick:
			if (obj instanceof View) {
				((View) obj).setOnLongClickListener(new EventListener(activity).longClick(methodName));
			}
			break;
		case itemLongClick:
			if (obj instanceof AbsListView) {
				((AbsListView) obj).setOnItemLongClickListener(new EventListener(activity).itemLongClick(methodName));
			}
			break;

		case focusChange:
			if (obj instanceof View) {
				((View) obj).setOnFocusChangeListener(new EventListener(activity).focusChange(methodName));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onMessage(Message msg) {

	}

	public enum Method {
		Click, LongClick, ItemClick, itemLongClick, focusChange
	}

	protected final void registerReceiver(String[] actionArray) {
		if (actionArray == null) {
			return;
		}
		IntentFilter intentfilter = new IntentFilter();
		for (String action : actionArray) {
			intentfilter.addAction(action);
		}
		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		registerReceiver(internalReceiver, intentfilter);
	}

	// Internal calss.
	private class InternalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null || intent.getAction() == null) {
				return;
			}
			handleReceiver(context, intent);
		}
	}

	/**
	 * 如果子界面需要拦截处理注册的广播 需要实现该方法
	 *
	 * @param context
	 * @param intent
	 */
	protected void handleReceiver(Context context, Intent intent) {
		// 广播处理
		if (intent == null) {
			return;
		}
	}

	@Override
	protected void onDestroy() {
		try {
			if (internalReceiver != null) {
				unregisterReceiver(internalReceiver);
			}
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	/**
	 * 触发消息
	 *
	 * @param what
	 */
	protected void postMessage(int what) {
		handler.obtainMessage(what).sendToTarget();
	}
}
