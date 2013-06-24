package com.media.Activity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.media.Util.SaveVideo;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class VideoViewBActivity extends AbstractMMQAActivity{


	private String path = Environment.getExternalStorageDirectory().getPath()
			+ "/mmqa/cache/1.mp4";
	private VideoView mVideoView;
	private SaveVideo saveVideo;
	private String videoId;
	private String videoUrl;
	private Button buttonClose;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Bundle bundle = this.getIntent().getExtras();
		
		if(bundle.getString("local_video")!=null){
			
			videoId = bundle.getString("local_video");
			System.out.println(videoId);
			path = Environment.getExternalStorageDirectory().getPath()
					+ "/mmqa/temp/"+videoId+".mp4";
			System.out.println(path);
		}else{
			videoId = bundle.getString("videoId");
			videoUrl = bundle.getString("videoUrl");
			path = Environment.getExternalStorageDirectory().getPath()
					+ "/mmqa/cache/"+videoId+".mp4";
		}
		
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.video_view_b);
		
		buttonClose = (Button)findViewById(R.id.buttonClose);
		
		mVideoView = (VideoView) findViewById(R.id.surface_view);

				
		buttonClose.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mVideoView.pause();
						finish();
					}
				});
		
		
		if(new File(path).isFile()){
			mVideoView.setVideoPath(path);
			mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		//	mVideoView.setMediaController(new MediaController(VideoViewBActivity.this));
		}else{
			try {
				URL url = new URL(videoUrl);
				new DownloadFilesTask().execute(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		
		
	}
	
	class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
		@Override
		protected Long doInBackground(URL... urls) {
			long totalSize = 0;
			try {
				saveVideo = new SaveVideo(urls[0]);
				saveVideo.saveVideoToLocal(videoId);
				Log.e("xiazai", path);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				Log.e("MalformedURLException", e.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Exception", e.toString());
			}
			return totalSize;
		}

		@Override
		protected void onPreExecute() {
			showProgressDialog("正在下载，请稍等...");
		}

		@Override
		protected void onPostExecute(Long result) {

			dismissProgressDialog();
			mVideoView.setVideoPath(path);
			mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		//	mVideoView.setMediaController(new MediaController(VideoViewBActivity.this));
		}
	}
}