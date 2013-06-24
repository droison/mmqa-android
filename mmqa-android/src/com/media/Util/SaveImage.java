package com.media.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Log;

import com.constants.AppConstant;

public class SaveImage {

	private URL url;
	private Bitmap bitmap;

	public SaveImage(URL url) {
		this.url = url;

	}

	public SaveImage(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void savePortrait() {
		File file = new File(AppConstant.BASE_DIR_CACHE);
		if (!file.exists()) {
			file.mkdir();
		}
		File userImage = new File(AppConstant.USER_IMAGE);
		try {
			userImage.createNewFile();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("CActivity", "保存图片出错");
		}
		// 定义文件输出流
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(userImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 将bitmap存储为jpg格式的图片
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			// 刷新文件流
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// 关闭文件流
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveVideoToLocal(String name) throws Exception {

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		int length = conn.getContentLength();
		InputStream is = conn.getInputStream();

		File file = new File(AppConstant.BASE_DIR_CACHE);
		if (!file.exists()) {
			file.mkdir();
		}
		File imageFile = new File(file, name);
		FileOutputStream fos = new FileOutputStream(imageFile);

		byte buf[] = new byte[1024];
		int numread = 0;

		while ((numread = is.read(buf)) != -1) {
			fos.write(buf, 0, numread);
		}

		fos.close();
		is.close();

	}
}
