
package com.media.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;
import com.media.Thread.ThreadExecutor;
import com.media.httpservice.HTTP;
import com.media.httpservice.HttpResponseEntity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * author chaisong
 * 
 * 类说明，图片异步加载
 * */
public class AsyncImageLoader {

    private static final String TAG = "AsyncImageLoader";

    private HashMap<String, SoftReference<Drawable>> imageCache;

    private BlockingQueue<SoftReference<Drawable>> queue;

    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();

        queue = new LinkedBlockingQueue<SoftReference<Drawable>>();
    }

    public Drawable loadDrawable(final Context context, final String imageUrl,
            final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable)message.obj, imageUrl);
            }
        };

        ThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(context, imageUrl);
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        });

        return null;
    }

    public static Drawable loadImageFromUrl(Context context, String imageUrl) {
        Drawable drawable = null;
        if (imageUrl == null)
            return null;
        String imagePath = "";
        String fileName = "";

        if (imageUrl != null && imageUrl.length() != 0) {
            fileName = imageUrl.substring(imageUrl.lastIndexOf(".com/") + 5,imageUrl.lastIndexOf(".")-1) + ".jpg";
        }

        imagePath = context.getCacheDir() + "/" + fileName;

        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists() && !file.isDirectory()) {
            try {

                FileOutputStream fos = new FileOutputStream(file);
                HttpResponseEntity hre = HTTP.get(imageUrl);

                byte[] is = null;
                if(hre != null){
                	if (hre.getHttpResponseCode() == HttpStatus.SC_OK)
                	{
                		is = hre.getB();
            			for(int i=0;i<is.length;i++){
            				fos.write(is[i]);
            			}
                	}
                }			
                fos.close();
                drawable = Drawable.createFromPath(file.toString());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            drawable = Drawable.createFromPath(file.toString());
        }
        return drawable;
    }

    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }

}
