package com.media.httpservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.constants.AppConstant;
import com.media.Thread.ThreadExecutor;
import com.media.info.Account;
import com.media.info.Answer;
import com.media.info.AnswerUpload;
import com.media.info.Image;
import com.media.info.Question;
import com.media.info.QuestionUpload;
import com.qiniu.conf.Conf;
import com.qiniu.demo.QiniuUpload;
import com.qiniu.demo.QiniuUpload.CallbackByQiniuUpload;

public class QiNiuFileUpload {

	private static final String TAG = "FileUpload";

	private Handler mHandler = null;
	private List<String> imagePath = new ArrayList<String>();
	private String videoPath;
	private String voicePath;
	private boolean isVideo;
	private int uploadSize;
	private Context context;
	private QuestionUpload qUpload;
	private AnswerUpload aUpload;

	private int uploadType; // 11：问题+视频+图片，12：问题+图片，13：问题+图片+语音，14：问题+语音，15：头像
							// 21：回答+视频+语音，22：回答+图片，23：回答+图片+语音，24：回答+语音，25：回答+文字

	public QiNiuFileUpload(Context context, Handler mHandler, Object o, int uploadType) {
		this.context = context;
		this.mHandler = mHandler;
		this.uploadType = uploadType;
		if (uploadType > 20) {
			aUpload = (AnswerUpload) o;
			imagePath = aUpload.getImagePath();
			videoPath = aUpload.getVideoPath();
			voicePath = aUpload.getVoicePath();
		}

		else {
			qUpload = (QuestionUpload) o;
			imagePath = qUpload.getImagePath();
			videoPath = qUpload.getVideoPath();
			voicePath = qUpload.getVoicePath();
		}

		Log.d(TAG, "创建实例");
	}

	private boolean checkData() {
		if (uploadType > 20)
			return aUpload.checkCompletion(uploadType);
		else
			return qUpload.checkCompletion(uploadType);
	}

	public void run() {
		if (!checkData())
			return;
		final String uploadUrl = getServerUrl();
		String[] token = HttpDataService.getInstance(context).getToken();
		QiniuUpload qiniuU = new QiniuUpload(context, mHandler);

		switch (uploadType) {
		case 11:

			qiniuU.videoUpload(videoPath, token[0], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					if (state) {
						Question question = new Question();
						question.setTitle(qUpload.getTitle());
						question.setViewType(1);
						question.setTags(qUpload.getTags());
						
						com.media.info.Video video = new com.media.info.Video();
						video.setCameraInfo(qUpload.getCameraInfo());
						video.setDuration(qUpload.getDuration());
						video.setEncode(qUpload.getEncode());
						video.setVideoUrl(url);
						video.setImageUrl(url + "?" + Conf.VIDEO_THUMB );
						video.setFileType(qUpload.getFileType());
						question.setVideo(video);
						
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, question));
					} else {
						Log.e("qiniuUpload", url);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {
					// TODO Auto-generated method stub

				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case 21:

			qiniuU.videoUpload(videoPath, token[0], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					if (state) {
						Answer answer = new Answer();
						com.media.info.Video video = new com.media.info.Video();
						video.setCameraInfo(aUpload.getCameraInfo());
						video.setDuration(aUpload.getDuration());
						video.setEncode(aUpload.getEncode());
						video.setVideoUrl(url);
						video.setFileType(aUpload.getFileType());
						video.setImageUrl(url + "?" + Conf.VIDEO_THUMB );
						answer.setVideo(video);
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, answer));
					} else {
						Log.e("qiniuUpload", url);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {

				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {

				}
			});
			break;
		case 12:

			qiniuU.imageUpload(imagePath, token[1],  new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {					
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {
					if (state) {
						Question question = new Question();
						question.setTitle(qUpload.getTitle());
						question.setViewType(2);
						question.setTags(qUpload.getTags());
						
						List<Image> images = new ArrayList<Image>();
						for(String url:imageUrl){
							Image image = new Image();
							image.setImageThumbUrl(url + "?" + Conf.IMAGE_THUMB);
							image.setImageUrl(url);
							images.add(image);
						}				
						question.setImages(images);
						
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, question));
					} else {
						Log.e("qiniuUpload", imageUrl.get(0));
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {
				}
			});
			break;
		case 22:

			qiniuU.imageUpload(imagePath, token[1], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {
					if (state) {
						Answer answer = new Answer();
						
						List<Image> images = new ArrayList<Image>();
						for(String url:imageUrl){
							Image image = new Image();
							image.setImageThumbUrl(url + "?" + Conf.IMAGE_THUMB);
							image.setImageUrl(url);
							images.add(image);
						}
						
						answer.setImages(images);
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, answer));
					} else {
						Log.e("qiniuUpload", imageUrl.get(0));
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {

				}
			});
			break;
		case 13:

			qiniuU.imageVoiceUpload(imagePath, token[1], voicePath, token[2], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {					
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {
					if (state) {
						Question question = new Question();
						question.setTitle(qUpload.getTitle());
						question.setViewType(3);
						question.setTags(qUpload.getTags());
						
						List<Image> images = new ArrayList<Image>();
						for(String url:imageUrl){
							Image image = new Image();
							image.setImageThumbUrl(url + "?" + Conf.IMAGE_THUMB);
							image.setImageUrl(url);
							images.add(image);
						}	
						question.setImages(images);
						
						com.media.info.Voice voice = new com.media.info.Voice();
						voice.setDuration(qUpload.getVoiceDuration());
						voice.setVoiceUrl(voiceUrl);
						question.setVoice(voice);
						
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, question));
					} else {
						Log.e("qiniuUpload", voiceUrl);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}
			});
			break;
		case 23:

			qiniuU.imageVoiceUpload(imagePath, token[1], voicePath, token[2], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {
					if (state) {
						Answer answer = new Answer();
						
						List<Image> images = new ArrayList<Image>();
						for(String url:imageUrl){
							Image image = new Image();
							image.setImageThumbUrl(url + "?" + Conf.IMAGE_THUMB);
							image.setImageUrl(url);
							images.add(image);
						}
						answer.setImages(images);
						com.media.info.Voice voice = new com.media.info.Voice();
						voice.setDuration(aUpload.getVoiceDuration());
						voice.setVoiceUrl(voiceUrl);
						answer.setVoice(voice);
						
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, answer));
					} else {
						Log.e("qiniuUpload", voiceUrl);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}
			});
			break;
		case 14:

			qiniuU.voiceUpload(voicePath, token[2], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					if (state) {
						Question question = new Question();
						question.setTitle(qUpload.getTitle());
						question.setViewType(4);
						question.setTags(qUpload.getTags());
						com.media.info.Voice voice = new com.media.info.Voice();
						voice.setDuration(qUpload.getVoiceDuration());
						voice.setVoiceUrl(url);
						question.setVoice(voice);
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, question));
					} else {
						Log.e("qiniuUpload", url);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {

				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {

				}
			});
			break;
		case 24:

			qiniuU.voiceUpload(voicePath, token[2], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					if (state) {
						Answer answer = new Answer();
						com.media.info.Voice voice = new com.media.info.Voice();
						voice.setDuration(aUpload.getVoiceDuration());
						voice.setVoiceUrl(url);
						answer.setVoice(voice);
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, answer));
					} else {
						Log.e("qiniuUpload", url);
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {

				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {

				}
			});
			break;
		case 15:
			qiniuU.imageUpload(imagePath, token[1], new CallbackByQiniuUpload() {
				@Override
				public void back(Boolean state, String url) {
					
				}

				@Override
				public void imageBack(Boolean state, List<String> imageUrl) {
					if (state) {
						Account account = new Account();
						/*
						List<Image> images = new ArrayList<Image>();
						for(String url:imageUrl){
							Image image = new Image();
							image.setImageThumbUrl(url + "?" + Conf.IMAGE_THUMB);
							image.setImageUrl(url);
							images.add(image);
						}
						*/
						account.setId(qUpload.getUserId());
						account.setUsername(qUpload.getUserName());
						account.setIcon(imageUrl.get(0));
						ThreadExecutor.execute(new ObjectPost(context, mHandler, uploadUrl, account));
					} else {
						Log.e("qiniuUpload", imageUrl.get(0));
						mHandler.sendEmptyMessage(AppConstant.HANDLER_HTTPSTATUS_ERROR);
					}
				}

				@Override
				public void imageVoiceBack(Boolean state, List<String> imageUrl, String voiceUrl) {

				}
			});

		default:
			break;
		}

	}

	private String getServerUrl() {
		StringBuilder builder = new StringBuilder(AppConstant.serverUrl);
		switch (uploadType) {
		case 11:
			builder.append("question/byVideo");
			break;
		case 12:
			builder.append("question/byOtherMedia?viewType=2");
			break;
		case 13:
			builder.append("question/byOtherMedia?viewType=3");
			break;
		case 14:
			builder.append("question/byOtherMedia?viewType=4");
			break;
		case 15:
			builder.append("auth/update");
			break;
		case 21:
			builder.append("answer/byVideo/").append(aUpload.getQuestionId());
			break;
		case 22:
			builder.append("answer/byOtherMedia/").append(aUpload.getQuestionId()).append("?viewType=").append("2");
			break;
		case 23:
			builder.append("answer/byOtherMedia/").append(aUpload.getQuestionId()).append("?viewType=").append("3");
			break;
		case 24:
			builder.append("answer/byOtherMedia/").append(aUpload.getQuestionId()).append("?viewType=").append("4");
			break;
		case 25:
			builder.append("answer/byText/").append(aUpload.getQuestionId()).append("?content=").append(aUpload.getContent()).append("&userId=").append(aUpload.getUserId());
			break;
		default:
			break;
		}
		return builder.toString();
	}

}
