package com.media.Activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.constants.AppConstant;
import com.media.httpservice.HttpDataService;
import com.media.info.AnswerUpload;
import com.media.info.QuestionUpload;
import com.media.mtj.BaiduSDK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoActivity extends BaiduSDK implements OnClickListener{

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Button buttonCtrl;
	private Button buttonSwitchVideo;
	private TextView title;
	private Button left_button;

	private String cameraInfo;
	private Boolean isFront = false;
	private String duration;
	private MediaRecorder recorder;
	private Camera mCamera;
	private int cameraId;
	private int cameraCurrentId;
	private int numCamera;
	private List<Size> mSupportedPreviewSizes;
	private boolean isQuestion;
	private String questionId;
	private float durationTime;
	private boolean flag = true;
	private Bundle bundle;

	private boolean isPreview = false;// 是否在浏览中

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		initData();
		
		setContentView(R.layout.video);

		setUpView();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int widthSize = dm.widthPixels; // 使用DisplayMetrics获取屏幕高的像素
		int tempSize = widthSize * 4 / 3; // 获取临时的预览框尺寸

		mSurfaceHolder.setKeepScreenOn(true);
		mSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(tempSize, widthSize));
//		mSurfaceHolder.setFixedSize(tempSize, widthSize);

		numCamera = Camera.getNumberOfCameras();
		CameraInfo info = new CameraInfo();
		for (int i = 0; i < numCamera; i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
			}
		}
		if (numCamera == 1) {
			cameraCurrentId = cameraId;
			buttonSwitchVideo.setVisibility(View.GONE);
		} else {
			cameraCurrentId = cameraId;
		}

		mSurfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 打开摄像头
				initCamera(holder);
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (mCamera != null) {
					if (isPreview)
						mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
			}
		});

		left_button.setOnClickListener(this);

		buttonCtrl.setOnClickListener(this);

		buttonSwitchVideo.setOnClickListener(this);
	}
	
	private void initData(){
		
		File dir = new File(AppConstant.BASE_DIR_PATH);
		if (!dir.exists())
			dir.mkdirs();
		
		
		bundle = this.getIntent().getExtras();
		
		if(android.os.Build.VERSION.SDK_INT <= 9 || android.os.Build.VERSION.SDK_INT > 16){
			Intent toNativeCamera = new Intent(this,NativeCameraActivity.class);
			toNativeCamera.putExtras(bundle);
			startActivity(toNativeCamera);
			finish();
		}
		
		isQuestion = bundle.getBoolean("isQuestion");
		questionId = bundle.getString("questionId");
	}
	
	private void cameraInitialization(){
		
	}
	
	private void setUpView(){
		mSurfaceView = (SurfaceView) findViewById(R.id.videoView);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // 设置该SurfaceView自己不维护缓冲
		buttonCtrl = (Button) findViewById(R.id.ctrl);
		buttonSwitchVideo = (Button) findViewById(R.id.switchVideo);
		left_button = (Button) findViewById(R.id.left_button);
		title = (TextView) findViewById(R.id.title);
		title.setText("视频录制");
	}

	public void recorder() {
		try {
			releaseMediaRecorder();
			recorder = new MediaRecorder();

			mCamera.unlock();          // 要在MediaRecorder设置参数之前就调用unlock来获得camera的控制权
			recorder.setCamera(mCamera);
	
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//

			recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//
			recorder.setVideoSize(640, 480);//

			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//
			recorder.setMaxDuration(30000);//
			recorder.setPreviewDisplay(mSurfaceHolder.getSurface());//
			recorder.setOutputFile(AppConstant.VIDEO_PATH);//
			// recorder.setVideoFrameRate(15);//
			recorder.prepare();
			recorder.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFileTime() { // 获取String型系统时间
		Date curTime = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = df.format(curTime).toString();
		return time;
	}

	private void initCamera(SurfaceHolder holder) {
		if (!isPreview) {

			mCamera = Camera.open(cameraCurrentId);
		}
		if (mCamera != null && !isPreview) {
			try {
				// Camera.Parameters parameters = mCamera.getParameters();

				// camera.setParameters(parameters); //android2.3.3以后无需下步
				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(holder); // 通过SurfaceView显示取景画面

				mCamera.startPreview(); // 开始预览

			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
		mCamera.autoFocus(null);
	}

	public void switchCamera(Camera camera) {
		if (numCamera > 1) {// 非一个摄像头
			setCamera(camera);
			try {
				camera.setPreviewDisplay(mSurfaceHolder);
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
			}
			// Camera.Parameters parameters = camera.getParameters();
			mCamera.setDisplayOrientation(90);
			mSurfaceView.requestLayout();
			// camera.setParameters(parameters);
		}

	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();// 获取硬件所支持的尺寸
			mSurfaceView.requestLayout();
		}
	}

	class MyThread implements Runnable {
		@Override
		public void run() {
			while (durationTime!=2) {
				durationTime = durationTime + (float) 0.5;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("视频时间", String.valueOf(durationTime));
			}
		}

	}
	
	private void releaseMediaRecorder()
    {
        if (recorder != null)
        {
          
            // 再次尝试停止MediaRecorder
            try
            {
            	recorder.stop();
            }
            catch (Exception e)
            {
                Log.e("CAMERA STOP", "stop fail2", e);
            }
           
            // 等待，让停止彻底执行完毕
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e1)
            {
                Log.e("TAG", "sleep for reset error Error", e1);
            }
           
            // 然后再进行reset、release
            recorder.reset();
            recorder.release();
           
            recorder = null;
        }
    }

	private int temp = 0;
	private long timeStampe = 0;
	MyThread m = new MyThread();
	final Thread countTime = new Thread(m);
	
	@Override
	public void onClick(View v) {
		
		
		switch (v.getId()) {
		case R.id.switchVideo:
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			mCamera = Camera.open((cameraCurrentId + 1) % numCamera);
			cameraCurrentId = (cameraCurrentId + 1) % numCamera;
			switchCamera(mCamera);
			mCamera.startPreview();
			isFront = !isFront;
			break;
		case R.id.ctrl:
			if (timeStampe != 0 && System.currentTimeMillis() - timeStampe < 5000){
				return;
			}else{
				timeStampe = System.currentTimeMillis();
			}
			switch (temp) {
			case 0:
				recorder();
				mCamera.setPreviewCallback(new PreviewCallback() {
					
					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
						if(durationTime==1){
							 Size size = camera.getParameters().getPreviewSize();           
							    try{  
							        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);   
							        if(image!=null){
							        	
							            ByteArrayOutputStream outS = new ByteArrayOutputStream();
							            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 30, outS);
							            InputStream is = new ByteArrayInputStream(outS.toByteArray());
							            Bitmap bm = BitmapFactory.decodeStream(is);
							            Matrix matrix = new Matrix();
							            matrix.setRotate(isFront?-90:90);
							            //旋转图像，并生成新的Bitmap对像  
							            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
							            File f1 = new File(AppConstant.THUMB_PATH);
							            FileOutputStream outstream = new FileOutputStream(f1);
							            bm.compress(Bitmap.CompressFormat.JPEG, 80, outstream);
							            outstream.flush();  
							            outS.flush();
							            is.close();
							        }   
							    }catch(Exception ex){   
							        Log.e(TAG,"savePic:"+ex.getMessage());   
							    }   
						}
						}
				});
				buttonCtrl.setText("停止录制");
				countTime.start();
				buttonSwitchVideo.setClickable(false);
				temp = 1;
				break;
			case 1:
				flag = false;
				recorder.stop();
				recorder.reset();
				recorder.release();
				recorder = null;
				buttonCtrl.setText("开始录制");
				temp = 0;
				
				//返回界面传递参数
				setResult(RESULT_OK);
				
				finish();		
				if (!isQuestion) {
					AnswerUpload au = HttpDataService.getInstance(VideoActivity.this).getAnswerUpload();
					au.setUserId(HttpDataService.getInstance(VideoActivity.this).getAccount().getId());
					au.setQuestionId(questionId);
					au.setCameraInfo(isFront == true ? "android.front" : "android.behind");
					au.setEncode("H.264");
					au.setFileType("MP4");				
					au.setVideoPath(AppConstant.VIDEO_PATH);
					au.setThumbPath(AppConstant.THUMB_PATH);
					Intent uploadAnswer = new Intent(getApplicationContext(), UploadActivity.class);
					startActivity(uploadAnswer);
				}else{
					QuestionUpload qu = HttpDataService.getInstance(VideoActivity.this).getQuestionUpload();
					qu.setUserId(HttpDataService.getInstance(VideoActivity.this).getAccount().getId());
					qu.setCameraInfo(isFront == true ? "android.front" : "android.behind");
					qu.setEncode("H.264");
					qu.setFileType("MP4");
					qu.setVideoPath(AppConstant.VIDEO_PATH);
					qu.setThumbPath(AppConstant.THUMB_PATH);
				}
				break;
			}	
			
			break;
		case R.id.left_button:
			finish();
			break;
		default:
			break;
		}
		
	}
}
