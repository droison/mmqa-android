package com.media.Activity;

import java.io.File;

import com.constants.AppConstant;
import com.media.mtj.BaiduSDK;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author chaisong
 */
public abstract class AbstractMMQAActivity extends BaiduSDK {

	protected static Boolean isChangeA = false;
	protected static Boolean isChangeB = false;

	private ProgressDialog progressDialog;

	private boolean destroyed = false;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyed = true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		File baseDir_temp = new File(AppConstant.BASE_DIR_PATH);
		File baseDir_cache = new File(AppConstant.BASE_DIR_CACHE);
		if(!baseDir_cache.isDirectory())
			baseDir_cache.mkdirs();
		if(!baseDir_temp.isDirectory())
			baseDir_temp.mkdir();		
	}
	
	// ***************************************
	// Public methods
	// ***************************************
	public void showLoadingProgressDialog() {
		this.showProgressDialog("Loading. Please wait...");
	}

	public void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (progressDialog != null && !destroyed) {
			progressDialog.dismiss();
		}
	}

	protected void displayResponse(String result) {
		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
	}
}
