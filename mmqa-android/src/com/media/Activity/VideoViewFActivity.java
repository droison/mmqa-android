package com.media.Activity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.media.Util.SaveVideo;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

public class VideoViewFActivity extends AbstractMMQAActivity{

	private Button buttonClose;
	private String videoUrl;
	private SaveVideo saveVideo;
	private String videoId;
	private String path;
	private VideoView vv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 实现全屏播放
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(8);
		setContentView(R.layout.video_view_f);
		super.onCreate(savedInstanceState);

		buttonClose = (Button) findViewById(R.id.buttonClose);

		vv = (VideoView) findViewById(R.id.surface_view);

		Bundle bundle = this.getIntent().getExtras();
		videoId = bundle.getString("videoId");
		videoUrl = bundle.getString("videoUrl");	

		path = Environment.getExternalStorageDirectory().getPath()
				+ "/mmqa/cache/"+videoId+".mp4";
	


		vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.v("useVideoView", "onCompletion");
				finish();
			}
		});

		buttonClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vv.pause();
				finish();
			}
		});
		
		if(new File(path).isFile()){
			vv.setVideoPath(path);
			
			vv.setOnPreparedListener(new OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mp) {

                        Log.v("v","getDuration"+vv.getDuration());
                }
        });

			
			vv.start();

		}else{
			try {
				URL url = new URL(videoUrl);
				new DownloadFilesTask().execute(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (what) {

				case MediaPlayer.MEDIA_ERROR_UNKNOWN:
					Log.v("useVideoView", "MEDIA_ERROR_UNKNOWN");
					return true;

				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					Log.v("useVideoView", "MEDIA_ERROR_SERVER_DIED");
					return true;

				case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
					Log.v("useVideoView",
							"MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
					return true;
				}
				return false;
			}
		});
	}
	
	class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
		@Override
		protected Long doInBackground(URL... urls) {
			long totalSize = 0;
			try {
				saveVideo = new SaveVideo(urls[0]);
				saveVideo.saveVideoToLocal(videoId);
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
			vv.setVideoPath(path);
			vv.start();
		}
	}
	
}