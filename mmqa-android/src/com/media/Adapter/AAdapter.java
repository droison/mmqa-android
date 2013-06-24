package com.media.Adapter;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.media.Activity.ImageActivity;
import com.media.Activity.MyViewPagerActivity;
import com.media.Activity.QuestionViewActivity;
import com.media.Activity.R;
import com.media.Activity.VideoViewBActivity;
import com.media.Activity.VideoViewFActivity;
import com.media.Util.AsyncImageLoader;
import com.media.Util.RoundCorner;
import com.media.Util.SaveImage;
import com.media.Util.AsyncImageLoader.ImageCallback;
import com.media.Util.TimeUtil;
import com.media.component.xlistview.XListView;
import com.media.info.Question;
import com.media.service.AudioPlayer;

public class AAdapter extends BaseAdapter {

	private Context mContext;
	private List<Question> questions;
	private LayoutInflater mInflater;
	private XListView listView;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private String imageUrl;
	private String icon;
	private String cameraInfo;
	private ImageView startBtn;
	private int[] userImage = { R.drawable.emoji_0, R.drawable.emoji_1,
			R.drawable.emoji_2, R.drawable.emoji_3, R.drawable.emoji_4,
			R.drawable.emoji_5, R.drawable.emoji_6, R.drawable.emoji_7,
			R.drawable.emoji_8, R.drawable.emoji_9 };
	private int[] imageSize = {R.drawable.image_1,R.drawable.image_2,R.drawable.image_3,R.drawable.image_4,R.drawable.image_5};
	public AAdapter(Context mContext, List<Question> questions,
			XListView listView) {
		this.mContext = mContext;
		this.questions = questions;
		this.listView = listView;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return questions.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return questions.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Question question = questions.get(position);

		Log.d("ViewType", question.getViewType() + "");
		switch (question.getViewType()) {

		case 1:
			convertView = videoView(question, convertView);
			break;
		case 2:
			convertView = imageView(question, convertView);
			break;
		case 3:
			convertView = voiceimageView(question, convertView);
			break;
		case 4:
			convertView = voiceView(question, convertView);
			break;

		default:
			convertView = voiceView(question, convertView);
			break;
		}
		return convertView;
	}

	public void setIcon(ImageView iv, final Question question) {
		
		if(question.getIcon()==null){
			return;
		}
		icon = question.getIcon();
		//System.out.println(icon);

		
		iv.setTag(question.getId()+icon);
		new AsyncImageLoader().loadDrawable(mContext, icon,
				new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {

						ImageView imageView = (ImageView) listView
								.findViewWithTag(question.getId()+imageUrl);
						if (imageDrawable != null) {
							BitmapDrawable bd = (BitmapDrawable) imageDrawable;
							// thumb.setImageDrawable(imageDrawable);
							imageView.setImageBitmap((RoundCorner.toRoundCorner(bd.getBitmap(), 20)));
						}
					}
				});

	}

	//
	private View videoView(Question q, View convertView) {
		ViewHolder viewHolder = null;
		final Question question = q;

		convertView = mInflater.inflate(R.layout.main_attention_item, null);
		viewHolder = new ViewHolder();
		viewHolder.create_cnName = (TextView) convertView
				.findViewById(R.id.create_cnName);
		viewHolder.createTime = (TextView) convertView
				.findViewById(R.id.createTime);
		viewHolder.isCompleteImage = (ImageView) convertView
				.findViewById(R.id.isCompleteImage);
		viewHolder.questionId = (TextView) convertView
				.findViewById(R.id.questionId);
		viewHolder.title = (TextView) convertView.findViewById(R.id.title);
		viewHolder.main_attention_item_count = (TextView) convertView
				.findViewById(R.id.main_attention_item_count);
		viewHolder.lastAnswer_cnName = (TextView) convertView
				.findViewById(R.id.lastAnswer_cnName);
		viewHolder.lastAnswerTime = (TextView) convertView
				.findViewById(R.id.lastAnswerTime);
		viewHolder.image = (ImageView) convertView.findViewById(R.id.video);
		viewHolder.startBtn = (ImageView) convertView
				.findViewById(R.id.startbtn);
		viewHolder.user_portrait = (ImageView) convertView
				.findViewById(R.id.user_portrait);

		setIcon(viewHolder.user_portrait, question);

		viewHolder.create_cnName.setText(question.getAuthorName());
		viewHolder.createTime.setText(TimeUtil.converTime(question
				.getCreateTime()));
		viewHolder.isCompleteImage
				.setImageResource(question.getBeComplete() ? R.drawable.ic_duihao
						: R.drawable.ic_wenhao);
		viewHolder.questionId.setText(question.getId());
		viewHolder.title.setText(question.getTitle());
		viewHolder.main_attention_item_count.setText("("
				+ question.getCountAnswer() + ")");
		{
			int i = question.getTags().size();
			viewHolder.tag1 = (TextView) convertView.findViewById(R.id.tag1);
			viewHolder.tag1.setText(question.getTags().get(0));
			if (i > 1) {
				viewHolder.tag2 = (TextView) convertView
						.findViewById(R.id.tag2);
				viewHolder.tag2.setVisibility(View.VISIBLE);
				viewHolder.tag2.setText(question.getTags().get(1));
			}
			if (i > 2) {
				viewHolder.tag3 = (TextView) convertView
						.findViewById(R.id.tag3);
				viewHolder.tag3.setVisibility(View.VISIBLE);
				viewHolder.tag3.setText(question.getTags().get(2));
			}
		}
		viewHolder.lastAnswer_cnName.setText(question.getLastAnswerAuthor()
				+ " ");
		viewHolder.lastAnswerTime.setText(TimeUtil.converTime(question
				.getAnswerTime()));

		imageUrl = question.getVideo().getImageUrl();
		System.out.println("imageUrl==============="+imageUrl);

		viewHolder.image.setTag(imageUrl);

		if (imageUrl.equals("")) {
			viewHolder.image.setImageResource(R.drawable.test2);
		}

		new AsyncImageLoader().loadDrawable(mContext, imageUrl,
				new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {

						ImageView imageView = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageView != null) {
							// thumb.setImageDrawable(imageDrawable);
							imageView.setImageDrawable(imageDrawable);
							imageView.setTag("");
						}
					}
				});

		viewHolder.startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String qvAnswerVideoId = question.getVideo().getId();
				Intent toVideoView = new Intent();
				cameraInfo = question.getVideo().getCameraInfo();
				if (cameraInfo.equals("android.front")
						|| cameraInfo.equals("android.native")) {
					toVideoView.setClass(mContext, VideoViewFActivity.class);
				} else {
					toVideoView.setClass(mContext, VideoViewBActivity.class);
				}

				Bundle bundle = new Bundle();
				bundle.putString("videoId", qvAnswerVideoId);
				bundle.putString("videoUrl", question.getVideo().getVideoUrl());
				toVideoView.putExtras(bundle);
				mContext.startActivity(toVideoView);
			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toQuestionView = new Intent();
				toQuestionView.setClass(mContext, QuestionViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("question", question);
				toQuestionView.putExtras(bundle);
				mContext.startActivity(toQuestionView);

			}
		});
		return convertView;
	}

	//
	private View imageView(Question q, View convertView) {
		ViewHolder viewHolder = null;
		final Question question = q;
		convertView = mInflater.inflate(R.layout.main_attention_item_image,
				null);
		viewHolder = new ViewHolder();
		viewHolder.create_cnName = (TextView) convertView
				.findViewById(R.id.create_cnName);
		viewHolder.createTime = (TextView) convertView
				.findViewById(R.id.createTime);
		viewHolder.isCompleteImage = (ImageView) convertView
				.findViewById(R.id.isCompleteImage);
		viewHolder.questionId = (TextView) convertView
				.findViewById(R.id.questionId);
		viewHolder.title = (TextView) convertView.findViewById(R.id.title);
		viewHolder.main_attention_item_count = (TextView) convertView
				.findViewById(R.id.main_attention_item_count);
		viewHolder.lastAnswer_cnName = (TextView) convertView
				.findViewById(R.id.lastAnswer_cnName);
		viewHolder.lastAnswerTime = (TextView) convertView
				.findViewById(R.id.lastAnswerTime);
		viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
		viewHolder.image_size = (TextView) convertView
				.findViewById(R.id.image_size);
		viewHolder.user_portrait = (ImageView) convertView
				.findViewById(R.id.user_portrait);
		// 设置头像
		setIcon(viewHolder.user_portrait, question);

		viewHolder.create_cnName.setText(question.getAuthorName());
		viewHolder.createTime.setText(TimeUtil.converTime(question
				.getCreateTime()));
		viewHolder.isCompleteImage
				.setImageResource(question.getBeComplete() ? R.drawable.ic_duihao
						: R.drawable.ic_wenhao);
		viewHolder.questionId.setText(question.getId());
		viewHolder.title.setText(question.getTitle());
		viewHolder.main_attention_item_count.setText("("
				+ question.getCountAnswer() + ")");

		{
			int i = question.getTags().size();
			String result = "#" + question.getTags().get(0) + "<br>";
			if (i > 1) {
				result += "#" + question.getTags().get(1) + "<br>";
			}
			if (i > 2) {
				result += "#" + question.getTags().get(2);
			}
			viewHolder.tag1 = (TextView) convertView.findViewById(R.id.tag1);
			viewHolder.tag1.setText(Html.fromHtml(result.toString()));
		}

		viewHolder.lastAnswer_cnName.setText(question.getLastAnswerAuthor()
				+ " ");
		viewHolder.lastAnswerTime.setText(TimeUtil.converTime(question
				.getAnswerTime()));

		viewHolder.image_size.setBackgroundResource(imageSize[question.getImages().size()-1]);
		//viewHolder.image_size.setText(question.getImages().size() + "");

		imageUrl = question.getImages().get(0).getImageThumbUrl();

		System.out.println(imageUrl);

		viewHolder.image.setTag(imageUrl);

		if (imageUrl.equals("")) {
			viewHolder.image.setImageResource(R.drawable.test2);
		}

		new AsyncImageLoader().loadDrawable(mContext, imageUrl,
				new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {

						ImageView imageView = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageView != null) {
							// thumb.setImageDrawable(imageDrawable);
							imageView.setImageDrawable(imageDrawable);
							imageView.setTag("");
						}
					}
				});

		if (viewHolder.startBtn != null)
			viewHolder.startBtn.setVisibility(View.GONE);

		viewHolder.image_size.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toImageActivity = new Intent(mContext,
						MyViewPagerActivity.class);
				ArrayList<String> urllist = new ArrayList<String>();

				for (int i = 0; i < question.getImages().size(); i++) {
					urllist.add(question.getImages().get(i).getImageUrl());

				}
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("urllist", urllist);

				toImageActivity.putExtras(bundle);
				mContext.startActivity(toImageActivity);
			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toQuestionView = new Intent();
				toQuestionView.setClass(mContext, QuestionViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("question", question);
				toQuestionView.putExtras(bundle);
				mContext.startActivity(toQuestionView);

			}
		});
		return convertView;
	}

	// imagevoice
	private View voiceimageView(Question q, View convertView) {
		ViewHolder viewHolder = null;
		final Question question = q;
		convertView = mInflater.inflate(
				R.layout.main_attention_item_imagevoice, null);
		viewHolder = new ViewHolder();
		viewHolder.create_cnName = (TextView) convertView
				.findViewById(R.id.create_cnName);
		viewHolder.createTime = (TextView) convertView
				.findViewById(R.id.createTime);
		viewHolder.isCompleteImage = (ImageView) convertView
				.findViewById(R.id.isCompleteImage);
		viewHolder.questionId = (TextView) convertView
				.findViewById(R.id.questionId);
		viewHolder.title = (TextView) convertView.findViewById(R.id.title);
		viewHolder.main_attention_item_count = (TextView) convertView
				.findViewById(R.id.main_attention_item_count);
		viewHolder.lastAnswer_cnName = (TextView) convertView
				.findViewById(R.id.lastAnswer_cnName);
		viewHolder.lastAnswerTime = (TextView) convertView
				.findViewById(R.id.lastAnswerTime);
		viewHolder.image = (ImageView) convertView
				.findViewById(R.id.imagevoice);
		viewHolder.image_size = (TextView) convertView
				.findViewById(R.id.image_size);
		viewHolder.imagevoice_start = (ImageView) convertView
				.findViewById(R.id.play_button);
		viewHolder.layout = (FrameLayout) convertView
				.findViewById(R.id.imagevoice_start);
		viewHolder.voiceduration = (TextView) convertView
				.findViewById(R.id.audio_length);
		viewHolder.user_portrait = (ImageView) convertView
				.findViewById(R.id.user_portrait);
		setIcon(viewHolder.user_portrait, question);
		viewHolder.create_cnName.setText(question.getAuthorName());
		viewHolder.createTime.setText(TimeUtil.converTime(question
				.getCreateTime()));
		viewHolder.isCompleteImage
				.setImageResource(question.getBeComplete() ? R.drawable.ic_duihao
						: R.drawable.ic_wenhao);
		viewHolder.questionId.setText(question.getId());
		viewHolder.image_size.setBackgroundResource(imageSize[question.getImages().size()-1]);
		viewHolder.title.setText(question.getTitle());
		viewHolder.main_attention_item_count.setText("("
				+ question.getCountAnswer() + ")");
		{
			int i = question.getTags().size();
			viewHolder.tag1 = (TextView) convertView.findViewById(R.id.tag1);
			viewHolder.tag1.setText(question.getTags().get(0));
			if (i > 1) {
				viewHolder.tag2 = (TextView) convertView
						.findViewById(R.id.tag2);
				viewHolder.tag2.setVisibility(View.VISIBLE);
				viewHolder.tag2.setText(question.getTags().get(1));
			}
			if (i > 2) {
				viewHolder.tag3 = (TextView) convertView
						.findViewById(R.id.tag3);
				viewHolder.tag3.setVisibility(View.VISIBLE);
				viewHolder.tag3.setText(question.getTags().get(2));
			}
		}
		viewHolder.lastAnswer_cnName.setText(question.getLastAnswerAuthor()
				+ " ");
		viewHolder.voiceduration.setText(question.getVoice().getDuration()
				+ "'");
		viewHolder.lastAnswerTime.setText(TimeUtil.converTime(question
				.getAnswerTime()));

		imageUrl = question.getImages().get(0).getImageThumbUrl();
		ImageView imageView = viewHolder.image;

		viewHolder.image.setTag(imageUrl);

		new AsyncImageLoader().loadDrawable(mContext, imageUrl,
				new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {

						ImageView imageView = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageView != null) {
							// thumb.setImageDrawable(imageDrawable);
							imageView.setImageDrawable(imageDrawable);
							imageView.setTag("");
						}
					}
				});

		viewHolder.imagevoice_start.setTag(question.getVoice().getVoiceUrl());
		viewHolder.layout.setOnClickListener(new OnClickListener() {
			private AnimationDrawable animationDrawable;
			MediaPlayer voicePlay;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final ImageView button = (ImageView) listView
						.findViewWithTag(question.getVoice().getVoiceUrl());
				voicePlay = AudioPlayer.voicePlay;
				if (voicePlay != null && voicePlay.isPlaying()
						&& button == AudioPlayer.button) {
					button.setImageResource(R.drawable.feed_main_player_play);
					voicePlay.stop();
					voicePlay.release();
					AudioPlayer.voicePlay = null;
					return;
				}

				voicePlay = AudioPlayer.getInstance().getPlayer(mContext,
						button, question.getVoice().getVoiceUrl());
				voicePlay.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {// 为下次播放做准备

						if (mp != null) {
							mp.stop();
							button.setImageResource(R.drawable.feed_main_player_play);
						}
					}

				});

				button.setImageResource(R.drawable.feed_main_player_pause_anim_big);
				animationDrawable = (AnimationDrawable) button.getDrawable();
				animationDrawable.start();
				voicePlay.start();

			}
		});

		viewHolder.image_size.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toImageActivity = new Intent(mContext,
						MyViewPagerActivity.class);
				ArrayList<String> urllist = new ArrayList<String>();

				for (int i = 0; i < question.getImages().size(); i++) {
					urllist.add(question.getImages().get(i).getImageUrl());

				}
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("urllist", urllist);

				toImageActivity.putExtras(bundle);
				mContext.startActivity(toImageActivity);
			}
		});
/*		viewHolder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toImageActivity = new Intent(mContext,
						ImageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("imageUrl", question.getImages().get(0)
						.getImageUrl());
				bundle.putString("imageId", question.getImages().get(0).getId());
				toImageActivity.putExtras(bundle);
				mContext.startActivity(toImageActivity);
			}
		});*/

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toQuestionView = new Intent();
				toQuestionView.setClass(mContext, QuestionViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("question", question);
				toQuestionView.putExtras(bundle);
				mContext.startActivity(toQuestionView);

			}
		});
		return convertView;
	}

	// 返回image
	private View voiceView(Question q, View convertView) {
		ViewHolder viewHolder = null;
		final Question question = q;

		convertView = mInflater.inflate(R.layout.main_attention_item_voice,
				null);
		viewHolder = new ViewHolder();
		viewHolder.create_cnName = (TextView) convertView
				.findViewById(R.id.create_cnName);
		viewHolder.createTime = (TextView) convertView
				.findViewById(R.id.createTime);
		viewHolder.isCompleteImage = (ImageView) convertView
				.findViewById(R.id.isCompleteImage);
		viewHolder.questionId = (TextView) convertView
				.findViewById(R.id.questionId);
		viewHolder.title = (TextView) convertView.findViewById(R.id.title);
		viewHolder.main_attention_item_count = (TextView) convertView
				.findViewById(R.id.main_attention_item_count);
		viewHolder.lastAnswer_cnName = (TextView) convertView
				.findViewById(R.id.lastAnswer_cnName);
		viewHolder.lastAnswerTime = (TextView) convertView
				.findViewById(R.id.lastAnswerTime);
		viewHolder.layout1 = (LinearLayout) convertView
				.findViewById(R.id.voice);
		viewHolder.imagevoice_start = (ImageButton) convertView
				.findViewById(R.id.voice_btn_start);
		viewHolder.voiceduration = (TextView) convertView
				.findViewById(R.id.voiceduration);
		viewHolder.user_portrait = (ImageView) convertView
				.findViewById(R.id.user_portrait);
		setIcon(viewHolder.user_portrait, question);
		viewHolder.create_cnName.setText(question.getAuthorName());
		viewHolder.createTime.setText(TimeUtil.converTime(question
				.getCreateTime()));
		viewHolder.isCompleteImage
				.setImageResource(question.getBeComplete() ? R.drawable.ic_duihao
						: R.drawable.ic_wenhao);
		viewHolder.questionId.setText(question.getId());
		viewHolder.title.setText(question.getTitle());
		viewHolder.main_attention_item_count.setText("("
				+ question.getCountAnswer() + ")");
		{
			int i = question.getTags().size();
			viewHolder.tag1 = (TextView) convertView.findViewById(R.id.tag1);
			viewHolder.tag1.setText(question.getTags().get(0));
			if (i > 1) {
				viewHolder.tag2 = (TextView) convertView
						.findViewById(R.id.tag2);
				viewHolder.tag2.setVisibility(View.VISIBLE);
				viewHolder.tag2.setText(question.getTags().get(1));
			}
			if (i > 2) {
				viewHolder.tag3 = (TextView) convertView
						.findViewById(R.id.tag3);
				viewHolder.tag3.setVisibility(View.VISIBLE);
				viewHolder.tag3.setText(question.getTags().get(2));
			}
		}
		viewHolder.lastAnswer_cnName.setText(question.getLastAnswerAuthor()
				+ " ");
		viewHolder.lastAnswerTime.setText(TimeUtil.converTime(question
				.getAnswerTime()));
		viewHolder.voiceduration.setText(question.getVoice().getDuration()
				+ "'");
		viewHolder.imagevoice_start.setTag(question.getVoice().getVoiceUrl());
		viewHolder.layout1.setOnClickListener(new OnClickListener() {
			private AnimationDrawable animationDrawable;
			MediaPlayer voicePlay;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final ImageButton button = (ImageButton) listView
						.findViewWithTag(question.getVoice().getVoiceUrl());
				voicePlay = AudioPlayer.voicePlay;
				if (voicePlay != null && voicePlay.isPlaying()
						&& button == AudioPlayer.button) {
					button.setImageResource(R.drawable.feed_main_player_play);
					voicePlay.stop();
					voicePlay.release();
					AudioPlayer.voicePlay = null;
					return;
				}

				voicePlay = AudioPlayer.getInstance().getPlayer(mContext,
						button, question.getVoice().getVoiceUrl());
				voicePlay.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {// 为下次播放做准备

						if (mp != null) {
							mp.stop();
							button.setImageResource(R.drawable.feed_main_player_play);
						}
					}

				});

				button.setImageResource(R.drawable.feed_main_player_pause_anim_big);
				animationDrawable = (AnimationDrawable) button.getDrawable();
				animationDrawable.start();
				voicePlay.start();

			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toQuestionView = new Intent();
				toQuestionView.setClass(mContext, QuestionViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("question", question);
				toQuestionView.putExtras(bundle);
				mContext.startActivity(toQuestionView);

			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView isCompleteImage;
		TextView questionId;
		TextView title;
		TextView main_attention_item_count;
		TextView tag1;
		TextView tag2;
		TextView tag3;
		TextView create_cnName;
		TextView createTime;
		TextView lastAnswer_cnName;
		TextView lastAnswerTime;
		TextView voiceduration;
		ImageView image;
		ImageView startBtn;
		ImageView imagevoice_start;
		ImageView user_portrait;
		FrameLayout layout;
		LinearLayout layout1;
		TextView image_size;
	}

}
