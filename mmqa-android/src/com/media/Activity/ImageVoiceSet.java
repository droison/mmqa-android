package com.media.Activity;

import com.media.service.AudioService;
import com.media.service.VoiceDialogUtil;
import com.media.service.VoiceDialogUtil.OnCallback;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageVoiceSet extends AbstractMMQAActivity implements
		OnClickListener {

	private ImageView image;
	private ImageView play;
	private ImageView back;
	private RelativeLayout addvoice;
	private String imagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_voice);
		initData();
		setUpView();
		addListener();
	}

	private void initData() {
		imagePath = getIntent().getStringExtra("imagePath");
	}

	private void setUpView() {
		image = (ImageView) this.findViewById(R.id.image01);
		play = (ImageView) this.findViewById(R.id.play);
		back = (ImageView) this.findViewById(R.id.back);
		addvoice = (RelativeLayout) this.findViewById(R.id.add_voice);
		image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
	}

	private void addListener() {
		play.setOnClickListener(this);
		back.setOnClickListener(this);
		addvoice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:

			break;

		case R.id.back:
			finish();
			break;

		case R.id.add_voice:
			VoiceDialogUtil dialog = new VoiceDialogUtil(
					ImageVoiceSet.this, R.style.DialogVoice, 13, new OnCallback() {
						
						@Override
						public void back(String voicePath) {
							setResult(RESULT_OK);
							finish();
						}
					});
			dialog.show();
			AudioService.getService().start();
			break;

		default:
			break;
		}
	}

}
