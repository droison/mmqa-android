package com.media.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.constants.AppConstant;
import com.media.Util.GetDuration;
import com.media.Util.GetDuration.OnCustomCallBack;
import com.media.httpservice.HttpDataService;
import com.media.info.AnswerUpload;
import com.media.service.QiNiuUploadService;

public class UploadActivity extends AbstractMMQAActivity {
	private Button btnUploadFile = null;
	private LinearLayout upload_layout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		useServiceConfig();
	}

	private void useServiceConfig() {

		btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
		upload_layout = (LinearLayout) findViewById(R.id.upload_layout);
		btnUploadFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new GetDuration(UploadActivity.this, upload_layout,
						new OnCustomCallBack() {

							@Override
							public void back(long info) {
								int i = (int) (info / 1000);

								AnswerUpload au = HttpDataService.getInstance(UploadActivity.this)
										.getAnswerUpload();
								au.setDuration(i + "s");

								QiNiuUploadService.startservice(
										getApplicationContext(), au,
										AppConstant.AVIDEOViewType);
								finish();

								Toast.makeText(getApplicationContext(),
										"发布回答中...", Toast.LENGTH_SHORT).show();

							}
						});
			}

		});
	}
}