package com.media.Activity.VideoPlay;

import com.media.Activity.AbstractMMQAActivity;
import com.media.Activity.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class VideoViewBActivity extends AbstractMMQAActivity{

	private VideoView mVideoView;
	private String videoUrl;
	private Button buttonClose;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		

		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Bundle bundle = this.getIntent().getExtras();
		//videoUrl = bundle.getString("videoUrl");
		videoUrl = "http://192.168.8.111/2013/05/09/11/81368068575213.m3u8";
		setContentView(R.layout.video_view_b);
		
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
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	if (mVideoView != null)
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
	super.onConfigurationChanged(newConfig);
	}

}



