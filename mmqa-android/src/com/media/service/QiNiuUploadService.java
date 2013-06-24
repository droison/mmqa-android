package com.media.service;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Thread.ThreadExecutor;

import com.media.httpservice.GetToken;
import com.media.httpservice.HTTP;
import com.media.httpservice.QiNiuFileUpload;
import com.media.httpservice.StringPost;
import com.media.info.Answer;
import com.media.info.AnswerUpload;
import com.qiniu.conf.Conf;
import com.qiniu.demo.QiniuUpload;
import com.qiniu.demo.QiniuUpload.CallbackByQiniuUpload;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

public class QiNiuUploadService extends Service {

	private static final int NOTIFICATION_ID = 0x13;
	private Notification notification = null;
	private NotificationManager manager = null;

	private static Context mContext;
	private static Object mObject;
	private static int mViewType;
	private static Bundle mBundle;
	/*
	 * 该方法为Service的抽象方法 必须实现 (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 第一次被创建时，调用该方法 (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
		notification = new Notification(R.drawable.setting_clicked, "正在上传，请稍候...",
				System.currentTimeMillis());
		notification.contentView = new RemoteViews(getApplication()
				.getPackageName(), R.layout.notice_upload);
		notification.contentView.setProgressBar(R.id.notice_upload__progress,
				100, 0, false);
		notification.contentView.setTextViewText(R.id.notice_upload__text, "进度"
				+ 0 + "%");
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		manager.notify(NOTIFICATION_ID, notification);
	}
	
	public static void startservice(Context c,Bundle b){
		mContext = c;
		mBundle = b;
		Intent iService=new Intent(mContext,QiNiuUploadService.class);         
		iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		iService.putExtras(mBundle);
		mContext.startService(iService);     
		}
	
	public static void startservice(Context c,Object o,int viewType){
		mContext = c;
		mObject = o;
		mViewType = viewType;
		Intent iService=new Intent(mContext,QiNiuUploadService.class);         
		iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		mContext.startService(iService);     
		}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ThreadExecutor.execute(new GetToken(mContext, handler));
		return START_REDELIVER_INTENT; // 表示在被异常KILL后，重传Intent并重启Service
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("服务销毁", "销毁成功！！！！！");
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.HANDLER_MESSAGE_NORMAL:
				String result = (String) msg.obj;
				
				if(!result.equals("success" ))
				{
					Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				}else{
					
					QiNiuFileUpload qfu = new QiNiuFileUpload(mContext, handler, mObject, mViewType);
					qfu.run();
					
				}	
				break;
			case AppConstant.HANDLER_UPLOAD_VIDEO:
				int uploadCount = (Integer) msg.obj;
				
				notification.contentView.setProgressBar(
						R.id.notice_upload__progress, 100, uploadCount, false);
				notification.contentView.setTextViewText(
						R.id.notice_upload__text, "进度" + uploadCount + "%");
				manager.notify(NOTIFICATION_ID, notification);
				break;
			case AppConstant.HANDLER_PUBLISH_SUCCESS:
				Toast.makeText(getApplicationContext(), "发布成功",
						Toast.LENGTH_SHORT).show();
				Intent bo = new Intent(AppConstant.UPLOAD_MESSAGE_ACTION);
				bo.putExtra("isQuestion", mViewType<20);
				sendBroadcast(bo);
				Intent iService=new Intent(mContext,QiNiuUploadService.class);         
				iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
				mContext.stopService(iService);
				break;
			case AppConstant.HANDLER_UPLOAD_IMAGE_SUCCESS:
				Toast.makeText(getApplicationContext(), "上传完毕",
						Toast.LENGTH_SHORT).show();
				manager.cancel(NOTIFICATION_ID);
				break;
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(getApplicationContext(), "上传失败",
						Toast.LENGTH_SHORT).show();
				manager.cancel(NOTIFICATION_ID);
				
				Intent iService2=new Intent(mContext,QiNiuUploadService.class);         
				iService2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
				mContext.stopService(iService2);
				break;
			default:
				break;
			}
		}
	};

}
