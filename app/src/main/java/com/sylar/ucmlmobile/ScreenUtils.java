package com.sylar.ucmlmobile;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {
	private static DisplayMetrics mMetrics = new DisplayMetrics();
	private static ScreenUtils instance;
	private Context context;

	public static ScreenUtils getInstance(Context context) {
		if (instance == null)
			instance = new ScreenUtils(context);
		return instance;
	}

	private ScreenUtils(Context _context) {
		context = _context;
		mMetrics = context.getResources().getDisplayMetrics();
	}

	public int getScreenHeight() {
		return mMetrics.heightPixels;
	}	

	public int getScreenWidth() {
		return mMetrics.widthPixels;
	}

	public int dip2px(float dpValue) {
		final float scale = mMetrics.density;
		return (int) (dpValue * scale + 0.5f);
	}

	public int px2dip(float pxValue) {
		final float scale = mMetrics.density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static float dpToPixel(float dp,Context context) {
		return dp * (getDisplayMetrics(context).densityDpi / 160F);
	    }

	 public static DisplayMetrics getDisplayMetrics(Context context) {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			((WindowManager) context.getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
				displaymetrics);
			return displaymetrics;
		    }
}
