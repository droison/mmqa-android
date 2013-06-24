package com.media.Activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.constants.AppConstant;
import com.media.Thread.ThreadExecutor;
import com.media.Util.ExitApplication;
import com.media.httpservice.ApkDownloadService;
import com.media.httpservice.CheckVersionService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private static MainHandler handler;

	/** Called when the activity is first created. */
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);

		this.mAIntent = new Intent(this, AActivity.class);
		this.mBIntent = new Intent(this, BActivity.class);
		this.mCIntent = new Intent(this, CActivity.class);

		ExitApplication.getInstance().addActivity(this);

		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2))
				.setOnCheckedChangeListener(this);

		setupIntent();

		handler = new MainHandler(this);
		checkVersion();
		StatService.setSessionTimeOut(30);
		StatService.setOn(this, StatService.EXCEPTION_LOG);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.mTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_button1:
				this.mTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_button2:
				this.mTabHost.setCurrentTabByTag("C_TAB");
				break;
			}
		}

	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_attention,
				R.drawable.home, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_whole,
				R.drawable.search, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB", R.string.main_iask,
				R.drawable.iask, this.mCIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	private void checkVersion() {
		ThreadExecutor.execute(new CheckVersionService(handler, this));
	}

	public static class MainHandler extends Handler {

		/**
		 * 更新版本使用的URL
		 */
		private String downloadUrl;

		/**
		 * 更新进度
		 */
		private ProgressBar mProgress;

		/**
		 * 下载提示框
		 */
		private Dialog downloadDialog;

		private Activity context;

		public MainHandler(Activity context) {
			this.context = context;
		}

		protected void installApk(File file) {

			Intent intent = new Intent();

			intent.setAction(Intent.ACTION_VIEW);

			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");

			context.startActivity(intent);
		}

		@Override
		public void handleMessage(Message mes) {
			switch (mes.what) {

			case AppConstant.HANDLER_VERSION_UPDATE:
				String result = (String) mes.obj;
				JSONObject jo;
				String info = "";
				try {
					jo = new JSONObject(result);
					JSONObject data = jo.getJSONObject("data");
					info = jo.getString("info");
			        downloadUrl = data.getString("url");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	           
				AlertDialog.Builder builer = new Builder(context);
				builer.setTitle("升级提示");
				builer.setMessage(info.equals("")?"新版本发布了，强烈建议更新":info);

				builer.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AlertDialog.Builder builder = new Builder(
										context);
								builder.setTitle("MMQA新版本下载更新中");
								final LayoutInflater inflater = LayoutInflater
										.from(context);
								View v = inflater.inflate(R.layout.progress,
										null);
								mProgress = (ProgressBar) v
										.findViewById(R.id.progress);
								builder.setView(v);
								downloadDialog = builder.create();
								downloadDialog.setCancelable(false);
								downloadDialog.show();
								ThreadExecutor.execute(new ApkDownloadService(
										downloadUrl, MainHandler.this));
							}
						});

				builer.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								context.finish();
							}
						});
				AlertDialog dialog = builer.create();
				dialog.show();
				break;
			case AppConstant.HANDLER_APK_DOWNLOAD_PROGRESS:
				mProgress.setProgress((Integer) mes.obj);
				break;
			case AppConstant.HANDLER_APK_DOWNLOAD_FINISH:
				File file = new File(AppConstant.BASE_DIR_PATH,
						AppConstant.APK_NAME);

				installApk(file);
				break;
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(context, "服务器出错啦", Toast.LENGTH_SHORT).show();
				break;

			}
		}

	}
}