package com.media.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {

		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > reqWidth) {
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 1080);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	public static void bitmapCompress(String filePath) {

		Bitmap bm = getSmallBitmap(filePath);
		OutputStream ous = null;
		try {
			ous = new FileOutputStream(new File(filePath));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, ous);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ous != null) {
				try {
					ous.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static void bitmapCompress(String inputPath,String output) {

		Bitmap bm = getSmallBitmap(inputPath);
		OutputStream ous = null;
		try {
			ous = new FileOutputStream(new File(output));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, ous);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ous != null) {
				try {
					ous.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
