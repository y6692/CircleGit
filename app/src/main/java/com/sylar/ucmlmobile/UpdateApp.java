package com.sylar.ucmlmobile;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.circlegit.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class UpdateApp {
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private static final int CONNECT_FAILED = 0;
	private static final int CONNECT_SUCCESS = 1;
	HashMap<String, String> mHashMap;
	private String mSavePath;
	private int progress;
	private boolean cancelUpdate = false;
	private Context mContext;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String mXmlPath;
	
	public UpdateApp(Context context, String xmlPath, String savePath)
	{
		this.mContext = context;
		this.mXmlPath = xmlPath;
		this.mSavePath = savePath;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessaeg(Message msg) {
			switch(msg.what) {
			case DOWNLOAD:
				mProgress.setProgress(progress);
				break;

			case DOWNLOAD_FINISH:
				installApk();
				break;

			default:
				break;
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case CONNECT_FAILED:
				Toast.makeText(mContext, "访问服务器失败!", Toast.LENGTH_SHORT).show();
				break;

			case CONNECT_SUCCESS:
				if(null != mHashMap) {
					int serviceCode = Integer.valueOf(mHashMap.get("version"));
					if(serviceCode > getVersionCode(mContext)) {
						showNoticeDialog();
					}
				}
				break;
			}
		}
	}; 
	
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("检测到新版本,是否更新?");
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("正在更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		builder.setNegativeButton("取消下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		downloadApk();
	}
	
	private void downloadApk() {
		new downloadApkThread().start();
	}
	
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					URL url = new URL(mHashMap.get("url"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					File file = new File(mSavePath);
					if(!file.exists()) {
						file.mkdir();
					}
					
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float)count/length)*100);
						mHandler.sendEmptyMessage(DOWNLOAD);
						if(numread <= 0) {
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					}while(!cancelUpdate);

					fos.close();
					is.close();
				}
			}catch (MalformedURLException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
			
			mDownloadDialog.dismiss();
		}
	}
	
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		}catch(NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if(!apkfile.exists()) {
			return;
		}
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://"+apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
	
	public void checkUpdate() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					URL url = new URL(mXmlPath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					
					InputStream inStream = conn.getInputStream();
					mHashMap = parseXml(inStream);
					Message msg = new Message();
					msg.what = CONNECT_SUCCESS;
					handler.sendMessage(msg);
				}catch(Exception e) {
					Message msg = new Message();
					msg.what = CONNECT_FAILED;
					handler.sendMessage(msg);
				}
			}
		}).run();
	}
	
	public boolean existUpdate() {
		boolean hasUpdate = false;
		try {
			URL url = new URL(mXmlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			
			InputStream inStream = conn.getInputStream();
			mHashMap = parseXml(inStream);
			if(null != mHashMap) {
				int serviceCode = Integer.valueOf(mHashMap.get("version"));
				if(serviceCode > getVersionCode(mContext)) {
					hasUpdate = true;
				}
			}
		}catch (Exception e) {
			hasUpdate = false;
		}
	
		return hasUpdate;
	}
	
	private HashMap<String, String> parseXml(InputStream inStream) throws Exception {
		HashMap<String, String> hashMap = new HashMap<String,String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();
		NodeList childNodes = root.getChildNodes();
		for(int j=0;j<childNodes.getLength();j++) {
			Node childNode = (Node) childNodes.item(j);
			
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				
				if("version".equals(childElement.getNodeName())) {
					hashMap.put("version", childElement.getFirstChild().getNodeValue());
				}
				else if("name".equals(childElement.getNodeName())) {
					hashMap.put("name", childElement.getFirstChild().getNodeValue());
				}
				else if("url".equals(childElement.getNodeName())) {
					hashMap.put("url", childElement.getFirstChild().getNodeValue());
				}
				
			}
		}
		return hashMap;
	}
}
