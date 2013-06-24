package com.media.info;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionUpload {
	private String userId, title, duration, encode, fileType, cameraInfo, userName;


	private List<String> tags = new ArrayList<String>();
	private String videoPath, thumbPath, voicePath;
	private List<String> imagePath;
	private int voiceDuration;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getVoiceDuration() {
		return voiceDuration;
	}

	public void setVoiceDuration(int voiceDuration) {
		this.voiceDuration = voiceDuration;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
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

	public String getCameraInfo() {
		return cameraInfo;
	}

	public void setCameraInfo(String cameraInfo) {
		this.cameraInfo = cameraInfo;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public List<String> getImagePath() {
		return imagePath;
	}

	public void setImagePath(List<String> imagePath) {
		this.imagePath = imagePath;
	}

	public String getVoicePath() {
		return voicePath;
	}

	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}

	// i值： 11：问题+视频+图片，12：问题+图片，13：问题+图片+语音，14：问题+语音
	public boolean checkCompletion(int i) {
		switch (i) {
		case 11:
			return tags.size() != 0 && duration != null && encode != null && fileType != null && cameraInfo != null && videoPath != null && imagePath != null && title != null && userId != null;
		case 12:
			return tags.size() != 0 && imagePath != null && title != null && userId != null;
		case 13:
			return tags.size() != 0 && voicePath != null && imagePath != null && title != null && userId != null;
		case 14:
			return tags.size() != 0 && voicePath != null && title != null && userId != null;
		case 15:
			return userName != null;
		default:
			return false;
		}
	}

}
