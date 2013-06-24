package com.media.httpservice;

import org.apache.http.HttpStatus;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Util.JsonUtil;
import com.media.Util.StringUtil;

import android.content.Context;
import android.os.Handler;

public class QuestionInfoService extends Thread{

	private String infoUrl;
	private Handler mHandler;
	private Context mContext;
	private int delayTime = 0;
	
	public QuestionInfoService(String infoUrl,Handler mHandler,Context mContext ,int delayTime){
		this.infoUrl = infoUrl;
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.delayTime = delayTime;

	}
	
	public QuestionInfoService(String infoUrl,Handler mHandler,Context mContext){
		this.infoUrl = infoUrl;
		this.mContext = mContext;
		this.mHandler = mHandler;

	}
	
	@Override
	public void run() {

		if(delayTime != 0){
			try {
				sleep(delayTime);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		HttpResponseEntity hre = HTTP.get(infoUrl);

        String result = null;
        
        if(hre == null){
			mHandler.sendMessage(mHandler.obtainMessage(
					AppConstant.HANDLER_HTTPSTATUS_ERROR,
					mContext.getString(R.string.httpstatus_error)));
			return;
		}
        
        if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
        	
        	try {
				result = StringUtil.byte2String(hre.getB());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if (StringUtil.isEmpty(result)) {
                mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_MESSAGE_NULL,
                        mContext.getString(R.string.message_null)));
                return;
            }
           mHandler.sendMessage(mHandler.obtainMessage( AppConstant.HANDLER_MESSAGE_NORMAL,
                            JsonUtil.getQuestionInfo(result, mContext)));
           }else{
        	   mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR,
                mContext.getString(R.string.httpstatus_error)));
        	   return;
           }                  
		
	}
	
}
