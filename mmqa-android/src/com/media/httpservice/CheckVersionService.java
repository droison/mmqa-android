
package com.media.httpservice;

import org.json.JSONObject;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Util.StringUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 手机版本升级服务
 * 
 * @author peikuo
 */
public class CheckVersionService implements Runnable {

    private Handler handler;

    private Context context;

    public CheckVersionService(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    /**
     * 检查更新
     */
    public void run() {


        String updateUrl = context.getResources().getString(R.string.update_url);


        try {

        	 HttpResponseEntity hre = HTTP.get(updateUrl);
             String result = StringUtil.byte2String(hre.getB());

            JSONObject jo = new JSONObject(result);

            JSONObject data = jo.getJSONObject("data");

            int versionCode = context.getPackageManager().getPackageInfo("com.media.Activity", 0).versionCode;
            
            Log.v("versionCode", ""+versionCode);

            int newVersion = data.getInt("version");

            if (newVersion > versionCode) {
                handler.sendMessage(handler.obtainMessage(AppConstant.HANDLER_VERSION_UPDATE,
                		result));
            }

        } catch (Exception e) {
            Log.e("UpdateVersion", "http error", e);
            handler.sendMessage(handler.obtainMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR));
        }

    }

}
