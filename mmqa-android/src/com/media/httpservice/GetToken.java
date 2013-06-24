package com.media.httpservice;

import org.json.JSONObject;

import com.constants.AppConstant;
import com.media.Util.StringUtil;

import android.content.Context;
import android.os.Handler;

public class GetToken implements Runnable{
	
	private Context mContext;
	private Handler mHandler;
	
	public GetToken(Context mContext, Handler mHandler){
		this.mContext = mContext;
		this.mHandler = mHandler;
	}
	
	/**
	 * 
	 * @param mContext
	 * @param type 1:"mmqa-vid";2:"mmqa-img";3:"mmqa-aud"; 其它无效
	 * @return
	 */

	@Override
	public void run() {
		long tokenTime = HttpDataService.getInstance(mContext).getTokenTime();
		String[] token= HttpDataService.getInstance(mContext).getToken();
		String result="";
		if(tokenTime != 0&&System.currentTimeMillis()- tokenTime < 600000 &&token!=null )
		{
			result = "success";
			mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_MESSAGE_NORMAL,result));
		}else{
			HttpResponseEntity hre = HTTP.get(AppConstant.serverUrl + "auth/getToken" );
			if(hre.getHttpResponseCode() == 200){
				try {
					String json = StringUtil.byte2String(hre.getB());
					JSONObject jo = new JSONObject(json);
					if(jo.get("result").equals("success")){
						JSONObject jotoken = jo.getJSONObject("token");
						token[0] = (String) jotoken.get("vidtoken");
						token[1] = (String) jotoken.get("imgtoken");
						token[2] = (String) jotoken.get("audtoken");
						tokenTime = System.currentTimeMillis();
						HttpDataService.getInstance(mContext).setToken(token);
						HttpDataService.getInstance(mContext).setTokenTime(tokenTime);
						result = "success";
						mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_MESSAGE_NORMAL,result));
					}else{
						result = (String) jo.get("reason");
						mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR,result));
					}
					
				} catch (Exception e) {
					result = "error";
					mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR,result));
				}
			}else{
				result = "error";
				mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR,result));
			}
		}
	}

}
