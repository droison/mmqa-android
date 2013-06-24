package com.media.info;

/**
 * Q&A视频信息
 * 
 * @author suzhj
 * 
 */

public class Video extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6210081498407784018L;
	private String filePath;
	private String duration;
	private String fileSize;
	private String encode;
	private String fileType;
	private String mimeType;
	private String videoUrl;
	private String imageUrl;
	private String cameraInfo; // 格式为"操作系统.摄像头位置"，包括android.front,android.behind,iphone.front,iphone.behind

	
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getCameraInfo() {
		return cameraInfo;
	}

	public void setCameraInfo(String cameraInfo) {
		this.cameraInfo = cameraInfo;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	
}
