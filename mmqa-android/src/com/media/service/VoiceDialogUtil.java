package com.media.service;

import java.io.File;

import com.constants.AppConstant;
import com.media.Activity.R;
import com.media.Thread.ThreadExecutor;
import com.media.db.AccountInfoService;
import com.media.info.AnswerUpload;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义dialog
 * 
 * @author chaisong
 */
public class VoiceDialogUtil extends Dialog {

	private RelativeLayout dialog_voice_rlayout;

	private AccountInfoService accountInfoService;

	private ProgressBar progress_upload, progress_volume_size;

	private final int messageGetAudioInfo = 0x0012;

	private final int messageGetVolumnSize = 0x0001;

	private int volumnSize = 0;

	private boolean VolumnShow = true;

	private Context context;

	private String questionId;

	private AnswerUpload au;
	
	private OnCallback back;

	private int type; // 13为问题 图片+语音，14为 问题 语音 ；23为回答 图片+语音，24为 回答 语音 13、14不上传

	public VoiceDialogUtil(Context context, int theme, int type) {

		super(context, theme);
		this.context = context;
		this.type = type;
	}
	
	public VoiceDialogUtil(Context context, int theme, int type,OnCallback back) {

		super(context, theme);
		this.context = context;
		this.type = type;
		this.back = back;
	}

	public VoiceDialogUtil(Context context, int theme, int type,
			String questionId) {

		super(context, theme);
		this.type = type;
		this.context = context;
		this.questionId = questionId;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AudioService.getService().stop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_voice);
		TextView cancel = (TextView) findViewById(R.id.voice_cancel);
		cancel.setOnClickListener(clickListener);
		dialog_voice_rlayout = (RelativeLayout) this
				.findViewById(R.id.dialog_voice_rlayout);
		progress_upload = (ProgressBar) this.findViewById(R.id.progress_upload);
		progress_volume_size = (ProgressBar) this
				.findViewById(R.id.progress_volume_size);
		progress_volume_size.setProgress(volumnSize);
		new VolumnShowThread().start();
		if (type > 20) {
			accountInfoService = new AccountInfoService(context);
			au = new AnswerUpload();
			au.setVoicePath(AppConstant.VOICE_PATH);
			au.setQuestionId(questionId);
			au.setUserId(accountInfoService.getAccount().getId());
		}
	}

	class VolumnShowThread extends Thread {
		public void run() {
			while (VolumnShow) {
				try {
					volumnSize = AudioService.getService().getVolumnSize();
					myHandler.sendEmptyMessage(messageGetVolumnSize);
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dialog_voice_rlayout.setVisibility(View.GONE);
			progress_upload.setVisibility(View.VISIBLE);
			VolumnShow = false;
			new Thread(new GetAudioInfo()).start();
			if(type == 13||type == 14){
				back.back(AppConstant.VOICE_PATH);
			}
		}
	};
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int temp = msg.what;
			switch (temp) {
			case messageGetAudioInfo:

				VoiceDialogUtil.this.dismiss();

				break;
			case messageGetVolumnSize:
				progress_volume_size.setProgress(volumnSize / 350);
				break;
			default:
				break;
			}
		}
	};

	class GetAudioInfo implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			AudioService.getService().stop();
			if (type > 20)
				QiNiuUploadService.startservice(context, au, type);
			myHandler.sendEmptyMessage(messageGetAudioInfo);
		}

	}
	
	public interface OnCallback{
		public void back(String voicePath);
	}

}
