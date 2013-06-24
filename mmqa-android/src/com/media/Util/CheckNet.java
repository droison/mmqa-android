/**
 * 功能：检测网络是否连接
 * @author ls
 * 
 */
package com.media.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNet {
	Context context;
	
	public CheckNet(Context con) {
		this.context = con;
	}
	
	public Boolean netState(){
		Boolean b = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo!=null){
				b = networkInfo.isAvailable();
			}
		return b;
	}

}
