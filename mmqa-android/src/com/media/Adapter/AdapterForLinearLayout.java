package com.media.Adapter;

import io.vov.utils.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.constants.AppConstant;
import com.media.Activity.ImageActivity;
import com.media.Activity.MyViewPagerActivity;
import com.media.Activity.R;
import com.media.Activity.StartActivity;
import com.media.Activity.VideoViewBActivity;
import com.media.Activity.VideoViewFActivity;
import com.media.Thread.ThreadExecutor;
import com.media.Util.AsyncImageLoader;
import com.media.Util.AsyncImageLoader.ImageCallback;
import com.media.db.AccountInfoService;
import com.media.httpservice.Delete;
import com.media.httpservice.StringGet;
import com.media.info.Answer;
import com.media.service.AudioPlayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterForLinearLayout extends BaseAdapter {
	private LinearLayout linearLayout;
	private LayoutInflater mInflater;

	private List<Answer> answers;

	private Context mContext = null;

	private String questionCnName;

	private AccountInfoService accountInfoService;

	private AsyncImageLoader asyncImageLoader;

	private String accountName;

	private String questionId;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case AppConstant.HANDLER_MESSAGE_NORMAL:
				Toast.makeText(mContext, "成功了！", Toast.LENGTH_SHORT).show();
				break;
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(mContext, "失败...", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	public AdapterForLinearLayout(Context context, LinearLayout linearLayout, List<Answer> answers,
			String questionCnName, String questionId) {
		this.answers = removeNull(answers);
		this.linearLayout = linearLayout;
		this.mContext = context;
		this.questionCnName = questionCnName;
		this.questionId = questionId;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		accountInfoService = new AccountInfoService(mContext);
		accountName = accountInfoService.getAccount().getName();
		asyncImageLoader = new AsyncImageLoader();
	}

	private List<Answer> removeNull(List<Answer> answers) {
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i) == null)
				answers.remove(i);
		}
		return answers;
	}

	@Override
	public int getCount() {
		return answers.size();
	}

	@Override
	public Object getItem(int arg0) {
		return answers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Answer answer = answers.get(position);
		switch (answer.getViewType()) {
		case 1:
			convertView = videoView(answer, convertView);
			break;
		case 2:
			convertView = imageView(answer, convertView);
			break;
		case 3:
			convertView = imageVoiceView(answer, convertView);
			break;
		case 4:
			convertView = voiceView(answer, convertView);
			break;
		case 5:
			convertView = textView(answer, convertView);
			break;

		default:
			break;
		}
		return convertView;
	}

	private View videoView(Answer a, View convertView) {
		ViewHolder viewHolder = null;
		final Answer answer = a;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_item, null);
			viewHolder = new ViewHolder();

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.answerThumb);
			viewHolder.answerCnName = (TextView) convertView
					.findViewById(R.id.answerCnName);
/*			viewHolder.qvAnswerVideoId = (TextView) convertView
					.findViewById(R.id.qvAnswerVideoId);*/
/*			viewHolder.answerRole = (TextView) convertView
					.findViewById(R.id.answerRole);*/
/*			viewHolder.qvAnswerId = (TextView) convertView
					.findViewById(R.id.qvAnswerId);*/
			viewHolder.answerTime = (TextView) convertView
					.findViewById(R.id.answerTime);
			viewHolder.answerDuration = (TextView) convertView
					.findViewById(R.id.answerDuration);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final View tempView = convertView;

		if (answer.getVideo() != null && !"".equals(answer.getVideo())) {

			viewHolder.answerCnName.setText(answer.getAuthorName());
	/*		viewHolder.qvAnswerVideoId.setText(answer.getVideo().getId());*/
	/*		viewHolder.answerRole.setText(answer.getRole());*/
	/*		viewHolder.qvAnswerId.setText(answer.getId());*/
			viewHolder.answerTime.setText(dateFormat.format(answer
					.getCreateTime()));
			viewHolder.answerDuration.setText(answer.getVideo().getDuration());
			String imageUrl = answer.getVideo().getImageUrl();
			if (imageUrl.equals("")) {
				viewHolder.image.setImageResource(R.drawable.prepare_video);
			}
			viewHolder.image.setTag(imageUrl);

			asyncImageLoader.loadDrawable(mContext, imageUrl,
					new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							ImageView imageViewByTag = (ImageView) tempView
									.findViewWithTag(imageUrl);
							if (imageViewByTag != null && imageDrawable != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});

		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("", "test");
				Intent toVideoView = new Intent();
				String cameraInfo = answer.getVideo().getCameraInfo();
				if (cameraInfo.equals("android.front")
						|| cameraInfo.equals("android.native")) {
					toVideoView.setClass(mContext, VideoViewFActivity.class);
				} else {
					toVideoView.setClass(mContext, VideoViewBActivity.class);
				}

				Bundle bundle = new Bundle();
				bundle.putString("videoId", answer.getVideo().getId());
				bundle.putString("videoUrl", answer.getVideo().getVideoUrl());
				toVideoView.putExtras(bundle);
				mContext.startActivity(toVideoView);

			}

		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (answer.getAuthorName().equals(questionCnName)) {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放", "删除");
					} else {
						setupAlertDialog(answer.getId(), "播放");
					}

				} else {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放");
					} else if (answer.getAuthorName().equals(accountName)) {
						setupAlertDialog(answer.getId(), "删除", "播放");
					} else {
						setupAlertDialog("播放");
					}
				}
				return true;
			}
		});
		return convertView;
	}

	private View textView(Answer a, View convertView) {

		ViewHolder viewHolder = null;
		final Answer answer = a;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_text_item, null);
			viewHolder = new ViewHolder();

			viewHolder.content = (TextView) convertView
					.findViewById(R.id.answer_item_content);
			viewHolder.answerCnName = (TextView) convertView
					.findViewById(R.id.answer_item_author);
			viewHolder.answerTime = (TextView) convertView
					.findViewById(R.id.answer_item_time);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.answerCnName.setText(answer.getAuthorName());
		viewHolder.answerTime
				.setText(dateFormat.format(answer.getCreateTime()));
		viewHolder.content.setText(answer.getContent());

		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (answer.getAuthorName().equals(questionCnName)) {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放", "删除");
					} else {
						setupAlertDialog(answer.getId(), "播放");
					}

				} else {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放");
					} else if (answer.getAuthorName().equals(accountName)) {
						setupAlertDialog(answer.getId(), "删除", "播放");
					} else {
						setupAlertDialog("播放");
					}
				}
				return true;
			}
		});
		return convertView;

	}

	private View imageVoiceView(Answer a, View convertView) {
		final ArrayList<String> urllist = new ArrayList<String>();
		ViewHolder viewHolder = null;
		final Answer answer = a;
		urllist.add(answer.getImages().get(0).getImageUrl());
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_imagevoice_item, null);
			viewHolder = new ViewHolder();

			viewHolder.answerCnName = (TextView) convertView
					.findViewById(R.id.answer_item_author);
			viewHolder.answerTime = (TextView) convertView
					.findViewById(R.id.answer_item_time);
			viewHolder.linearLayout = (LinearLayout)convertView.findViewById(R.id.imagevoice_start);
			viewHolder.image = (ImageView)convertView.findViewById(R.id.imagevoice);
			viewHolder.imageButton = (ImageButton)convertView.findViewById(R.id.voice_btn_start);
			convertView.setTag(viewHolder);
			viewHolder.textView = (TextView)convertView.findViewById(R.id.voiceduration);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.answerCnName.setText(answer.getAuthorName());
		viewHolder.answerTime
				.setText(dateFormat.format(answer.getCreateTime()));
		viewHolder.textView.setText(answer.getVoice().getDuration()+"s");
		viewHolder.image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,MyViewPagerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("urllist", urllist);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});
		
		String imageUrl = answer.getImages().get(0).getImageThumbUrl();
		ImageView imageView = viewHolder.image;

		viewHolder.image.setTag(imageUrl);

		new AsyncImageLoader().loadDrawable(mContext, imageUrl,
				new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {

						ImageView imageView = (ImageView) linearLayout
								.findViewWithTag(imageUrl);
						if (imageView != null) {
							// thumb.setImageDrawable(imageDrawable);
							imageView.setImageDrawable(imageDrawable);
						}
					}
				});
		
		
		viewHolder.imageButton.setTag(answer.getVoice().getVoiceUrl());
		viewHolder.linearLayout.setOnClickListener(new OnClickListener() {
			
			private AnimationDrawable animationDrawable;
			MediaPlayer voicePlay;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//viewHolder.imageButton.
				final ImageButton button = (ImageButton) linearLayout
						.findViewWithTag(answer.getVoice().getVoiceUrl());
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
						button, answer.getVoice().getVoiceUrl());
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
		
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (answer.getAuthorName().equals(questionCnName)) {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放", "删除");
					} else {
						setupAlertDialog(answer.getId(), "播放");
					}

				} else {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放");
					} else if (answer.getAuthorName().equals(accountName)) {
						setupAlertDialog(answer.getId(), "删除", "播放");
					} else {
						setupAlertDialog("播放");
					}
				}
				return true;
			}
		});
		return convertView;

	}

	private View imageView(Answer a, View convertView) {

		ViewHolder viewHolder = null;
		final Answer answer = a;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_image_item, null);
			viewHolder = new ViewHolder();

			viewHolder.answerCnName = (TextView) convertView
					.findViewById(R.id.answer_item_author);
			viewHolder.answerTime = (TextView) convertView
					.findViewById(R.id.answer_item_time);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.answer_item_image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.answerCnName.setText(answer.getAuthorName());
		viewHolder.answerTime
				.setText(dateFormat.format(answer.getCreateTime()));

		String imageThumb = null;
		if (answer.getImages() != null && answer.getImages().size() != 0) {
			imageThumb = answer.getImages().get(0).getImageThumbUrl();
			final ImageView imageView = viewHolder.image;

			new AsyncImageLoader().loadDrawable(mContext, imageThumb,
					new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							if (imageView != null) {
								// thumb.setImageDrawable(imageDrawable);
								imageView.setImageDrawable(imageDrawable);
							}
						}
					});

		}

		viewHolder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toImageActivity = new Intent(mContext,
						ImageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("imageUrl", answer.getImages().get(0)
						.getImageUrl());
				bundle.putString("imageId", answer.getImages().get(0).getId());
				toImageActivity.putExtras(bundle);
				mContext.startActivity(toImageActivity);
			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (answer.getAuthorName().equals(questionCnName)) {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放", "删除");
					} else {
						setupAlertDialog(answer.getId(), "播放");
					}

				} else {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放");
					} else if (answer.getAuthorName().equals(accountName)) {
						setupAlertDialog(answer.getId(), "删除", "播放");
					} else {
						setupAlertDialog("播放");
					}
				}
				return true;
			}
		});
		return convertView;

	}

	private View voiceView(Answer a, View convertView) {
		ViewHolder viewHolder = null;
		final Answer answer = a;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.answer_voice_item, null);
			viewHolder = new ViewHolder();

/*			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.answer_item_content);*/
			viewHolder.answerCnName = (TextView) convertView
					.findViewById(R.id.answer_item_author);
			viewHolder.answerTime = (TextView) convertView
					.findViewById(R.id.answer_item_time);
			viewHolder.layout1 = (FrameLayout) convertView.findViewById(R.id.voice_start);
			viewHolder.startBtn = (ImageView) convertView
					.findViewById(R.id.play_button);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.audio_length);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.answerCnName.setText(answer.getAuthorName());
		viewHolder.answerTime
				.setText(dateFormat.format(answer.getCreateTime()));
		viewHolder.textView.setText(answer.getVoice().getDuration()+"s  ");
		viewHolder.startBtn.setTag(answer.getVoice().getVoiceUrl());
		viewHolder.layout1.setOnClickListener(new OnClickListener() {
			private AnimationDrawable animationDrawable;
			MediaPlayer voicePlay;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final ImageView button = (ImageView) linearLayout
						.findViewWithTag(answer.getVoice().getVoiceUrl());
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
						button, answer.getVoice().getVoiceUrl());
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
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (answer.getAuthorName().equals(questionCnName)) {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放", "删除");
					} else {
						setupAlertDialog(answer.getId(), "播放");
					}

				} else {
					if (questionCnName.equals(accountName)) {
						setupAlertDialog(answer.getId(), "设置为最佳答案", "播放");
					} else if (answer.getAuthorName().equals(accountName)) {
						setupAlertDialog(answer.getId(), "删除", "播放");
					} else {
						setupAlertDialog("播放");
					}
				}
				return true;
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView qvAnswerVideoId;
		TextView answerCnName;
		TextView answerRole;
		TextView qvAnswerId;
		TextView answerTime;
		TextView answerDuration;
		ImageView image;
		TextView content;
		LinearLayout linearLayout;
		ImageButton imageButton;
		
		TextView textView;
		ImageView startBtn;
		ImageView imagevoice_start;
		FrameLayout layout1;
	}

	private void setupAlertDialog(final String answerId, final String... items) {
		new AlertDialog.Builder(mContext)
				.setTitle("对该回答操作")
				.setItems(items,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (items[which].equals("删除")) {
									String url = AppConstant.serverUrl
											+ "answer/delete/" + answerId;
									ThreadExecutor.execute(new Delete(mHandler,
											url));

								} else if (items[which].equals("设置为最佳答案")) {
									String url = AppConstant.serverUrl
											+ "question/setUpBestAnswer?questionId="
											+ questionId + "&answerId="
											+ answerId;
									ThreadExecutor.execute(new StringGet(
											mHandler, url));
								} else if (items[which].equals("播放")) {

								}
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
	}
}
