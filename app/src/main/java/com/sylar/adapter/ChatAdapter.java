package com.sylar.adapter;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.flyco.roundview.RoundTextView;
import com.sylar.dao.MsgDbHelper;
import com.sylar.fragment.VideoFragment;
import com.sylar.model.ChatItem;
import com.sylar.model.Emoji;
import com.sylar.model.User;
import com.sylar.ucmlmobile.Constants;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.MyMapActivity;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.PlayMovieActivity;
import com.sylar.ucmlmobile.ShowMapActivity;
import com.sylar.ucmlmobile.ShowPicActivitiy;
import com.sylar.ucmlmobile.VideoActivity;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.DateUtil;
import com.sylar.unit.FileUtil;
import com.sylar.unit.ImageManager;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.ImgConfig;
import com.sylar.unit.ImgHandler;
import com.sylar.unit.StringUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.Utils;
import com.sylar.view.expression.ExpressionUtil;
import com.sylar.view.gifView.GifView;
import com.sylar.view.gifView.GifView.GifImageType;

import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class ChatAdapter extends ArrayAdapter<ChatItem> {
	private Context cxt;
	private static int[] resTo = { R.drawable.voiceto0, R.drawable.voiceto1,
		R.drawable.voiceto2, R.drawable.voiceto3 };
	private static int[] resFrom = { R.drawable.voicefrom0, R.drawable.voicefrom1,
		R.drawable.voicefrom2, R.drawable.voicefrom3 };
	private String username = null;
	NewChatActivity activity;
	protected ImageManager mImageManager;
    private HashMap<String, String> map=new HashMap();
	
	public static interface MsgType {
		int MSG_OUT = 0;
		int MSG_IN = 1;
	}

	public ChatAdapter(NewChatActivity activity, Context context, String username) {
		super(context, 0);
		this.activity = activity;
		this.cxt = context;
		this.username = username;

		mImageManager = new ImageManager(context);
	}
	
	@Override
	public int getItemViewType(int position) {
		ChatItem nowMsg = (ChatItem)getItem(position);
		if (nowMsg.inOrOut == 1) {
			return MsgType.MSG_OUT;
		} else {
			return MsgType.MSG_IN;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		final ChatItem item = (ChatItem)getItem(position);
		final ViewHolder viewHolder;	
		int msgType = getItemViewType(position);

		if (convertView == null) {
			if (msgType == MsgType.MSG_OUT) {
				convertView = LayoutInflater.from(cxt).inflate(R.layout.row_chat_mine, null, false);
			} else {
				convertView = LayoutInflater.from(cxt).inflate(R.layout.row_chat,null, false);
			}

			viewHolder = new ViewHolder();
			viewHolder.timeView = (TextView) convertView.findViewById(R.id.timeView);
			viewHolder.msgView = (TextView) convertView.findViewById(R.id.msgView);
			viewHolder.head = (ImageView) convertView.findViewById(R.id.headImg);
			viewHolder.img = (ImageView) convertView.findViewById(R.id.imgView);
			viewHolder.flMap = (FrameLayout) convertView.findViewById(R.id.fl_map);
			viewHolder.map = (ImageView) convertView.findViewById(R.id.iv_map);
			viewHolder.address= (RoundTextView) convertView.findViewById(R.id.tv_map);
			viewHolder.gifImg = (ImageView) convertView.findViewById(R.id.gifImgView);
			viewHolder.voice = (ImageView) convertView.findViewById(R.id.voiceView);
			viewHolder.soundDuration = (TextView)convertView.findViewById(R.id.soundView);
			viewHolder.flMovieView = (FrameLayout) convertView.findViewById(R.id.fl_movieView);
			viewHolder.movie = (ImageView) convertView.findViewById(R.id.iv_movieView);
			viewHolder.movieDuration = (TextView) convertView.findViewById(R.id.tv_duration);
			viewHolder.gif = (GifView) convertView.findViewById(R.id.gifView);
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
			viewHolder.textimg=(ImageView) convertView.findViewById(R.id.textimgView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.timeView.setVisibility(View.VISIBLE);
			viewHolder.msgView.setVisibility(View.VISIBLE);
			viewHolder.head.setVisibility(View.VISIBLE);
			viewHolder.img.setVisibility(View.GONE);
			viewHolder.flMap.setVisibility(View.GONE);
			viewHolder.gifImg.setVisibility(View.GONE);
			viewHolder.voice.setVisibility(View.GONE);
			viewHolder.flMovieView.setVisibility(View.GONE);
			viewHolder.soundDuration.setVisibility(View.GONE);
			viewHolder.gif.setVisibility(View.GONE);
			viewHolder.nameView.setVisibility(View.GONE);
			viewHolder.textimg.setVisibility(View.GONE);
		}
		viewHolder.msgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(cxt, VideoActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle = new Bundle();
				bundle.putString("videoPath", item.msg);
				bundle.putBoolean("useCache", false);
				intent.putExtras(bundle);
				cxt.startActivity(intent);
			}
        });

		//head
		if (item.inOrOut == 0 ) {   //接收
			if (item.chatType == ChatItem.CHAT) {
				Bitmap bmp = MessageConstants.findAllHeadById(item.username);
				if (bmp != null) {
					viewHolder.head.setImageBitmap(bmp);
				}
			}
			
//			viewHolder.head.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(cxt, MessageMain.class);
//					intent.putExtra("username", item.username);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					cxt.startActivity(intent);
//				}
//			});

            if(map.get(item.username)==null){
                VCard vcard=XmppConnection.getInstance().getUservcard(item.username);
                map.put(item.username, vcard.getField("Name"));
                item.username= vcard.getField("Name");
            }else{
                item.username= map.get(item.username);
            }

		} else{
			if (item.chatType == ChatItem.CHAT) {
				Bitmap bmp= MessageConstants.findAllHeadById(Constants.XMPP_USERNAME);
				if(bmp!=null){
					viewHolder.head.setImageBitmap(bmp);
				}
			}

			item.username= Constants.XMPP_NICKNAME;
		}

		//同一分钟内则不显示相同时间了
		ChatItem lastMsg = null;
		if(position!=0)
			lastMsg = (ChatItem)getItem(position-1);
		if(lastMsg!=null && lastMsg.sendDate.substring(0,12).equals(item.sendDate.substring(0,12))){
			viewHolder.timeView.setVisibility(View.GONE);
		}else if(item.isbit==0){
			viewHolder.timeView.setVisibility(View.GONE);
		}else{
			viewHolder.timeView.setText(DateUtil.getRecentTimeMM_dd(item.sendDate));
		}

		String json = item.msg;
		JSONObject obj = null;
		try {
			obj = new JSONObject(json);
		}catch (Exception e){
			e.printStackTrace();
		}

		try {
			if("0".equals(item.bodyType)){
				String msg = obj.getString("msg");
				if (msg != null && msg.contains("[/g0")) { // isGif
					playGif(viewHolder.gif, viewHolder.msgView, viewHolder.gifImg, msg, position);
				}
				else if(msg != null && msg.contains("[/f0")){ // 适配表情
					viewHolder.msgView.setText(ExpressionUtil.getText(cxt,StringUtil.Unicode2GBK(msg)));
				}
				// 2017.6.2 djy add start
				else if(msg != null && msg.contains("[")){ // 适配中文表情
					viewHolder.msgView.setText(ExpressionUtil.getText(cxt, StringUtil.Unicode2GBK(msg)));

					//			Emoji emoji = faceAdapters.get(current).getItem(position);
					//			Bitmap bitmap = null;
					//			bitmap = BitmapFactory.decodeResource(cxt.getResources(), R.mipmap.expression_1);
					//
					//			Matrix matrix = new Matrix();
					//			matrix.postScale(0.6f, 0.6f); //长和宽放大缩小的比例
					//			Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
					//
					//			ImageSpan imageSpan = new ImageSpan(getContext(), resizeBmp);
					//			SpannableString spannableString = new SpannableString("微笑");
					//			spannableString.setSpan(imageSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					//			// 编辑框设置数据
					//			viewHolder.msgView.setText("删掉");
					//			viewHolder.msgView.append(spannableString);

				}else{
					viewHolder.msgView.setText(msg);
				}




			} else if("2".equals(item.bodyType)){     //图片
				String path = item.msg;
				viewHolder.msgView.setVisibility(View.GONE);
				showImg(viewHolder.img, path);
			} else if("3".equals(item.bodyType)){		//语音
				String path = item.msg;
				viewHolder.msgView.setVisibility(View.GONE);
				playSound(path, viewHolder.soundDuration, viewHolder.voice,item.inOrOut==1);
			} else if("5".equals(obj.getString("bodyType"))){		//小视频
				String path = obj.getString("msg");
				viewHolder.msgView.setVisibility(View.GONE);
				viewHolder.flMovieView.setVisibility(View.VISIBLE);
				playMovie(path, viewHolder.movieDuration, viewHolder.movie,item.inOrOut==1);
			} else if("6".equals(obj.getString("bodyType"))){		//定位
				viewHolder.msgView.setVisibility(View.GONE);
				viewHolder.flMap.setVisibility(View.VISIBLE);
				viewHolder.address.setText(obj.getString("name"));
				showPosition(viewHolder.map, obj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		if (item.chatType == ChatItem.GROUP_CHAT) {
			viewHolder.nameView.setVisibility(View.VISIBLE);
			viewHolder.nameView.setText(item.username);
		}else {
			viewHolder.nameView.setVisibility(View.GONE);
		}
		
		//内容复制
		viewHolder.msgView.setOnLongClickListener(new OnLongClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View v) {
				TextView msgView = (TextView)v;
				ClipboardManager cm = (ClipboardManager) cxt.getSystemService(Context.CLIPBOARD_SERVICE);
				//将文本数据复制到剪贴板
				cm.setText(msgView.getText());
				Vibrator vib = (Vibrator) cxt.getSystemService(Context.VIBRATOR_SERVICE);  //震动提醒
			    vib.vibrate(100); 
				Tool.initToast(cxt,"复制成功");
				return false;
			}
		});
		return convertView;
	}

	private void showImg(ImageView img , final String path){
		img.setVisibility(View.VISIBLE);
		img.setImageBitmap(ImageUtil.createImageThumbnail(path,200*200));
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("picPath", path);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(cxt, ShowPicActivitiy.class);
				cxt.startActivity(intent);
			}
		});

		img.setOnLongClickListener(new OnLongClickListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View v) {
//				FileUtil.changeFile(path, CircleConstants.IMG_PATH+"/"+FileUtil.getFileName(path));
				Tool.initToast(cxt,"图片已保存至本地"+path);
				ContextUtil.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path)));
				return false;
			}
		});
	}

	private void showPosition(ImageView img, final JSONObject obj){
		try {
			final String path = obj.getString("imageUrl");
			img.setVisibility(View.VISIBLE);
			mImageManager.loadRoundImage(path, img, 13);

//			img.setImageBitmap(ImageUtil.createImageThumbnail(path,200*200));
//			Bitmap bm = getRoundedCornerBitmap(compressImage(Utils.createVideoThumbnail((String)map.get("msgUrl"))));
//			BitmapDrawable drawable = new BitmapDrawable(bm);
//			img.setBackgroundDrawable(drawable);

			ViewGroup.LayoutParams para;
			para = img.getLayoutParams();
			para.width  = 600 * ContextUtil.width/1080;
			para.height = para.width/2;
			img.setLayoutParams(para);

			img.setOnLongClickListener(new OnLongClickListener() {
				@SuppressLint("NewApi")
				@Override
				public boolean onLongClick(View v) {
//					FileUtil.changeFile(path, CircleConstants.IMG_PATH+"/"+FileUtil.getFileName(path));
					Tool.initToast(cxt,"图片已保存至本地"+path);
					ContextUtil.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path)));
					return false;
				}
			});


			img.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					try {
						Intent intent= new Intent(cxt, ShowMapActivity.class);
						intent.putExtra("lat", obj.getString("lat"));
						intent.putExtra("lon", obj.getString("lon"));
						intent.putExtra("name", obj.getString("name"));
						intent.putExtra("address", obj.getString("address"));
						intent.putExtra("imageUrl", path);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						cxt.startActivity(intent);
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * play gif
	 * @param gif
	 * @param msg
	 */
	private void playGif(GifView gif, TextView msgView, ImageView img, String msg, int position){
		msgView.setVisibility(View.GONE);
		try {
			Field field = R.drawable.class.getDeclaredField(msg.substring(2,msg.indexOf("]")));
			int resId = Integer.parseInt(field.get(null).toString());
			if(getCount()-1 - position < 3 ){   //只显示三个动态
				gif.setVisibility(View.VISIBLE);
				gif.setGifImageType(GifImageType.COVER);
				gif.setGifImage(resId); 
			}else{
				img.setVisibility(View.VISIBLE);
				img.setBackgroundResource(resId);
			}
		}catch (NoSuchFieldException e) {
			msgView.setVisibility(View.VISIBLE);
			msgView.setText(ExpressionUtil.getText(cxt, StringUtil.Unicode2GBK(msg)));
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * play voice 
	 * @param file
	 * @param soundDuration
	 * @param voice
	 * @param isOut
	 */
	public MediaPlayer mping = new MediaPlayer();
	private void playSound(String file, TextView soundDuration, final ImageView voice, final boolean isOut) {
		final MediaPlayer mp = new MediaPlayer();
		voice.setVisibility(View.VISIBLE);
		soundDuration.setVisibility(View.VISIBLE);
		try {
			mp.setDataSource(file);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		soundDuration.setText(""+mp.getDuration()/1000+"\"");
		voice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mp.isPlaying())
					mp.stop();
				else{
					mp.start();
					mping = mp;
					new CountDownTimer(mp.getDuration(), 500) {
						int i =0;
						@Override
						public void onTick(long millisUntilFinished) {
							if (i <= mp.getDuration()/1000) {
								if(isOut)
									voice.setImageResource(resTo[i]);
								else
									voice.setImageResource(resFrom[i]);
								i++;
								if (i > 3)
									i = 0;
							}
						}
			
						@Override
						public void onFinish() {
							if(isOut)
								voice.setImageResource(resTo[0]);
							else
								voice.setImageResource(resFrom[0]);
						}
					}.start();
				}
			}
		});
	}


	private void playMovie(final String path, final TextView movieDuration, final ImageView img, final boolean isOut) {
		ViewGroup.LayoutParams para;
		para = img.getLayoutParams();

//		Bitmap bm = Utils.createVideoThumbnail(path);
//		Map map = ImageUtil.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
//		int width = bm.getWidth();
//		int height = bm.getHeight();

		para.width  = 240 * ContextUtil.width/1080;
		para.height = para.width*ContextUtil.height/ContextUtil.width;
		img.setLayoutParams(para);
		img.setBackgroundColor(Color.WHITE);

		if(MessageConstants.movieMap.containsKey(path)){    // 网络路径（即消息msg）：http://192.168.10.201:8000/files/video/201804092100355648054_qz_camera_1523278845058.mp4
			Map map = MessageConstants.movieMap.get(path);
			BitmapDrawable drawable = new BitmapDrawable((Bitmap) map.get("bitmap"));
			img.setBackgroundDrawable(drawable);
			movieDuration.setText("0:0"+map.get("duration"));
		}else if(MsgDbHelper.getInstance(activity).getonevideoMsg(path)!=null && MsgDbHelper.getInstance(activity).getonevideoMsg(path).size()>0){
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
							Map map = MsgDbHelper.getInstance(activity).getonevideoMsg(path);
							Bitmap bm = getRoundedCornerBitmap(compressImage(Utils.createVideoThumbnail((String)map.get("msgUrl"))));
							map.put("bitmap", bm);
							MessageConstants.movieMap.put(path, map);
							BitmapDrawable drawable = new BitmapDrawable(bm);
							img.setBackgroundDrawable(drawable);
							movieDuration.setText("0:0"+map.get("duration"));
						}
					}
				};

				public  Bitmap compressImage(Bitmap image) {
						// Scale down the bitmap if it's too large.
						int width = image.getWidth();
						int height = image.getHeight();
						int max = Math.max(width, height);
						if (max > 512)
						{
							float scale = 512f / max;
							int w = Math.round(scale * width);
							int h = Math.round(scale * height);
							image = Bitmap.createScaledBitmap(image, w, h, true);
						}//压缩图片 结束处

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

				public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
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
			}).start();

		}else{
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
							Map map = ImageUtil.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
							MessageConstants.movieMap.put(path, map);
							BitmapDrawable drawable = new BitmapDrawable((Bitmap)map.get("bitmap"));
							img.setBackgroundDrawable(drawable);
							movieDuration.setText("0:0"+map.get("duration"));
						}
					}
				};
			}).start();
		}

		img.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				if (path!=null && !path.equalsIgnoreCase("")) {
//					VideoFragment bigPic = VideoFragment.newInstance(path);
//					android.app.FragmentManager mFragmentManager = activity.getFragmentManager();
//					FragmentTransaction transaction = mFragmentManager.beginTransaction();
//					transaction.replace(R.id.main_menu, bigPic);
//					transaction.commit();

					Intent intent = new Intent(cxt, VideoActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putString("videoPath", path);
					bundle.putBoolean("useCache", false);
					intent.putExtras(bundle);
					cxt.startActivity(intent);
				}
			}
		});
	}

	class ViewHolder {
		TextView timeView, msgView, soundDuration, nameView, movieDuration;
		RoundTextView address;
		ImageView head, img, map, gifImg, voice, movie, textimg;
		GifView gif;
		FrameLayout flMap, flMovieView;
	}
	
}