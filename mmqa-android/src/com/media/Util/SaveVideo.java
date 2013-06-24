package com.media.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.constants.AppConstant;

import android.os.Environment;
import android.util.Log;


public class SaveVideo {
	private URL url;

	public SaveVideo(URL url) { 		
		this.url = url;

	}

	public void saveVideoToLocal(String name) throws Exception {

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            File file = new File(AppConstant.BASE_DIR_CACHE);
            if (!file.exists()) {
                file.mkdir();
            }
            File videoFile = new File(file, name+".mp4");
            FileOutputStream fos = new FileOutputStream(videoFile);

            byte buf[] = new byte[1024];
            int numread = 0;
            
            while( (numread=is.read(buf)) != -1 ){
            	 fos.write(buf, 0, numread);
    		}
            
            fos.close();
            is.close();
        

    }

}
