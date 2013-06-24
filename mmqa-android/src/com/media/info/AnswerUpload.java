package com.media.info;

import java.io.File;
import java.util.List;

public class AnswerUpload {

	private String questionId, userId, duration, encode, fileType, cameraInfo,
			content;
	private String videoPath, thumbPath, voicePath;
	private List<String> imagePath;
	private int voiceDuration;

	public int getVoiceDuration() {
		return voiceDuration;
	}

	public void setVoiceDuration(int voiceDuration) {
		this.voiceDuration = voiceDuration;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	// i值： 21：回答+视频+图片，22：回答+图片，23：回答+图片+语音，24：回答+语音，25：回答+文字
	public boolean checkCompletion(int i) {
		switch (i) {
		case 21:
			return duration != null && encode != null && fileType != null
					&& cameraInfo != null && videoPath != null
					&& thumbPath != null && questionId != null
					&& userId != null;
		case 22:
			return imagePath != null && questionId != null && userId != null;
		case 23:
			return voicePath != null && imagePath != null && questionId != null
					&& userId != null;
		case 24:
			return voicePath != null && questionId != null && userId != null;
		case 25:
			return content != null && questionId != null && userId != null;
		default:
			return false;
		}
	}

}
