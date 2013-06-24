package com.media.Activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.constants.AppConstant;
import com.media.Adapter.AdapterForLinearLayout;
import com.media.Adapter.MyLinearLayoutForListAdapter;
import com.media.Thread.ThreadExecutor;
import com.media.Util.AsyncImageLoader;
import com.media.Util.GetDuration;
import com.media.Util.ImageUtil;
import com.media.Util.AsyncImageLoader.ImageCallback;
import com.media.Util.GetDuration.OnCustomCallBack;
import com.media.db.AccountInfoService;
import com.media.httpservice.QuestionInfoService;
import com.media.httpservice.StringPost;
import com.media.info.Answer;
import com.media.info.AnswerUpload;
import com.media.info.Question;
import com.media.service.AudioPlayer;
import com.media.service.AudioService;
import com.media.service.QiNiuUploadService;
import com.media.service.VoiceDialogUtil;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionViewActivity extends AbstractMMQAActivity {
	/** Called when the activity is first created. */

	private ScrollView scroll;
	private ImageButton left_button;
	private Button right_button;
	//private ImageView addmdeia;
	private InputMethodManager m;
	private FrameLayout frame1;
	private Button send_button;
	private EditText text_editor;
	private ImageView new_video;
	private ImageView new_image;
	private ImageView new_imageVoice;
	private ImageView new_voice;
	// private Button reply;
	private ViewStub stub;
	private ImageView questionThumb;
	private TextView title;
	private TextView qvTitle;
	private TextView qvDuration;
	private TextView qvCreate_cnName;
	private TextView qvCreateTime;
	private RelativeLayout rLquestionThumb;
	private AccountInfoService accountInfoService;
	private ViewStub viewstub_qb_best_answer;
	private ViewStub viewstub_qb_other_answer;
	private String questionCnName;
	private String questionId;
	private String userId;
	//音频的播放
	private FrameLayout layout;
	private ImageView voice_btn_start;
	private TextView textView;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private Boolean is_viewstub_qb_best_answer_inflate = false;
	private Boolean is_viewstub_qb_other_answer_inflate = false;
	private Boolean is_question_view_image_inlate = false;
	private Boolean is_question_view_video_inlate = false;
	private Boolean is_question_view_imagevoice_inlate = false;
	private Boolean is_question_view_voice_inlate = false;
	private boolean isChange = false;
	private boolean isRefresh = false;
	private Question question;

	private int countImg = 0;
	
	private MyLinearLayoutForListAdapter layout_qb_other_answer_content;
	
	private Answer answer;
	private AdapterForLinearLayout adapter;
	private int[] userImage = { R.drawable.emoji_0, R.drawable.emoji_1,
			R.drawable.emoji_2, R.drawable.emoji_3, R.drawable.emoji_4,
			R.drawable.emoji_5, R.drawable.emoji_6, R.drawable.emoji_7,
			R.drawable.emoji_8, R.drawable.emoji_9 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.question_view);

		Bundle bundle = this.getIntent().getExtras();
		question = (Question) bundle.getSerializable("question");
		questionId = question.getId();
		accountInfoService = new AccountInfoService(this);
		userId = accountInfoService.getAccount().getId();
		initConfig();
		loading(question);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstant.UPLOAD_MESSAGE_ACTION);
		this.registerReceiver(new MyBroadcastReciver(), intentFilter);

	}

	public String getVideoCameraInfo(String answerId, List<Answer> answers) {
		Answer answer = new Answer();
		int i = 0;
		while (i < answers.size()) {
			if (answers.get(i).getVideo().getId().equals(answerId)) {
				answer = answers.get(i);
				break;
			}
			i++;
		}
		return answer.getVideo().getCameraInfo();
	}

	private void setupBestAnswerLayout(final List<Answer> answers) {
		MyLinearLayoutForListAdapter layout_qb_best_recommend_wonderful_answer_content;
		if (!is_viewstub_qb_best_answer_inflate) {
			viewstub_qb_best_answer = (ViewStub) findViewById(R.id.viewstub_qb_best_answer);
			viewstub_qb_best_answer.inflate();
			is_viewstub_qb_best_answer_inflate = true;
			layout_qb_best_recommend_wonderful_answer_content = (MyLinearLayoutForListAdapter) findViewById(R.id.layout_qb_best_recommend_wonderful_answer_content);

		} else {
			layout_qb_best_recommend_wonderful_answer_content = (MyLinearLayoutForListAdapter) findViewById(R.id.layout_qb_best_recommend_wonderful_answer_content);
			layout_qb_best_recommend_wonderful_answer_content.removeAllViews();
		}

		((ImageView) findViewById(R.id.imageview_qb_answer_best_recommend_wonderful_answer_icon))
				.setImageResource(R.drawable.icon_user_jiangpai_34_46);
		((TextView) findViewById(R.id.textview_qb_best_recommend_wonderful_answer_text))
				.setText("最佳回答");

		adapter = new AdapterForLinearLayout(this,layout_qb_best_recommend_wonderful_answer_content,
				answers, questionCnName, questionId);

		try {
			layout_qb_best_recommend_wonderful_answer_content.setAdapter(adapter);
		} catch (Exception e) {
			displayResponse("List加载失败");
			Log.e("QuestionViewActivity", e.toString());
		}

	}

	private void setupOtherAnswerLayout(final List<Answer> answers) {
		

		
/*		MyLinearLayoutForListAdapter layout_qb_other_answer_content;*/
		if (!is_viewstub_qb_other_answer_inflate) {
			viewstub_qb_other_answer = (ViewStub) findViewById(R.id.viewstub_qb_other_answer);
			viewstub_qb_other_answer.inflate();
			is_viewstub_qb_other_answer_inflate = true;
			layout_qb_other_answer_content = (MyLinearLayoutForListAdapter) this
					.findViewById(R.id.layout_qb_other_answer_content);
		} else {
			layout_qb_other_answer_content = (MyLinearLayoutForListAdapter) this
					.findViewById(R.id.layout_qb_other_answer_content);
			layout_qb_other_answer_content.removeAllViews();
		}

		((ImageView) findViewById(R.id.imageview_qb_answer_other_answer_icon))
				.setImageResource(R.drawable.icon_qb_other);
		((TextView) findViewById(R.id.textview_qb_other_answer_text))
				.setText("回答");

		adapter = new AdapterForLinearLayout(this,layout_qb_other_answer_content,
				answers, questionCnName, questionId);
		

		try {
			layout_qb_other_answer_content.setAdapter(adapter);
		} catch (Exception e) {
			displayResponse("List加载失败");
			Log.e("QuestionViewActivity", e.toString());
		}

	}

	private void loading(final Question tempQuestion) {
		
		
		//questionThumb = (ImageView) findViewById(R.id.questionThumb);
		//qvDuration = (TextView) findViewById(R.id.qvDuration);
		//父布局
		rLquestionThumb = (RelativeLayout) findViewById(R.id.RLquestionThumb);
		
		qvCreate_cnName = (TextView) findViewById(R.id.qvCreate_cnName);
		qvCreateTime = (TextView) findViewById(R.id.qvCreateTime);
		qvTitle = (TextView) findViewById(R.id.qvtitle);
		//addmdeia = (ImageView) findViewById(R.id.addmedia);
		frame1 = (FrameLayout) findViewById(R.id.frame1);
		text_editor = (EditText) findViewById(R.id.text_editor);
		m = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		new_video = (ImageView) this.findViewById(R.id.new_video);
		new_voice = (ImageView) this.findViewById(R.id.new_voice);
		new_image = (ImageView) this.findViewById(R.id.new_image);
		new_imageVoice = (ImageView) this.findViewById(R.id.new_imageVoice);
		text_editor = (EditText) this.findViewById(R.id.text_editor);
		send_button = (Button) this.findViewById(R.id.send_button);

		final List<Answer> answers = tempQuestion.getAnswers();

		send_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(text_editor.getText().toString())) {
					displayResponse("请输入内容");
				} else {
					String url = AppConstant.serverUrl
							+ "answer/byText/"
							+ questionId
							+ "?userId="
							+ userId
							+ "&content="
							+ java.net.URLEncoder.encode(text_editor.getText()
									.toString());
					ThreadExecutor.execute(new StringPost(
							QuestionViewActivity.this, url, ""));
					text_editor.setText("");		
					refresh();
					
				}

			}
		});

		new_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toVideoActivity = new Intent();
				toVideoActivity.setClass(getBaseContext(), VideoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("questionId", tempQuestion.getId());
				bundle.putBoolean("isQuestion", false);
				toVideoActivity.putExtras(bundle);
				startActivity(toVideoActivity);
				isChange = true;
				isRefresh = true;
			}
		});

		new_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VoiceDialogUtil dialog = new VoiceDialogUtil(
						QuestionViewActivity.this, R.style.DialogVoice, 24,
						questionId);
				dialog.show();
				AudioService.getService().start();
				isChange = true;
				isRefresh = true;
			}

		});

		new_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent("android.media.action.IMAGE_CAPTURE");

				i.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(AppConstant.TEMP_PATH)));

				startActivityForResult(i, AppConstant.TAKE_PHOTO_ID);
			}
		});

		new_imageVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						QuestionViewActivity.this);
				alertDialog.setItems(new String[] { "拍摄照片", "本地选取" }, listener);
				alertDialog.show();
			}
			
			
			final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case 0:
						Intent i = new Intent(
								"android.media.action.IMAGE_CAPTURE");
						i.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(AppConstant.TEMP_PATH)));
						startActivityForResult(i, AppConstant.TAKE_CAMERA_IV_ID);

						break;
					case 1:
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent,
								AppConstant.TAKE_IAMGE_IV_ID);
						break;
					}
				}
			};
		});

		
		
		text_editor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (frame1.getVisibility() == View.VISIBLE) {
					frame1.setVisibility(View.GONE);
					m.showSoftInput(v, 0);
				}

			}
		});

		/*addmdeia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (frame1.getVisibility() == View.GONE) {
					m.hideSoftInputFromWindow(v.getWindowToken(), 0);
					frame1.setVisibility(View.VISIBLE);
				} else {
					m.hideSoftInputFromWindow(v.getWindowToken(), 0);
					frame1.setVisibility(View.GONE);
				}

			}
		});*/

		switch (tempQuestion.getViewType()) {
		case 1:
			
			
			if (tempQuestion.getVideo() != null) {
				if(!is_question_view_video_inlate){
					stub = (ViewStub)findViewById(R.id.question_view_video);
					stub.inflate();
					is_question_view_video_inlate = true;
				}
				qvDuration = (TextView) findViewById(R.id.qvDuration);
				questionThumb = (ImageView) findViewById(R.id.questionThumb);
				qvDuration.setText(tempQuestion.getVideo().getDuration());
				questionThumb.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent toVideoViewActivity = new Intent();
						if (tempQuestion.getVideo().getCameraInfo()
								.equals("android.front")) {
							toVideoViewActivity.setClass(getBaseContext(),
									VideoViewFActivity.class);
						} else if (tempQuestion.getVideo().getCameraInfo()
								.equals("android.behind")) {
							toVideoViewActivity.setClass(getBaseContext(),
									VideoViewBActivity.class);
						} else {
							toVideoViewActivity.setClass(getBaseContext(),
									VideoViewBActivity.class);
						}

						Bundle b = new Bundle();
						b.putString("videoId", tempQuestion.getVideo().getId());
						b.putString("videoUrl", tempQuestion.getVideo()
								.getVideoUrl());
						// b.putString("videoId", "50e76ed0b7606b18aca8886f");
						toVideoViewActivity.putExtras(b);
						startActivity(toVideoViewActivity);
					}
				});

				String imageUrl = tempQuestion.getVideo().getImageUrl();
				if (imageUrl.equals("")) {
					questionThumb.setImageResource(R.drawable.prepare_video);
				}
				new AsyncImageLoader().loadDrawable(this, imageUrl,
						new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {

								if (imageDrawable != null) {
									questionThumb
											.setImageDrawable(imageDrawable);
								}
							}
						});

			} else {
				rLquestionThumb.setVisibility(View.GONE);
			}
			break;
		case 2:
			if(!is_question_view_image_inlate){
				ViewStub stub1 = (ViewStub)findViewById(R.id.question_view_image);
				stub1.inflate();
				is_question_view_image_inlate = true;
			}
			
			questionThumb = (ImageView) findViewById(R.id.questionThumb);
			questionThumb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent toQuestionView = new Intent();
					toQuestionView.setClass(QuestionViewActivity.this,
							MyViewPagerActivity.class);
					ArrayList<String> urllist = new ArrayList<String>();

					for(int i=0;i<question.getImages().size();i++){
						urllist.add(question.getImages().get(i).getImageUrl());
					
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("urllist", urllist);
						toQuestionView.putExtras(bundle);
						startActivity(toQuestionView);
					}
				}
			});
			

			String imageUrl = tempQuestion.getImages().get(0).getImageThumbUrl();
			if (imageUrl.equals("")) {
				questionThumb.setImageResource(R.drawable.prepare_video);
			}
			new AsyncImageLoader().loadDrawable(this, imageUrl,
					new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							if (imageDrawable != null) {
								questionThumb.setImageDrawable(imageDrawable);
							}
						}
					});

			break;
		case 3:
			if(!is_question_view_imagevoice_inlate){
				stub = (ViewStub)findViewById(R.id.question_view_imagevoice);
				stub.inflate();
				is_question_view_imagevoice_inlate = true;
			}
			textView = (TextView)this.findViewById(R.id.audio_length);
			textView.setText(tempQuestion.getVoice().getDuration()+"s");
			layout = (FrameLayout)findViewById(R.id.imagevoice_start);
			voice_btn_start = (ImageView)findViewById(R.id.play_button);
			questionThumb = (ImageView) findViewById(R.id.questionThumb);
			questionThumb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent toQuestionView = new Intent();
					toQuestionView.setClass(QuestionViewActivity.this,
							ImageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("imageUrl", question.getImages().get(0).getImageUrl());
					bundle.putString("imageId", question.getImages().get(0).getId());
					toQuestionView.putExtras(bundle);
					startActivity(toQuestionView);
				}
			});
			
			

			String imageUrl2 = tempQuestion.getImages().get(0).getImageThumbUrl();
			if (imageUrl2.equals("")) {
				questionThumb.setImageResource(R.drawable.prepare_video);
			}
			new AsyncImageLoader().loadDrawable(this, imageUrl2,
					new ImageCallback() {

						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							if (imageDrawable != null) {
								questionThumb.setImageDrawable(imageDrawable);
							}
						}
					});
			
			
			layout.setOnClickListener(new OnClickListener() {
				private AnimationDrawable animationDrawable;
				MediaPlayer voicePlay;
				Boolean isCreate = false;
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (!isCreate) {
						voicePlay = new MediaPlayer();
						Uri uri = Uri.parse(question.getVoice().getVoiceUrl());
						try {
							voicePlay.setDataSource(getBaseContext(), uri);
							voicePlay.prepare();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						isCreate = !isCreate;

					}
					
					voicePlay.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {// 为下次播放做准备

							if (mp != null) {
								mp.stop();
								voice_btn_start.setImageResource(R.drawable.feed_main_player_play);
								isCreate = false;
							}
						}

					});
					if (voicePlay.isPlaying()) {
						voice_btn_start.setImageResource(R.drawable.feed_main_player_play);
						voicePlay.pause();
					} else {
						voice_btn_start.setImageResource(R.drawable.feed_main_player_pause_anim_big);
						animationDrawable = (AnimationDrawable) voice_btn_start
								.getDrawable();
						animationDrawable.start();
						voicePlay.start();
					}

				}
			});
			
			break;
		case 4:
			if(!is_question_view_voice_inlate){
				stub = (ViewStub)findViewById(R.id.question_view_voice);
				stub.inflate();
				is_question_view_voice_inlate = true;
			}
			textView = (TextView)this.findViewById(R.id.audio_length);
			textView.setText(tempQuestion.getVoice().getDuration()+"s  ");
			layout = (FrameLayout)findViewById(R.id.voice_start);
			voice_btn_start = (ImageView)findViewById(R.id.play_button);

			layout.setOnClickListener(new OnClickListener() {
				private AnimationDrawable animationDrawable;
				MediaPlayer voicePlay;
				Boolean isCreate = false;
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (!isCreate) {
						voicePlay = new MediaPlayer();
						Uri uri = Uri.parse(question.getVoice().getVoiceUrl());
						try {
							voicePlay.setDataSource(getBaseContext(), uri);
							voicePlay.prepare();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						isCreate = !isCreate;

					}
					
					voicePlay.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {// 为下次播放做准备

							if (mp != null) {
								mp.stop();
								voice_btn_start.setImageResource(R.drawable.feed_main_player_play);
								isCreate = false;
							}
						}

					});
					if (voicePlay.isPlaying()) {
						voice_btn_start.setImageResource(R.drawable.feed_main_player_play);
						voicePlay.pause();
					} else {
						voice_btn_start.setImageResource(R.drawable.feed_main_player_pause_anim_big);
						animationDrawable = (AnimationDrawable) voice_btn_start
								.getDrawable();
						animationDrawable.start();
						voicePlay.start();
					}

				}
			});
			break;
		default:
			break;
		}

		qvCreate_cnName.setText(tempQuestion.getAuthorName());
		qvCreateTime.setText(dateFormat.format(tempQuestion.getCreateTime()));
		questionCnName = tempQuestion.getAuthorName();

		qvTitle.setText(tempQuestion.getTitle());
		

		//先排序。再传入answer
		if(answers!= null&&answers.size()>1){
			Collections.sort(answers,new Comparator<Answer>(){
	
				@Override
				public int compare(Answer lhs, Answer rhs) {
	                    if(lhs.getCreateTime().getTime() < rhs.getCreateTime().getTime() ){
	                            return 1;
	                    }else {
	                            return -1;
	                    }
				
				}});
		}
		
		/*
		 * 设置最佳回答的Layout
		 */
		if (tempQuestion.getBestAnswer() != null&&tempQuestion.getBestAnswer().size()!=0) {
			setupBestAnswerLayout(tempQuestion.getBestAnswer());
		}
		/*
		 * 设置其他回答的Layout
		 */
		if (answers!= null&&answers.size()!=0&&answers.get(0)!=null) {
			setupOtherAnswerLayout(answers);
		}
	}

	private void initConfig() {

		left_button = (ImageButton) findViewById(R.id.left_button);
		title = (TextView) findViewById(R.id.title);
		right_button = (Button) findViewById(R.id.right_button);
		scroll = (ScrollView) findViewById(R.id.scroll);

		title.setText("问题页面");
		right_button.setText("刷新");
		right_button.setVisibility(View.VISIBLE);
		left_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		right_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refresh();
			}
		});
	}

	private void refresh() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			String url = AppConstant.serverUrl + "question/" + questionId;
			ThreadExecutor.execute(new QuestionInfoService(url, mHandler,
					QuestionViewActivity.this));
			
		}


	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:

				break;
			case AppConstant.HANDLER_MESSAGE_NULL:

				break;
			case AppConstant.HANDLER_MESSAGE_NORMAL:
				Question question = (Question) msg.obj;
				loading(question);
				scroll.scrollTo(0, 0);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		isChange = false;
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AppConstant.UPLOAD_MESSAGE_ACTION)) {
				boolean isQuestion = intent
						.getBooleanExtra("isQuestion", false);
				Log.v("BroadcastReciver", "接收到了！！！");
				if (!isQuestion && isRefresh) {
					String url = AppConstant.serverUrl + "question/"
							+ questionId;
					ThreadExecutor.execute(new QuestionInfoService(url,
							mHandler, QuestionViewActivity.this, 1000));
					isRefresh = false;
					isChangeA = true;
					isChangeB = true;
				}

			}
			
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (AppConstant.TAKE_PHOTO_ID == requestCode) {
			if (resultCode != RESULT_OK)
				return;
			ImageUtil.bitmapCompress(AppConstant.TEMP_PATH,
					AppConstant.IMAGE1_PATH);

			List<String> imagePath = new ArrayList<String>();
			imagePath.add(AppConstant.IMAGE1_PATH);
			AnswerUpload au = new AnswerUpload();
			au.setUserId(userId);
			au.setQuestionId(questionId);
			au.setImagePath(imagePath);

			QiNiuUploadService.startservice(getApplicationContext(), au, 22);

			isRefresh = true;

			Toast.makeText(getApplicationContext(), "发布回答中...",
					Toast.LENGTH_SHORT).show();

		} else if (AppConstant.TAKE_CAMERA_IV_ID == requestCode) {

			if (resultCode != RESULT_OK)
				return;

			ImageUtil.bitmapCompress(AppConstant.TEMP_PATH,
					AppConstant.IMAGE1_PATH);

			Intent intent = new Intent(QuestionViewActivity.this,
					ImageVoiceSet.class);
			intent.putExtra("imagePath", AppConstant.IMAGE1_PATH);
			startActivityForResult(intent, AppConstant.TAKE_VOICE_IV_ID);
		} else if (AppConstant.TAKE_IAMGE_IV_ID == requestCode) {

			if (resultCode != RESULT_OK)
				return;

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.v("picturePath", picturePath);

			ImageUtil.bitmapCompress(picturePath, AppConstant.IMAGE1_PATH);

			Intent intent = new Intent(QuestionViewActivity.this,
					ImageVoiceSet.class);
			intent.putExtra("imagePath", AppConstant.IMAGE1_PATH);
			startActivityForResult(intent, AppConstant.TAKE_VOICE_IV_ID);
		} else if (AppConstant.TAKE_VOICE_IV_ID == requestCode) {
			if (resultCode != RESULT_OK)
				return;
			
			List<String> imagePath = new ArrayList<String>();
			imagePath.add(AppConstant.IMAGE1_PATH);
			final AnswerUpload au = new AnswerUpload();
			au.setVoicePath(AppConstant.VOICE_PATH);
			au.setUserId(userId);
			au.setQuestionId(questionId);
			au.setImagePath(imagePath);
			
			new GetDuration(QuestionViewActivity.this, AppConstant.VOICE_PATH, new OnCustomCallBack() {		
				@Override
				public void back(long info) {	
					au.setVoiceDuration((int)info/1000);

					QiNiuUploadService.startservice(getApplicationContext(), au, AppConstant.AIMAGEVOICEViewType);

					isRefresh = true;

				}
			});

			Toast.makeText(getApplicationContext(), "发布回答中...",
					Toast.LENGTH_SHORT).show();
		}
	}

}