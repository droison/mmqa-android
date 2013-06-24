package com.media.httpservice;

import io.vov.utils.Log;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Util.JsonUtil;
import com.media.Util.StringUtil;
import com.media.db.AccountInfoService;
import com.media.info.Account;

import android.content.Context;
import android.widget.Toast;

public class AccountSyn implements Runnable {

	private final String TAG = "AccountSyn";
	private String username;
	private Context mContext;
	private AccountInfoService accountInfoService;

	public AccountSyn(String username, Context mContext) {
		this.mContext = mContext;
		this.username = username;
		accountInfoService = new AccountInfoService(mContext);
	}

	@Override
	public void run() {
		String url = AppConstant.serverUrl + "auth?username=" + username;
		HttpResponseEntity hre = HTTP.get(url);

		String result = null;

		if (hre == null) {
			Log.e(TAG, "hre为空");
			return;
		}

		if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {

			try {
				result = StringUtil.byte2String(hre.getB());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (StringUtil.isEmpty(result)) {
				Log.e(TAG, "200，result为空");
				return;
			}
			
			JSONObject jo;
			try {
				jo = new JSONObject(result);
				String sessionid = jo.getString("JSESSIONID");
				Account account = JsonUtil.getAccoutInfo(jo.getString("account"), mContext);
				accountInfoService.saveOrUpdate(account);
				HttpDataService.getInstance(mContext).setAccount(account);
				HttpDataService.getInstance(mContext).setSessionId(sessionid);
			} catch (JSONException e) {
				Log.e(TAG, "200，格式化异常");
			}			
		} else {
			Log.e(TAG, "不是200");
			return;
		}
	}

}
