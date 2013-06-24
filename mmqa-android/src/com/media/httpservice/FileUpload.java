package com.media.httpservice;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.constants.AppConstant;

public class FileUpload implements Runnable {

	private static final String TAG = "FileUpload";

	private Handler mHandler = null;
	private String urlPath, filePath;
	private boolean isVideo;
	private int uploadSize;
	private Context context;

	public FileUpload(Context context, Handler mHandler, String urlPath,
			String filePath, boolean isVideo) {
		this.context = context;
		this.filePath = filePath;
		this.mHandler = mHandler;
		this.isVideo = isVideo;
		this.urlPath = urlPath;
		Log.d(TAG, "创建实例");
	}

	@Override
	public void run() {

		try {
			String end = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";

			URL url = new URL(urlPath);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (isVideo) {
				con.setChunkedStreamingMode(256 * 1024);
			}
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			con.setRequestMethod("POST");

			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\""
					+ (isVideo ? "1.mp4" : "1.jpg") + "\"" + end);
			ds.writeBytes(end);

			FileInputStream fStream = new FileInputStream(filePath);

			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			uploadSize = 0;
			int temp = 0;
			long fileSize = (new File(filePath)).length();
			int downloadCount = 0;

			while ((temp = fStream.read(buffer)) != -1) {

				ds.write(buffer, 0, temp);
				uploadSize += temp;
				int tmp = (int) (100 * uploadSize / fileSize);

				if (downloadCount == 0 || tmp - 5 > downloadCount) {

					downloadCount += 5;

					mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_UPLOAD_VIDEO, downloadCount));
				}
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			fStream.close();
			ds.flush();

			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}		
			if (isVideo) {
				mHandler.sendMessage(mHandler.obtainMessage(AppConstant.HANDLER_UPLOAD_VIDEO, 100));
				Message msg = new Message();
				msg.what = AppConstant.HANDLER_UPLOAD_VIDEO;
				msg.what = AppConstant.HANDLER_UPLOAD_VIDEO_SUCCESS;
				msg.obj = b.toString();
				mHandler.sendMessage(msg);
			} else {
				mHandler.sendEmptyMessage(AppConstant.HANDLER_UPLOAD_IMAGE_SUCCESS);
			}
			ds.close();
			con.disconnect();
		} catch (Exception e) {
			Log.e(TAG, "上传", e);
			Intent intent = new Intent();
			intent.setAction(AppConstant.UPLOAD_ERROR_MESSAGE);
			context.sendBroadcast(intent);
			e.printStackTrace();
		}

	}

}
