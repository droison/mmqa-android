package com.qiniu.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.constants.AppConstant;
import com.media.httpservice.HttpDataService;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.resumable.BlkputRet;
import com.qiniu.resumable.ResumableIO;
import com.qiniu.resumable.RputExtra;
import com.qiniu.resumable.RputNotify;
import com.qiniu.utils.Utils;
import org.json.JSONObject;

public class QiniuUpload{

	public static final int PICK_PICTURE_RESUMABLE = 0;
	public static int i = 0;

	private String domain = "";
	// upToken 这里需要自行获取. SDK 将不实现获取过程.

	private Handler mHandler;
	private Context mContext;

	public QiniuUpload(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}
	
	public void videoUpload(String path,String UP_TOKEN, final CallbackByQiniuUpload callback){
		
		final String key = HttpDataService.getInstance(mContext).getAccount().getUsername() + System.currentTimeMillis() +".mp4";
		this.domain = AppConstant.Qiniu.domain_vid;
		Uri uri = Uri.parse("file://"+ path);
		ResumableIO.putFile(mContext, UP_TOKEN, key, uri, getPutExtra(uri, "video/mpeg", AppConstant.Qiniu.bucketName_vid), new JSONObjectRet() {
			@Override
			public void onSuccess(JSONObject resp) {
				String hash;
				try {
					hash = resp.getString("hash");
				} catch (Exception ex) {
					callback.back(false, "exception");
					return;
				}
				String url = domain + key;
				callback.back(true, url);
			}

			@Override
			public void onFailure(Exception ex) {
				callback.back(false, "exception");
			}
		});
	}
	
	public void imageUpload(List<String> paths,String UP_TOKEN, final CallbackByQiniuUpload callback){
		final Vector<String> imageUrls = new Vector<String>();
		final int len = paths.size();
		for(int i = 0;i<len ;i++){
			final String key = HttpDataService.getInstance(mContext).getAccount().getUsername() + System.currentTimeMillis() + i+ ".jpg";
			Uri uri = Uri.parse("file://"+ paths.get(i));
			ResumableIO.putFile(mContext, UP_TOKEN, key, uri, getPutExtra(uri, "audio/x-wav", AppConstant.Qiniu.bucketName_img), new JSONObjectRet() {
				@Override
				public void onSuccess(JSONObject resp) {
					String hash;
					try {
						hash = resp.getString("hash");
					} catch (Exception ex) {
						callback.back(false, "exception");
						return;
					}
					String url = AppConstant.Qiniu.domain_img + key;
					imageUrls.add(url);
					if(imageUrls.size() == len){
						callback.imageBack(true, imageUrls);
					}
					
				}

				@Override
				public void onFailure(Exception ex) {
					imageUrls.add("failure:exception");
					callback.imageBack(false, imageUrls);
				}
			});
		}
	}

	public void voiceUpload(String path,String UP_TOKEN, final CallbackByQiniuUpload callback){

		
		final String key = HttpDataService.getInstance(mContext).getAccount().getUsername() + System.currentTimeMillis() +".wav";
		this.domain = AppConstant.Qiniu.domain_aud;
		Uri uri = Uri.parse("file://"+ path);
		ResumableIO.putFile(mContext, UP_TOKEN, key, uri, getPutExtra(uri, "audio/x-wav", AppConstant.Qiniu.bucketName_aud), new JSONObjectRet() {
			@Override
			public void onSuccess(JSONObject resp) {
				String hash;
				try {
					hash = resp.getString("hash");
				} catch (Exception ex) {
					callback.back(false, "exception");
					return;
				}
				String url = domain + key;
				callback.back(true, url);
			}

			@Override
			public void onFailure(Exception ex) {
				callback.back(false, "exception");
			}
		});
	
	}
	
	public void imageVoiceUpload(List<String> paths,String IMAGE_TOKEN, String path,String VOICE_TOKEN, final CallbackByQiniuUpload callback){

		final Vector<String> imageUrls = new Vector<String>();
		final List<String> voiceUrls = new ArrayList<String>();
		final int len = paths.size();
		
		for(int i = 0;i<len ;i++){
			final String key = HttpDataService.getInstance(mContext).getAccount().getUsername() + System.currentTimeMillis() + i+ ".jpg";
			Uri uri = Uri.parse("file://"+ paths.get(i));
			ResumableIO.putFile(mContext, IMAGE_TOKEN, key, uri, getPutExtra(uri, "audio/x-wav", AppConstant.Qiniu.bucketName_img), new JSONObjectRet() {
				@Override
				public void onSuccess(JSONObject resp) {
					String hash;
					try {
						hash = resp.getString("hash");
					} catch (Exception ex) {
						callback.back(false, "exception");
						return;
					}
					String url = AppConstant.Qiniu.domain_img + key;
					imageUrls.add(url);
					if(imageUrls.size() == len && voiceUrls.size()==1){
						callback.imageVoiceBack(true, imageUrls,voiceUrls.get(0));
					}
					
				}

				@Override
				public void onFailure(Exception ex) {
					imageUrls.add("failure:exception");
					callback.imageBack(false, imageUrls);
				}
			});
		}
	
		voiceUpload:{
			Uri uri = Uri.parse("file://"+ path);
			final String key = HttpDataService.getInstance(mContext).getAccount().getUsername() + System.currentTimeMillis() + ".wav";
			ResumableIO.putFile(mContext, VOICE_TOKEN, key, uri, getPutExtra(uri, "audio/x-wav", AppConstant.Qiniu.bucketName_aud), new JSONObjectRet() {
				@Override
				public void onSuccess(JSONObject resp) {
					String hash;
					try {
						hash = resp.getString("hash");
					} catch (Exception ex) {
						callback.imageVoiceBack(false, null,"exception");
						return;
					}
					voiceUrls.add(AppConstant.Qiniu.domain_aud + key) ;
					if(imageUrls.size() == len && voiceUrls.size()==1){
						callback.imageVoiceBack(true, imageUrls,voiceUrls.get(0));
					}
				}

				@Override
				public void onFailure(Exception ex) {
					callback.imageVoiceBack(false, null,"onFailure:exception");
				}
			});
		}
		
	}
	
	private RputExtra getPutExtra(Uri uri, String mimeType, String bucketName) {
		final long fsize = Utils.getSizeFromUri(mContext, uri);

		RputExtra extra = new RputExtra(bucketName);
	//	extra.mimeType = mimeType;
		extra.notify = new RputNotify() {
			long uploaded = 0;
			@Override
			public synchronized void onNotify(int blkIdx, int blkSize, BlkputRet ret) {
				uploaded += blkSize;
				int progress = (int) (uploaded * 100 / fsize);
				mHandler.sendMessage(mHandler.obtainMessage(
						AppConstant.HANDLER_UPLOAD_VIDEO, progress));
				if(progress == 100){
					mHandler.sendEmptyMessage(AppConstant.HANDLER_UPLOAD_IMAGE_SUCCESS);
				}
			}
		};
		return extra;
	}
	
	public interface CallbackByQiniuUpload{
		public void back(Boolean state,String url);
		public void imageBack(Boolean state,List<String> imageUrl);
		public void imageVoiceBack(Boolean state,List<String> imageUrl,String voiceUrl);
	}
}
