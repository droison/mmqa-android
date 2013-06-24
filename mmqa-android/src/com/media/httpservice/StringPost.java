package com.media.httpservice;

import android.content.Context;
import android.widget.Toast;

public class StringPost implements Runnable {
	private Context mContext;
	private String url;
	private Object postObject;
	
	public StringPost(Context mContext,String url,Object postObject){
		this.mContext = mContext;
		this.url = url;
		this.postObject = postObject;
	}
	public void run() {
		HttpResponseEntity hre = HTTP.postByHttpUrlConnection(url, postObject);
		/*if(hre.getHttpResponseCode() == 200){
			Toast.makeText(mContext, "成功！", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(mContext, "失败了，请重试", Toast.LENGTH_SHORT).show();
		}*/
	}
	
}