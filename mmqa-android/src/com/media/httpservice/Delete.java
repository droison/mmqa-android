package com.media.httpservice;

import com.constants.AppConstant;

import android.os.Handler;

public class Delete implements Runnable {
	private Handler mHandler;
	private String url;
	
	public Delete(Handler mHandler,String url){
		this.mHandler = mHandler;
		this.url = url;
	}
	public void run() {
		boolean b = HTTP.deleteMothod(url);
		if(b){
				mHandler.sendEmptyMessage(AppConstant.HANDLER_MESSAGE_NORMAL);
		}else{
			mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
		}
	}
	
}