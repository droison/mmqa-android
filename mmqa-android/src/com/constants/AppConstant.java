package com.constants;

import java.io.File;

import android.os.Environment;

public class AppConstant {
	public static final String UPLOAD_MESSAGE_ACTION = "com.upload.uploadprogressbar";
	public static final String UPLOAD_SUCCESS_MESSAGE = "com.upload.success";
	public static final String UPLOAD_ERROR_MESSAGE = "com.upload.error";
	public final static String serverUrl = "http://218.249.255.29:9080/nesdu-webapp/api/"; //公网服务器地址
//	public final static String serverUrl = "http://192.168.8.112:8080/nesdu-webapp/api/"; //本机地址
//	public final static String serverUrl = "http://59.64.176.73:8080/nesdu-webapp/api/"; // 网院
		
	/** SD卡文件夹地址 */
	public static final String BASE_DIR_CACHE = Environment
			.getExternalStorageDirectory() + File.separator + "mmqa" + File.separator + "cache";// 北邮内网服务器地址
	/** SD卡文件夹地址 */
	public static final String BASE_DIR_PATH = Environment
			.getExternalStorageDirectory() + File.separator + "mmqa" + File.separator + "temp";
	/** wav文件地址 */
	public static final String VOICE_PATH = BASE_DIR_PATH + File.separator + "mmqa.wav";
	/** mp4文件地址 */
	public static final String VIDEO_PATH = BASE_DIR_PATH + File.separator + "mmqa.mp4";
	/** jpg卡文件地址 */
	public static final String THUMB_PATH = BASE_DIR_PATH + File.separator + "mmqa.jpg";
	/** jpg卡文件地址 */
	public static final String IMAGE1_PATH = BASE_DIR_PATH + File.separator + "image1.jpg";
	/** jpg卡文件地址 */
	public static final String IMAGE2_PATH = BASE_DIR_PATH + File.separator + "image2.jpg";
	/** jpg卡文件地址 */
	public static final String IMAGE3_PATH = BASE_DIR_PATH + File.separator + "image3.jpg";
	/** jpg卡文件地址 */
	public static final String IMAGE4_PATH = BASE_DIR_PATH + File.separator + "image4.jpg";
	/** jpg卡文件地址 */
	public static final String IMAGE5_PATH = BASE_DIR_PATH + File.separator + "image5.jpg";
	/** jpg卡文件地址 */
	public static final String TEMP_PATH = BASE_DIR_PATH + File.separator + "imageTemp.jpg";
	/**头像文件*/
	public static final String USER_IMAGE = BASE_DIR_PATH + File.separator + "protrait.jpg";
 
	/** APK文件名 */
	public static final String APK_NAME = "mmqa.apk";
	
	/** 拍摄图片的返回ID */
	public static final int TAKE_PHOTO_ID = 0x1991;
	/** 取图片返回ID */
	public static final int TAKE_PIC_ID = 0x1992;
	/** 取图片返回ID */
	public static final int TAKE_VIDEO_ID = 0x1993;
	/** 取语音图片中图片返回ID */
	public static final int TAKE_IAMGE_IV_ID = 0x1994;
	/** 取语音图片中拍照返回ID */
	public static final int TAKE_CAMERA_IV_ID = 0x1996;
	/** 取语音图片中语音返回ID */
	public static final int TAKE_VOICE_IV_ID = 0x1995;

	/** handler各种错误TAG */
	public static final int HANDLER_HTTPSTATUS_ERROR = 1002;

	public static final int HANDLER_MESSAGE_NULL = 1001;

	public static final int HANDLER_MESSAGE_NORMAL = 1000;

	public static final int HANDLER_PUBLISH_SUCCESS = 1003;
	
	public static final int HANDLER_VERSION_UPDATE = 2001;

	public static final int HANDLER_APK_DOWNLOAD_PROGRESS = 2002;

	public static final int HANDLER_APK_DOWNLOAD_FINISH = 2003;

	public static final int HANDLER_UPLOAD_IMAGE = 3001;

	public static final int HANDLER_UPLOAD_VIDEO = 3002;

	public static final int HANDLER_UPLOAD_IMAGE_SUCCESS = 3003;

	public static final int HANDLER_UPLOAD_VIDEO_SUCCESS = 3004;
	
	public static final int PAGESIZE = 10;
	
	public static final int QVIDEOViewType = 11;
	public static final int AVIDEOViewType = 21;
	public static final int QIMAGEViewType = 12;
	public static final int AIMAGEViewType = 22;
	public static final int QVOICEViewType = 14;
	public static final int AVOICEViewType = 24;
	public static final int QIMAGEVOICEViewType = 13;
	public static final int AIMAGEVOICEViewType = 23;
	public static final int ATEXTViewType = 25;
		
	public interface Qiniu{
		public static final String bucketName_img = "mmqa-img";
		public static final String bucketName_aud = "mmqa-aud";
		public static final String bucketName_vid = "mmqa-vid";
		public static final String domain_img = "http://mmqa-img.qiniudn.com/";
		public static final String domain_aud = "http://mmqa-aud.qiniudn.com/";
		public static final String domain_vid = "http://mmqa-vid.qiniudn.com/";
	}
}
