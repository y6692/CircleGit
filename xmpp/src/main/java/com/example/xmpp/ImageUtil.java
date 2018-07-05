package com.example.xmpp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Base64;


import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.nanchen.compresshelper.CompressHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@SuppressLint("NewApi")
public class ImageUtil
{
	public static byte[] getimage(String path) throws Exception
	{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();

		byte[] data = readinputStream(inStream);
		return data;
	}

	public static byte[] readinputStream(InputStream inputStream)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int lns = 0;
		try {
			while ((lns = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, lns);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}

	public static Bitmap getBitmapFromBase64String(String imageString)
	{
		if (imageString == null)
			return null;
		byte[] data = Base64.decode(imageString, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	

	public static String getBitmapString(String image)
	{
		Bitmap bitmap = BitmapFactory.decodeFile(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] data = baos.toByteArray();

		return Base64.encodeToString(data, Base64.DEFAULT);
	}
	

	public static String getBase64StringFromFile(String imageFile)
	{
		InputStream in = null;
		byte[] data = null;
		try
		{
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Base64.encodeToString(data, Base64.DEFAULT);
	}
	
	
	/**
	 * drawable -> Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
        
        Bitmap bitmap = Bitmap
                        .createBitmap(  
                                        drawable.getIntrinsicWidth(),  
                                        drawable.getIntrinsicHeight(),  
                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
	}
	
	/**
	 * resource - > Bitmap
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap resourceToBitmap(Context context , int resId){
		Resources res = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
		return bitmap;  
	}
	
	
	/**
	 * Bitmap   - > Bytes
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm){
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    return baos.toByteArray();  
	}
	
	/**
	 * Bytes  - > Bitmap
	 * @param b
	 * @return
	 */
	public static Bitmap Bytes2Bitmap(byte[] b){
        if(b.length!=0){  
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }  
        else {  
            return null;  
        }  
	}
	
	
	public static Bitmap b2Bitmap(byte[] b) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bais == null)
			return null;
		return FormatTools.getInstance().InputStream2Bitmap(bais);
	}
	
	public static Bitmap createImageThumbnail(String filePath, int maxSize){
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, maxSize);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }catch (Exception e) {
           // TODO: handle exception
       }
       return bitmap;
   }
	
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	

   public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
       int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
       int roundedSize;
       if (initialSize <= 8) {
           roundedSize = 1;
           while (roundedSize < initialSize) {
               roundedSize <<= 1;
           }
       } else {
           roundedSize = (initialSize + 7) / 8 * 8;
       }
       return roundedSize;
   }

   private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
       double w = options.outWidth;
       double h = options.outHeight;
       int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
       int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
       if (upperBound < lowerBound) {
           // return the larger one when there is no overlapping zone.
           return lowerBound;
       }
       if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
           return 1;
       } else if (minSideLength == -1) {
           return lowerBound;
       } else {
           return upperBound;
       }
   }

	public static String compressAndBase64(Context ctx, String path) {
		File newFile = CompressHelper.getDefault(ctx).compressToFile(new File(path));

		String imageFile = newFile.getPath();
		InputStream in = null;
		byte[] data = null;
		try
		{
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Base64.encodeToString(data, Base64.DEFAULT);
	}



	public static String getReadableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static void browserPics(String picsStr, Context mContext, int index){
		String[] pics = picsStr.split(",");
		ArrayList<ImageItem> list = new ArrayList<>();
		for (String s : pics) {
			ImageItem imageItem = new ImageItem();
			imageItem.path = s;
			list.add(imageItem);
		}
		Intent intentPreview = new Intent(mContext, ImageBrowserActivity.class);
		intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, list);
		intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, index);
		intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
		mContext.startActivity(intentPreview);
	}

	public static Map createVideoThumbnail(String filePath, int kind) {
		Bitmap bitmap = null;
		String duration = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try
		{
			if (filePath.startsWith("http://")
					|| filePath.startsWith("https://")
					|| filePath.startsWith("widevine://"))
			{
				retriever.setDataSource(filePath, new Hashtable<String, String>());
			}
			else
			{
				retriever.setDataSource(filePath);
			}
			bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //retriever.getFrameAtTime(-1);
			duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		}
		catch (IllegalArgumentException ex)
		{
			// Assume this is a corrupt video file
			ex.printStackTrace();
		}
		catch (RuntimeException ex)
		{
			// Assume this is a corrupt video file.
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				retriever.release();
			}
			catch (RuntimeException ex)
			{
				// Ignore failures while cleaning up.
				ex.printStackTrace();
			}
		}

		if (bitmap == null)
		{
			return null;
		}

		if (kind == MediaStore.Images.Thumbnails.MINI_KIND)
		{//压缩图片 开始处
			// Scale down the bitmap if it's too large.
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int max = Math.max(width, height);
			if (max > 512)
			{
				float scale = 512f / max;
				int w = Math.round(scale * width);
				int h = Math.round(scale * height);
				bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
			}//压缩图片 结束处
		}
		else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND)
		{
			bitmap = ThumbnailUtils.extractThumbnail(bitmap,96,96,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		Map map = new HashMap();
		map.put("bitmap", getRoundedCornerBitmap(compressImage(bitmap)));
		map.put("duration", Integer.parseInt(duration)/1000);
		return map;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 13;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 质量压缩方法
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

}