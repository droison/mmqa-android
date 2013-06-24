package com.media.httpservice;

import io.vov.utils.Log;

import com.constants.AppConstant;
import com.media.Util.StringUtil;

import android.os.Handler;

public class StringGet implements Runnable {
	private Handler mHandler;
	private String url;
	
	public StringGet(Handler mHandler,String url){
		this.mHandler = mHandler;
		this.url = url;
	}
	public void run() {
		HttpResponseEntity hre = HTTP.get(url);
		switch (hre.getHttpResponseCode()) {
		case 200:
			try {
				mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_MESSAGE_NORMAL, StringUtil.byte2String(hre.getB())));
			} catch (Exception e) {
				mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
				Log.e("StringGet", e);
			}
			break;
		case 204:
			mHandler.sendEmptyMessage(AppConstant.HANDLER_MESSAGE_NORMAL);
			break;
		default:
			mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
			Log.d("HTTPcode", hre.getHttpResponseCode());
			break;
		}
	}
	
}