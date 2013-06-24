package com.media.Activity;

import java.io.File;
import java.net.URL;

import com.constants.AppConstant;
import com.media.Util.AsyncImageLoader;
import com.media.Util.AsyncImageLoader.ImageCallback;
import com.media.Util.ImageUtil;
import com.media.Util.SaveImage;
import com.media.info.Question;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageActivity extends AbstractMMQAActivity {

	private URL url;
	private String imageUrl;
	private String imageId;
	private SaveImage saveImage;
	private MyHandler handler;
	private ImageView iv;
	private String filePath;
	private final static int SAVE_OK = 1;
	private Bitmap bitmap;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);
		iv = (ImageView) findViewById(R.id.image01);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		Bundle bundle = this.getIntent().getExtras();
		/** 判断是哪里跳转过来的，如果是上传前预览跳转的执行if中语句 */
		if(bundle.getString("imagePath")!=null){
			filePath = bundle.getString("imagePath");
			File file = new File(filePath);
			bitmap = ImageUtil.getSmallBitmap(filePath);
			iv.setImageBitmap(bitmap);
			progressBar.setVisibility(View.GONE);
		}
		else{
			imageId = bundle.getString("imageId");
			filePath = AppConstant.BASE_DIR_CACHE + "/"
					+ imageId + ".jpg";
			File file = new File(filePath);
			if (file.exists()) {
				bitmap = ImageUtil.getSmallBitmap(filePath);
				iv.setImageBitmap(bitmap);
				progressBar.setVisibility(View.GONE);
			} else {
				imageUrl = bundle.getString("imageUrl");
				try {
					url = new URL(imageUrl);
	
				} catch (Exception e) {
					// TODO: handle exception
				}
	
				handler = new MyHandler();
				MyThread myThread = new MyThread();
				new Thread(myThread).start();
	
			}
		}
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	class MyHandler extends Handler {


		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				bitmap = BitmapFactory.decodeFile(filePath);
				iv.setImageBitmap(bitmap);
				progressBar.setVisibility(View.GONE);
			}

		}

	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msg = handler.obtainMessage();

			try {
				saveImage = new SaveImage(url);
				saveImage.saveVideoToLocal(imageId + ".jpg");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.what = ImageActivity.SAVE_OK;
			ImageActivity.this.handler.sendMessage(msg);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	//	iv.getBackground().setCallback(null);
		iv.setImageBitmap(null);
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			}
			System.gc();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			}
			System.gc();
	}
	
}
