package com.media.Activity;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.constants.AppConstant;
import com.media.Thread.ThreadExecutor;
import com.media.Util.CheckNet;
import com.media.Util.JsonUtil;
import com.media.Util.StringUtil;
import com.media.db.AccountInfoService;
import com.media.httpservice.AccountSyn;
import com.media.httpservice.HTTP;
import com.media.httpservice.HttpDataService;
import com.media.httpservice.HttpResponseEntity;
import com.media.info.Account;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AbstractMMQAActivity{

	private AccountInfoService accountInfoService;
	private String key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		accountInfoService = new AccountInfoService(this);
		
	/*	EditText username = (EditText) findViewById(R.id.username);
		EditText password = (EditText) findViewById(R.id.password);
		username.setText("maojingli");
		password.setText("buptnu");

		Boolean b = new CheckNet(LoginActivity.this).netState();
		if(b){
			new FetchSecuredResourceTask().execute();
		}else{
			displayResponse("网络断开，请检查网络连接");
		}*/

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Boolean b = new CheckNet(LoginActivity.this).netState();
						if(b){
							new FetchSecuredResourceTask().execute();
						}else{
							displayResponse("网络断开，请检查网络连接");
						}
						
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Boolean b = new CheckNet(LoginActivity.this).netState();
						if(b){
							Intent toRegister = new Intent();
							toRegister.setClass(getBaseContext(),
									RegisterActivity.class);
							startActivity(toRegister);
						}else{
							displayResponse("网络断开，请检查网络连接");
						}
						
					}
				});
	}


	private Integer checkOut(String username, String password) {
		Integer i = 0;
		String url = AppConstant.serverUrl + "auth/login?username=" + username
				+ "&password=" + password;
		String result = null;
		
		HttpResponseEntity hre = HTTP.get(url);
		
		if(hre.getHttpResponseCode() != 200){
			i = 6;
		}else{
			try {
				result = StringUtil.byte2String(hre.getB());
				JSONObject jo = new JSONObject(result);
				if(jo.get("result").equals("fail")){
					if(jo.get("reason").equals("usernameError"))
						i = 4;
					else if(jo.get("reason").equals("passwordError"))
						i = 5;				
				}else{
					key = jo.getString("JSESSIONID");
					Account account = JSON.parseObject(jo.getString("account"), Account.class);
					accountInfoService.saveOrUpdate(account);
					HttpDataService.getInstance(LoginActivity.this).setAccount(account);
					HttpDataService.getInstance(LoginActivity.this).setSessionId(key);
				}								
			} catch (Exception e) {
				i = 6;
			}
		}
		return i;
	}

	private class FetchSecuredResourceTask extends
			AsyncTask<Void, Void, Integer> {

		private String username;
		private String password;
		private View focusView = null;

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();

			// build the message object
			EditText editText = (EditText) findViewById(R.id.username);
			this.username = editText.getText().toString();

			editText = (EditText) findViewById(R.id.password);
			this.password = editText.getText().toString();
		}

		@Override
		protected Integer doInBackground(Void... params) {

			Integer i = 0;

			// Check for a valid password.
			if (TextUtils.isEmpty(password)) {
				focusView = findViewById(R.id.password);
				i = 1;
			} else if (password.length() < 4) {
				focusView = findViewById(R.id.password);
				i = 2;
			}

			if (TextUtils.isEmpty(username)) {
				focusView = findViewById(R.id.username);
				i = 3;
			}

			if (i == 0) {
				InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 输入法弹出则关闭，关闭则弹出
				i = checkOut(username, password);

			}
			return i;
		}

		@Override
		protected void onPostExecute(Integer i) {
			dismissProgressDialog();
			switch (i) {
			case 1:
				focusView.requestFocus();
				displayResponse(getString(R.string.error_username_required));
				break;
			case 2:
				focusView.requestFocus();
				displayResponse(getString(R.string.error_invalid_password));
				break;
			case 3:
				focusView.requestFocus();
				displayResponse(getString(R.string.error_password_required));
				break;
			case 4:

				displayResponse("没有这个用户名，请检查拼写");
				break;
			case 5:

				displayResponse("密码错误，请重新输入");
				break;
			case 0:
			
				HttpDataService.getInstance(LoginActivity.this).setSessionId(key);
				Log.v("保存", "成功");
				Intent intent = new Intent(LoginActivity.this,
						MainTabActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				displayResponse("系统错误，请重启应用重试");
				break;
			}
		}
	}
}
