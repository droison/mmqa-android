package com.media.mtj;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.os.Bundle;

public class BaiduSDK extends Activity{
	
	protected String TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TAG = this.getClass().getName();
		super.onCreate(savedInstanceState);
		StatService.setSessionTimeOut(25);
		StatService.setOn(this, StatService.EXCEPTION_LOG);
		StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
}
