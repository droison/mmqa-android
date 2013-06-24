package com.media.service;

import java.io.IOException;

import com.media.Activity.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AudioPlayer {

	private static AudioPlayer instance;
	public static MediaPlayer voicePlay;
	public static ImageView button;
	private AudioPlayer(){
		
	}
	public static AudioPlayer getInstance(){
		if(instance == null)
		{
			return new AudioPlayer();
		}else{
			if(voicePlay.isPlaying())
				voicePlay.stop();
			return instance;
		}
	}

	public MediaPlayer getPlayer(Context mContext, String path){
		if(voicePlay != null)
		{
			voicePlay.stop();
			voicePlay.release();
		}
		voicePlay = new MediaPlayer();	
		audioPrepare(path, mContext);
		return voicePlay;
	}
	
	public MediaPlayer getPlayer(Context mContext,ImageView button, String path){
		if(voicePlay != null)
		{
			this.button.setImageResource(R.drawable.feed_main_player_play);
			voicePlay.stop();
			voicePlay.release();
		}
		this.button = button;
		voicePlay = new MediaPlayer();	
		audioPrepare(path, mContext);
		return voicePlay;
	}
	
	public void audioPrepare(String path, Context mContext){
		Uri uri = Uri.parse(path);
		try {
			voicePlay.setDataSource(mContext, uri);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			voicePlay.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("音频时常", ""+voicePlay.getDuration());
		
	}
}
