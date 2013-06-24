package com.media.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.constants.AppConstant;
import com.media.Util.ImageSave;
import com.media.httpservice.HttpDataService;
import com.media.info.AnswerUpload;
import com.media.info.QuestionUpload;
import com.media.info.Video;

public class NativeCameraActivity extends AbstractMMQAActivity implements
		OnClickListener {

	private Button highquality;
	private Button lowquality;
	private Intent videoCaptureIntent;
	private int CODE_VIDEO;
	private Uri cameraVideoURI;
	private Bundle bundle;
	private Video video;
	private boolean isQuestion;
	private RelativeLayout reLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native_camera);
		initData();
		setUpView();
		addListener();

	}

	private void initData() {
		File dir = new File(AppConstant.BASE_DIR_PATH);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}

		bundle = this.getIntent().getExtras();
		isQuestion = bundle.getBoolean("isQuestion");

		videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		videoCaptureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
		ContentValues values = new ContentValues();
		cameraVideoURI = getContentResolver().insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
		videoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraVideoURI);
		CODE_VIDEO = 0X1112;
	}

	private void setUpView() {
		highquality = (Button) this.findViewById(R.id.highquality);
		lowquality = (Button) this.findViewById(R.id.lowquality);
		reLayout = (RelativeLayout) this
				.findViewById(R.id.native_camera_relativeLayout);
	}

	private void addListener() {
		highquality.setOnClickListener(this);
		lowquality.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.highquality:

			videoCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(videoCaptureIntent, CODE_VIDEO);
			break;
		case R.id.lowquality:
			videoCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(videoCaptureIntent, CODE_VIDEO);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE_VIDEO) {
			if (data == null || data.getData() == null) {
				Log.v(TAG, "没有数据");
				finish();
				return;
			} else {
				String[] projection = { MediaColumns.DATA, MediaColumns.SIZE };
				Cursor cursor = managedQuery(cameraVideoURI, projection, null,
						null, null);
				int column_index_data = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				int column_index_size = cursor
						.getColumnIndexOrThrow(MediaColumns.SIZE);
				cursor.moveToFirst();
				String recordedVideoFilePath = cursor
						.getString(column_index_data);
				File videoPath = new File(recordedVideoFilePath);
				byte[] is = new byte[1024];
				int c;
				try {
					FileInputStream input = new FileInputStream(videoPath);
					FileOutputStream fos = new FileOutputStream(
							AppConstant.VIDEO_PATH);
					while ((c = input.read(is)) > 0) {
						fos.write(is);
					}
					input.close();
					fos.close();
					videoPath.delete();

					new ImageSave(true);

					if (!isQuestion) {
						AnswerUpload au = HttpDataService.getInstance(NativeCameraActivity.this)
								.getAnswerUpload();
						au.setUserId(HttpDataService.getInstance(NativeCameraActivity.this).getAccount().getId());
						au.setQuestionId(bundle.getString("questionId"));
						au.setCameraInfo("android.native");
						au.setEncode("H.264");
						au.setFileType("MP4");
						au.setVideoPath(AppConstant.VIDEO_PATH);
						au.setThumbPath(AppConstant.THUMB_PATH);
						Intent uploadAnswer = new Intent(
								getApplicationContext(), UploadActivity.class);
						startActivity(uploadAnswer);
						Intent toUpload = new Intent(NativeCameraActivity.this,
								UploadActivity.class);
						toUpload.putExtras(bundle);
						startActivity(toUpload);
					} else {
						QuestionUpload qu = HttpDataService.getInstance(NativeCameraActivity.this).getQuestionUpload();
						qu.setUserId(HttpDataService.getInstance(NativeCameraActivity.this).getAccount().getId());
						qu.setCameraInfo("android.native");
						qu.setEncode("H.264");
						qu.setFileType("MP4");
						qu.setVideoPath(AppConstant.VIDEO_PATH);
						qu.setThumbPath(AppConstant.THUMB_PATH);
					}
					finish();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
