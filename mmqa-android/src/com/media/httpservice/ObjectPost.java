package com.media.httpservice;

import com.constants.AppConstant;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ObjectPost implements Runnable {
	private Context mContext;
	private Handler mHandler;
	private String url;
	private Object postObject;
	
	public ObjectPost(Context mContext, Handler mHandler, String url, Object postObject){
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.url = url;
		this.postObject = postObject;
	}
	public void run() {
		HttpResponseEntity hre = HTTP.postByHttpUrlConnection(url, postObject);
		if(hre.getHttpResponseCode() == 200){
			mHandler.sendEmptyMessage(AppConstant.HANDLER_PUBLISH_SUCCESS);
		}else{
			mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
		}
	}
	
}