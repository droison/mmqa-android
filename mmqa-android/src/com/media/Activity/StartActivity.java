package com.media.Activity;

import com.media.Thread.ThreadExecutor;
import com.media.Util.CheckNet;
import com.media.db.AccountInfoService;
import com.media.db.DBHelper;
import com.media.httpservice.AccountSyn;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;

public class StartActivity extends AbstractMMQAActivity {

	private AccountInfoService accountInfoService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_start);
		ImageView iv = (ImageView) findViewById(R.id.startimage);
		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(3000);
		iv.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				Boolean b = false;
				b = new CheckNet(StartActivity.this).netState();
				
					DBHelper.init(StartActivity.this);
					accountInfoService = new AccountInfoService(
							StartActivity.this);
				if (b) {
					ThreadExecutor.execute(new InitDataThread());
				} else {
					AlertDialog.Builder builer = new Builder(StartActivity.this);
					builer.setTitle("警告");
					builer.setMessage("暂时没有联网，请检测网络连接");

					builer.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									StartActivity.this.finish();
								}
							});
					AlertDialog dialog = builer.create();
					dialog.show();
				}

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {}
		});

	}

	class InitDataThread implements Runnable {

		@Override
		public void run() {

			if (accountInfoService.isExist()) {
				String username = accountInfoService.getUserInfo();
				ThreadExecutor.execute(new AccountSyn(username, getApplicationContext()));
				Intent intent = new Intent(StartActivity.this,MainTabActivity.class);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(StartActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}

	}
}
