package com.media.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class FIleUploadUtil{
	
	 private String url = null;
	    /**
	     * 要提交的文件.
	     */
	    private List<File> files = null;
	 
	    /**
	     * 构造方法。
	     * 
	     * @param url
	     *            这个URL必须是get提交方式的URL，即这个URL不能带有任何参数信息。eg：'http://<span style="color: #000000;">localhost</span>:8080/FileUploadServer/file/upload.do'<br>     */
	    public FIleUploadUtil(String url) {
	        this.url = url;
	        files = new ArrayList<File>();
	 
	    }
	 
	    /**
	     * 通过这个方法来添加要提交的文件。
	     * 
	     * 
	     * @param file
	     *            提交的文件,如果文件为空或者不存在或者不可读，则不提交这个文件，重复的文件只提交一次。
	     */
	    public void addFile(File file) {
	        if (file == null || !file.exists() || !file.canRead()) {
	            return;
	        } else {
	            for (int i = 0; i < files.size(); i++) {
	                if (file.getPath().equalsIgnoreCase(files.get(i).getPath())) {
	                    return;
	                }
	            }
	            files.add(file);
	        }
	    }
	 
	    /**
	     * 提交的方法，该方法为每个文件创建一个请求连接进行提交。
	     * 
	     * @throws Exception
	     */
	    public void upload() throws Exception {
	        for (int i = 0; i < files.size(); i++) {
	            HttpClient httpClient = new DefaultHttpClient();
	            try {
	                FileEntity entity = new FileEntity(files.get(i),
	                        "multipart/form-data");
	                StringBuilder curUrl = new StringBuilder(url);
	                curUrl.append("?fileName=" + files.get(i).getName());
	                HttpPost httppost = new HttpPost(curUrl.toString());
	                httppost.setEntity(entity);
	 
	                HttpResponse response = httpClient.execute(httppost);
	                int resultCode = response.getStatusLine().getStatusCode();
	                if (resultCode != HttpStatus.SC_OK) {
	                    throw new Exception("上传文件" + files.get(i).getPath()
	                            + "失败.错误代码是：" + resultCode + ";原因描述是："
	                            + response.getStatusLine().getReasonPhrase());
	                }
	            } finally {
	                if (httpClient != null
	                        && httpClient.getConnectionManager() != null) {
	                    httpClient.getConnectionManager().shutdown();
	                }
	            }
	        }
	    }
	}