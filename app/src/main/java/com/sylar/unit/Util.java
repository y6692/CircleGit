package com.sylar.unit;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.RecorderParameters;
import com.sylar.ucmlmobile.SavedFrames;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateFileCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;


public class Util {
	public static String brand = android.os.Build.BRAND;//手机品牌
	private static Util util;
	public static int flag = 0;
	private double r = 6371.004;
	public static ContentValues videoContentValues = null;

	public static String getRecordingTimeFromMillis(long millis) {
		String strRecordingTime = null;
		int seconds = (int) (millis / 1000);
		int minutes = seconds / 60;
		int hours = minutes / 60;

		if (hours >= 0 && hours < 10)
			strRecordingTime = "0" + hours + ":";
		else
			strRecordingTime = hours + ":";

		if (hours > 0)
			minutes = minutes % 60;

		if (minutes >= 0 && minutes < 10)
			strRecordingTime += "0" + minutes + ":";
		else
			strRecordingTime += minutes + ":";

		seconds = seconds % 60;

		if (seconds >= 0 && seconds < 10)
			strRecordingTime += "0" + seconds;
		else
			strRecordingTime += seconds;

		return strRecordingTime;

	}

	public static int determineDisplayOrientation(Activity activity,
												  int defaultCameraId) {
		int displayOrientation = 0;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(defaultCameraId, cameraInfo);

			int degrees = getRotationAngle(activity);

			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				displayOrientation = (cameraInfo.orientation + degrees) % 360;
				displayOrientation = (360 - displayOrientation) % 360;
			} else {
				displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
			}
		}
		return displayOrientation;
	}

	public static int getRotationAngle(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;

		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;

			case Surface.ROTATION_90:
				degrees = 90;
				break;

			case Surface.ROTATION_180:
				degrees = 180;
				break;

			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		return degrees;
	}

	public static int getRotationAngle(int rotation) {
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;

			case Surface.ROTATION_90:
				degrees = 90;
				break;

			case Surface.ROTATION_180:
				degrees = 180;
				break;

			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		return degrees;
	}

	public static String createImagePath(Context context) {
		long dateTaken = System.currentTimeMillis();
		String title = Constants.FILE_START_NAME + dateTaken;
		String filename = title + Constants.IMAGE_EXTENSION;

		String dirPath = Environment.getExternalStorageDirectory()
				+ "/Android/data/" + context.getPackageName() + "/video";
		File file = new File(dirPath);
		if (!file.exists() || !file.isDirectory())
			file.mkdirs();
		String filePath = dirPath + "/" + filename;
		return filePath;
	}

	public static String createFinalPath(Context context) {
		long dateTaken = System.currentTimeMillis();
		String title = Constants.FILE_START_NAME + dateTaken;
		String filename = title + Constants.VIDEO_EXTENSION;
		String filePath = genrateFilePath(context, String.valueOf(dateTaken),
				true, null);

		ContentValues values = new ContentValues(7);
		values.put(MediaStore.Video.Media.TITLE, title);
		values.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
		values.put(MediaStore.Video.Media.DATE_TAKEN, dateTaken);
		values.put(MediaStore.Video.Media.MIME_TYPE, "video/3gpp");
		values.put(MediaStore.Video.Media.DATA, filePath);
		videoContentValues = values;

		return filePath;
	}

	public static void deleteTempVideo(Context context) {
		final String dirPath = Environment.getExternalStorageDirectory()
				+ "/Android/data/" + context.getPackageName() + "/video";
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(dirPath);
				if (file != null && file.isDirectory()) {
					for (File file2 : file.listFiles()) {
						file2.delete();
					}
				}
			}
		}).start();
	}

	private static String genrateFilePath(Context context, String uniqueId,
										  boolean isFinalPath, File tempFolderPath) {
		String fileName = Constants.FILE_START_NAME + uniqueId
				+ Constants.VIDEO_EXTENSION;
		String dirPath = Environment.getExternalStorageDirectory()
				+ "/Android/data/" + context.getPackageName() + "/video";
		if (isFinalPath) {
			File file = new File(dirPath);
			if (!file.exists() || !file.isDirectory())
				file.mkdirs();
		} else
			dirPath = tempFolderPath.getAbsolutePath();
		String filePath = dirPath + "/" + fileName;
		return filePath;
	}

	public static String createTempPath(Context context, File tempFolderPath) {
		long dateTaken = System.currentTimeMillis();
		String filePath = genrateFilePath(context, String.valueOf(dateTaken),
				false, tempFolderPath);
		return filePath;
	}

	public static File getTempFolderPath() {
		File tempFolder = new File(Constants.TEMP_FOLDER_PATH + "_"
				+ System.currentTimeMillis());
		return tempFolder;
	}

	public static List<Camera.Size> getResolutionList(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

		return previewSizes;
	}

	public static RecorderParameters getRecorderParameter(int currentResolution) {
		RecorderParameters parameters = new RecorderParameters();
		if (currentResolution == Constants.RESOLUTION_HIGH_VALUE) {
			parameters.setAudioBitrate(128000);
			parameters.setVideoQuality(0);
		} else if (currentResolution == Constants.RESOLUTION_MEDIUM_VALUE) {
			parameters.setAudioBitrate(128000);
			parameters.setVideoQuality(5);
		} else if (currentResolution == Constants.RESOLUTION_LOW_VALUE) {
			parameters.setAudioBitrate(96000);
			parameters.setVideoQuality(20);
		}
		return parameters;
	}

	public static int calculateMargin(int previewWidth, int screenWidth) {
		int margin = 0;
		if (previewWidth <= Constants.RESOLUTION_LOW) {
			margin = (int) (screenWidth * 0.12);
		} else if (previewWidth > Constants.RESOLUTION_LOW
				&& previewWidth <= Constants.RESOLUTION_MEDIUM) {
			margin = (int) (screenWidth * 0.08);
		} else if (previewWidth > Constants.RESOLUTION_MEDIUM
				&& previewWidth <= Constants.RESOLUTION_HIGH) {
			margin = (int) (screenWidth * 0.08);
		}
		return margin;

	}

	public static int setSelectedResolution(int previewHeight) {
		int selectedResolution = 0;
		if (previewHeight <= Constants.RESOLUTION_LOW) {
			selectedResolution = 0;
		} else if (previewHeight > Constants.RESOLUTION_LOW
				&& previewHeight <= Constants.RESOLUTION_MEDIUM) {
			selectedResolution = 1;
		} else if (previewHeight > Constants.RESOLUTION_MEDIUM
				&& previewHeight <= Constants.RESOLUTION_HIGH) {
			selectedResolution = 2;
		}
		return selectedResolution;

	}

	public static class ResolutionComparator implements Comparator<Camera.Size> {
		@Override
		public int compare(Camera.Size size1, Camera.Size size2) {
			if (size1.height != size2.height)
				return size1.height - size2.height;
			else
				return size1.width - size2.width;
		}
	}

	public static void concatenateMultipleFiles(String inpath, String outpath) {
		File Folder = new File(inpath);
		File files[];
		files = Folder.listFiles();

		if (files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				Reader in = null;
				Writer out = null;
				try {
					in = new FileReader(files[i]);
					out = new FileWriter(outpath, true);
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getEncodingLibraryPath(Context paramContext) {
		return paramContext.getApplicationInfo().nativeLibraryDir
				+ "/libencoding.so";
	}

	private static HashMap<String, String> getMetaData() {
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		localHashMap.put("creation_time", new SimpleDateFormat(
				"yyyy_MM_dd_HH_mm_ss_SSSZ").format(new Date()));
		return localHashMap;
	}

	public static int getTimeStampInNsFromSampleCounted(int paramInt) {
		return (int) (paramInt / 0.0441D);
	}

	public static void saveReceivedFrame(SavedFrames frame) {
		File cachePath = new File(frame.getCachePath());
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(cachePath));
			if (bos != null) {
				bos.write(frame.getFrameBytesData());
				bos.flush();
				bos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			cachePath = null;
		} catch (IOException e) {
			e.printStackTrace();
			cachePath = null;
		}
	}

	public static Toast showToast(Context context, String textMessage,
								  int timeDuration) {
		if (null == context) {
			return null;
		}
		textMessage = (null == textMessage ? "Oops! " : textMessage.trim());
		Toast t = Toast.makeText(context, textMessage, timeDuration);
		t.show();
		return t;
	}


	public opencv_core.IplImage getFrame(String filePath) {
		opencv_highgui.CvCapture capture = cvCreateFileCapture(filePath);
		opencv_core.IplImage image = cvQueryFrame(capture);
		return image;
	}

	private Util() {

	}

	public static Util getInstance() {
		if (util == null) {
			util = new Util();
		}
		return util;
	}

	/**
	 * 判断是否有sdcard
	 * 
	 * @return
	 */
	public boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public String getExtPath() {
		String path = "";



		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}

		Log.e("path", hasSDCard()+"==="+path);

		return path;
	}

	/**
	 * 得到/data/data/com.d3studio.together目录
	 * 
	 * @param mActivity
	 * @return
	 */
	public String getPackagePath(Context mActivity) {
		return mActivity.getFilesDir().toString();
	}

	/**
	 * 根据url得到图片
	 * 
	 * @param url
	 * @return
	 */
	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}

	/**
	 * 根据经纬度获取距离
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public String getDistance(double lat1, double lon1, double lat2, double lon2) {
		String string;

		//上传位置失败时===  0.0没办法，百度地图的蛋疼设定，唯有丢失非洲用户啦
		if (lat2 == 0 && lon2 == 0) {
			string = "暂无数据";
		} else {
			double distance = 0;
			distance = 2
					* r
					* Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
							2)
							+ Math.cos(lat1)
							* Math.cos(lat2)
							* Math.pow(Math.sin((lon1 - lon2) / 2), 2)));
			// 保留两位小数
			DecimalFormat df = new DecimalFormat("##.##");
			string = df.format(distance)+"km";
		}
		return string;
	}

	/**
	 * 判断手机号码的正确性
	 * 
	 * @param mobiles
	 * @return
	 */
	public boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 判断邮箱的正确性
	 * 
	 * @param email
	 * @return
	 */
	public boolean isEmail(String email) {
		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 不能全是相同的数字或者字母
	 */
	public boolean equalStr(String numOrStr) {
		boolean flag = true;
		char str = numOrStr.charAt(0);
		for (int i = 0; i < numOrStr.length(); i++) {
			if (str != numOrStr.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 不能是连续的数字--递增
	public boolean isOrderNumeric(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	// 不能是连续的数字--递减
	public boolean isOrderNumeric_(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;

	}
	
	
	// 遍历布局，并禁用所有子控件 -- 不禁止textview
	public static void disableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(false);
					spinner.setEnabled(false);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(false);
					((ListView) v).setEnabled(false);

				} else {
					disableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(false);
				((EditText) v).setClickable(false);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(false);
			}
		}
	}

	// 遍历布局，并禁用所有子控件  -- 不禁止textview
	public static void ableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(true);
					spinner.setEnabled(true);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(true);
					((ListView) v).setEnabled(true);

				} else {
					ableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(true);
				((EditText) v).setClickable(true);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(true);
			}
		}
	}
	

	 // 隐藏手机键盘  
	public static void hideIM(Context context, View edt) {
     try {  
         InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
         IBinder windowToken = edt.getWindowToken();
         if (windowToken != null) {  
             im.hideSoftInputFromWindow(windowToken, 0);
         }  
     } catch (Exception e) {
     }  
 }
}