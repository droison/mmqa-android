/**
 * 功能:将bitMap存为jpg图片;
 */
package com.media.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.constants.AppConstant;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class ImageSave {

	public ImageSave(Boolean isFront) throws IOException {

		Bitmap bm = createVideoThumbnail(AppConstant.VIDEO_PATH,isFront);

		//System.out.println(dirFile.exists());
		File myCaptureFile = new File(AppConstant.THUMB_PATH);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);// 三个参数， 图片格式/压缩比例/输出流
		bos.flush();
		bos.close();
	}

	/**
	 * 
	 * @param 该视频的地址filePath
	 *            = Environment.getExternalStorageDirectory().getPath()+
	 *            "/mmqa/cache/1.mp4"
	 * @return
	 * 
	 */

	@SuppressLint("NewApi")
	public Bitmap createVideoThumbnail(String filePath,Boolean isFront) {
		
		Bitmap bm = null;
		Bitmap resizeBmp = null;
		int curDegrees = 0; // 以当前为坐标
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);

			bm = retriever.getFrameAtTime(1);// 取帧，只能取第一帧

			/**
			 * 旋转bitmap   也可设置图片放大比例，
			 * 
			 */
			int bmpW = bm.getWidth();
			int bmpH = bm.getHeight();
			// 设置图片放大比例
			double scale = 1;
			// 计算出这次要放大的比例
			float scaleW = 1;
			float scaleH = 1;
			scaleW = (float) (scaleW * scale);
			scaleH = (float) (scaleH * scale);
			// 产生reSize后的Bitmap对象
			android.graphics.Matrix mt = new android.graphics.Matrix();
			// 设置位图缩放比例
			mt.postScale(scaleW, scaleH);
			// 设置位图旋转程度
			if(isFront){
			//	mt.setRotate(curDegrees = curDegrees - 90);// 逆时针旋转90度
				mt.setRotate(curDegrees);
			}else{
				mt.setRotate(curDegrees = curDegrees + 90);
			}
			
			// 设置例设置好的位图缩放比例与旋转程度改变位图
			resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);

		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		return resizeBmp;
	}

}
