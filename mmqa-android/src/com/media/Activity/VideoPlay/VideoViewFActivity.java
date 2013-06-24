package com.media.Activity.VideoPlay;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import com.media.Activity.AbstractMMQAActivity;
import com.media.Activity.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class VideoViewFActivity extends AbstractMMQAActivity{

	private Button buttonClose;
	private String videoUrl;
	private VideoView mVideoView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Bundle bundle = this.getIntent().getExtras();
		//videoUrl = bundle.getString("videoUrl");
		videoUrl = "http://192.168.8.111/2013/05/09/11/81368068575213.m3u8";
		setContentView(R.layout.video_view_f);
		
		mVideoView = (VideoView) this.findViewById(R.id.surface_view);
		buttonClose = (Button) this.findViewById(R.id.buttonClose);
				
		buttonClose.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mVideoView.pause();
						finish();
					}
				});
			
		mVideoView.setVideoPath(videoUrl);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.requestFocus();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	if (mVideoView != null)
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
	super.onConfigurationChanged(newConfig);
	}
	
}