package com.media.Util;

import java.io.IOException;

import com.constants.AppConstant;
import com.media.service.AudioPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class GetDuration {

	private Context mContext;
	private LinearLayout linearLayout;
	private OnCustomCallBack callBack;
	private RelativeLayout relativeLayout;

	public GetDuration(Context mContext, LinearLayout linearLayout,OnCustomCallBack callBack) {
		this.mContext = mContext;
		this.linearLayout = linearLayout;
		this.callBack = callBack;
		getVideoDuration();
	}

	public GetDuration(Context mContext, RelativeLayout relativeLayout, OnCustomCallBack callBack) {
		this.mContext = mContext;
		this.relativeLayout = relativeLayout;
		this.callBack = callBack;
		getVideoDuration();
	}
	
	public GetDuration(Context mContext, String path, OnCustomCallBack callBack){
		this.mContext = mContext;
		this.callBack = callBack;
		getVoiceDuration(path);
	}

	private void getVideoDuration() {

		final VideoView vv = new VideoView(mContext);
		vv.setLayoutParams(new LayoutParams(1, 1));
		if (relativeLayout != null)
			relativeLayout.addView(vv);
		else
			linearLayout.addView(vv);
		vv.setVideoPath(AppConstant.VIDEO_PATH);
		// long videoDuration = 0;
		vv.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.v("vv", "getDuration" + vv.getDuration());
				if (callBack != null) {
					callBack.back(vv.getDuration());
				}
			}
		});

	}

	private void getVoiceDuration(String path) {		
			MediaPlayer voicePlay = new MediaPlayer();
			Uri uri = Uri.parse(path);
			try {
				voicePlay.setDataSource(mContext, uri);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				voicePlay.prepare();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("音频时常", ""+voicePlay.getDuration());
			if (callBack != null) {
				callBack.back(voicePlay.getDuration());
			}
		
	}

	public interface OnCustomCallBack {
		public void back(long info);
	}
}
