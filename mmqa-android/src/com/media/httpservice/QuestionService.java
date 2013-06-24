package com.media.httpservice;

import org.apache.http.HttpStatus;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Util.JsonUtil;
import com.media.Util.StringUtil;

import android.content.Context;
import android.os.Handler;

public class QuestionService extends Thread{

	private int size;
	private String listUrl;
	private Handler mHandler;
	private Context mContext;
	private boolean greater;
	private long updateTime;
	private String username;
	
	public QuestionService(boolean greater,long updateTime,int size,String listUrl,String username,Handler mHandler,Context mContext){
		this.greater = greater;
		this.updateTime = updateTime;
		this.listUrl = listUrl;
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.size = size;
		this.username = username;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 StringBuilder url = new StringBuilder(listUrl);

         if (listUrl.indexOf("?") > 0) {
             url.append("&");
         } else {
             url.append("?");
         }

		listUrl = url.append("?size=").append(size).append("&greater=").append(greater).append("&updateTime=").append(updateTime).append("&username=").append(username).toString();
		
		HttpResponseEntity hre = HTTP.get(listUrl);

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
            }else{
            	mHandler.sendMessage(mHandler.obtainMessage( AppConstant.HANDLER_MESSAGE_NORMAL,
                        JsonUtil.getQuestionList(result, mContext)));
            }
           
           }else{
        	   mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR,
                mContext.getString(R.string.httpstatus_error)));
        	   return;
           }                  
		
	}
	
}
