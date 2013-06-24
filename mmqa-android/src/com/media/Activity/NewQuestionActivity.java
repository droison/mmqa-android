package com.media.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.constants.AppConstant;
import com.media.Util.GetDuration;
import com.media.Util.GetDuration.OnCustomCallBack;
import com.media.Util.ImageUtil;
import com.media.httpservice.HttpDataService;
import com.media.info.QuestionUpload;
import com.media.service.AudioPlayer;
import com.media.service.AudioService;
import com.media.service.QiNiuUploadService;
import com.media.service.VoiceDialogUtil;
import com.media.service.VoiceDialogUtil.OnCallback;
import com.qiniu.demo.QiniuUpload;
import com.qiniu.demo.QiniuUpload.CallbackByQiniuUpload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewQuestionActivity extends AbstractMMQAActivity {

	private EditText content;
	private ImageView new_video;
	private ImageView new_image;
	private ImageView new_voice;
	private ImageButton left_button;
	private Button right_button;
	private TextView title;
	private List<String> tags;
	private int[] temp_int;

	private LinearLayout tagsListRoot;
	private InputMethodManager m;
	private int viewType = 0;
	private boolean isImage = false;
	private boolean isVoice = false;
	// 添加预览图
	private LinearLayout linearLayout;
	// i用来动态生成照片地址
	private static int i = 0;
	private ArrayList<String> listpath = new ArrayList<String>();
	private List<String> imageFile = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_question);

		setUpView();
		initConfig();
		addListener();

	}

	public void addListener() {
		right_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayResponse(publishQuestion());
			}
		});

		new_voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isImage && linearLayout.getChildCount() > 0) {
					displayResponse("请先移除存在的多媒体");
					return;
				}
				if (isVoice) {
					displayResponse("只能添加一个语音");
					return;
				}
				isVoice = true;
				VoiceDialogUtil dialog = new VoiceDialogUtil(
						NewQuestionActivity.this, R.style.DialogVoice, 14,
						new OnCallback() {

							@Override
							public void back(String voicePath) {
								ImageView imageView = new ImageView(
										NewQuestionActivity.this);
								imageView.setLayoutParams(new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));
								imageView
										.setImageResource(R.drawable.chatfrom_bg_voice);
								linearLayout.addView(imageView);
								final String path = voicePath;
								imageView.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {

													MediaPlayer voicePlay = AudioPlayer.getInstance().getPlayer(NewQuestionActivity.this,path);
													
													voicePlay.setOnCompletionListener(new OnCompletionListener() {
																public void onCompletion(MediaPlayer mp) {// 为下次播放做准备
																	if (mp != null)
																		mp.stop();
																}
															});
													voicePlay.start();
											}

										});
								
								imageView.setOnLongClickListener(new OnLongClickListener() {

									@Override
									public boolean onLongClick(View v) {
										final View voiceView = v;
										AlertDialog.Builder alertDialog = new AlertDialog.Builder(
												NewQuestionActivity.this);
										alertDialog.setItems(new String[] { "播放", "删除", "取消" },
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(DialogInterface dialog,
															int which) {
														// TODO Auto-generated method stub
														switch (which) {
														case 0:											
															MediaPlayer voicePlay = AudioPlayer.getInstance().getPlayer(NewQuestionActivity.this,path);					
															voicePlay.setOnCompletionListener(new OnCompletionListener() {
																		public void onCompletion(MediaPlayer mp) {// 为下次播放做准备
																			if (mp != null)
																				mp.stop();
																		}
																	});
															voicePlay.start();
															break;
														case 1:
															linearLayout.removeView(voiceView);
															isVoice = false;
															viewType = 0;
															break;
														case 2:
															dialog.dismiss();
														}
													}

												});
										alertDialog.show();
										return false;
									}
								});


							}
						});

				dialog.show();

				AudioService.getService().start();
				viewType = 14;
			}
		});

		// 图片
		new_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isImage && !isVoice && linearLayout.getChildCount() > 0) {
					displayResponse("请先移除存在的多媒体");
					return;
				}
				if (isImage && !isVoice && linearLayout.getChildCount() == 3) {
					displayResponse("最多只能添加三张照片");
					return;
				}
				if (isImage && isVoice && linearLayout.getChildCount() == 4) {
					displayResponse("最多只能添加三张照片");
					return;
				}
				isImage = true;
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						NewQuestionActivity.this);
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
						startActivityForResult(i, AppConstant.TAKE_PHOTO_ID);

						break;
					case 1:
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, AppConstant.TAKE_PIC_ID);
						break;
					}
				}
			};
		});

		// 视频
		new_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (linearLayout.getChildCount() > 0) {
					displayResponse("请先移除存在的多媒体");
					return;
				}
				isImage = false;
				Intent toVideoActivity = new Intent();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isQuestion", true);
				toVideoActivity.putExtras(bundle);
				toVideoActivity.setClass(getBaseContext(), VideoActivity.class);
				startActivityForResult(toVideoActivity,
						AppConstant.TAKE_VIDEO_ID);

			}
		});

		left_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void setUpView() {
		left_button = (ImageButton) findViewById(R.id.left_button);
		title = (TextView) findViewById(R.id.title);
		right_button = (Button) findViewById(R.id.right_button);
		content = (EditText) findViewById(R.id.content);
		new_video = (ImageView) this.findViewById(R.id.new_video);
		new_image = (ImageView) this.findViewById(R.id.new_image);
		new_voice = (ImageView) this.findViewById(R.id.new_voice);
		// 初始化预览图界面
		linearLayout = (LinearLayout) findViewById(R.id.linerlayout_images);

		m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public void initConfig() {
		title.setText("新问题"); // 初始化页面，设置title
		right_button.setVisibility(0); // 设置右边按钮可见
		right_button.setText("发布");
		tags = HttpDataService.getInstance(NewQuestionActivity.this).getAccount().getTags();
		int len = tags.size();
		temp_int = new int[len];
		tagsListRoot = (LinearLayout) findViewById(R.id.tagsLinearLayout);

		for (int i = 0; i < len; i++) {
			// temp_int[i] = 0;
			String tag = tags.get(i);
			View view = getLayoutInflater().inflate(R.layout.tag_button_item,
					null);
			TextView tagButton = (TextView) view.findViewById(R.id.tag);
			tagButton.setText(tag);
			tagButton.setTag(i);
			tagsListRoot.addView(view);
			tagButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int num = (Integer) v.getTag();
					switch (temp_int[num]) {
					case 0:
						temp_int[num] = 1;
						if (countTags(temp_int) < 4) {
							v.setBackgroundResource(R.drawable.white_btn_clicked);
						} else {
							temp_int[num] = 0;
						}
						break;
					case 1:
						temp_int[num] = 0;
						if (countTags(temp_int) < 4) {
							v.setBackgroundResource(R.drawable.white_btn);
						} else {
							temp_int[num] = 1;
						}

						break;
					}
				}
			});
		}

	}

	public int countTags(int[] int_temp) { // 统计标签个数，即对temp_int求和
		int result = 0;
		for (int i = 0; i < int_temp.length; i++) {
			result = result + int_temp[i];
		}
		return result;
	}

	public List<String> finalTags(int[] int_temp, List<String> tags_temp) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < tags_temp.size(); i++) {
			if (int_temp[i] == 1)
				result.add(tags.get(i));
		}
		return result;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		HttpDataService.getInstance(NewQuestionActivity.this).setQuestionUpload(null);
	}

	private String publishQuestion() {
		final QuestionUpload qu = HttpDataService.getInstance(NewQuestionActivity.this).getQuestionUpload();

		List<String> finalTags = finalTags(temp_int, tags);
		String finalTitle = content.getText().toString();

		if (TextUtils.isEmpty(finalTitle)) {
			return "请输入问题主题";
		}
		if (finalTags.toString().equals("[]")) {
			return "请添加标签";
		}
		if (linearLayout.getChildCount() == 0) {
			return "请添加多媒体信息";
		}

		String videoPath = AppConstant.VIDEO_PATH;
		String imagePath = AppConstant.THUMB_PATH;
		String voicePath = AppConstant.VOICE_PATH;

		qu.setUserId(HttpDataService.getInstance(NewQuestionActivity.this).getAccount().getId());
		qu.setTitle(finalTitle);
		qu.setImagePath(imageFile);

		qu.setTags(finalTags);

		qu.setVoicePath(voicePath);
		qu.setThumbPath(imagePath);
		qu.setVideoPath(videoPath);
		
		if(viewType == 11){
			new GetDuration(NewQuestionActivity.this, linearLayout, new OnCustomCallBack() {
				
				@Override
				public void back(long info) {
					int i = (int) (info / 1000);
					qu.setDuration(i + "s");
					QiNiuUploadService.startservice(getApplicationContext(), qu, viewType);
					finish();
				}
			});
			return "发布问题中...";
		}
		if(isImage&&isVoice&&linearLayout.getChildCount()>1)
			viewType = 13;
		if(isImage&&!isVoice)
			viewType =12;
		if(isVoice&&linearLayout.getChildCount()==1)
			viewType =14;
		
		if(viewType == 13||viewType == 14){
			new GetDuration(NewQuestionActivity.this, AppConstant.VOICE_PATH, new OnCustomCallBack() {
				
				@Override
				public void back(long info) {	
					qu.setVoiceDuration((int)info/1000);
					QiNiuUploadService.startservice(getApplicationContext(), qu, viewType);
					finish();
				}
			});
		}
		else if(viewType == 12){
			QiNiuUploadService.startservice(getApplicationContext(), qu, viewType);
			finish();
		}
		return "发布问题中...";

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (AppConstant.TAKE_PHOTO_ID == requestCode) {
			if (resultCode != RESULT_OK)
				return;

			// 如果先拍摄的是视频（或者语音图片）需要先将其删除？
			View view1 = linearLayout.findViewWithTag("video");
			if (view1 != null) {
				if (view1.getTag().toString() == "video") {
					linearLayout.removeAllViews();
				}
			}

			i++;
			String path = AppConstant.BASE_DIR_PATH + File.separator + "image"
					+ i + ".jpg";
			ImageUtil.bitmapCompress(AppConstant.TEMP_PATH, path);
			final View view = getLayoutInflater().inflate(
					R.layout.thumd_image_item, null);
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.thumd_item_images);
			Bitmap bitmap = BitmapFactory.decodeFile(path);

			imageView.setImageBitmap(bitmap);
			view.setTag(path);
			linearLayout.addView(view);

			listpath.add(path);
			listener(view, path, listpath);
			imageFile.add(path);
			viewType = 12;

		} else if (AppConstant.TAKE_PIC_ID == requestCode) {

			if (resultCode != RESULT_OK)
				return;

			// 如果先拍摄的是视频（或者语音图片）需要先将其删除？
			View view1 = linearLayout.findViewWithTag("video");
			if (view1 != null) {
				if (view1.getTag().toString() == "video") {
					linearLayout.removeAllViews();
				}
			}

			final View view = getLayoutInflater().inflate(
					R.layout.thumd_image_item, null);
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.thumd_item_images);

			i++;

			String path = AppConstant.BASE_DIR_PATH + File.separator + "image"
					+ i + ".jpg";

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.v("picturePath", picturePath);

			ImageUtil.bitmapCompress(picturePath, path);

			Bitmap bitmap = BitmapFactory.decodeFile(path);

			imageView.setImageBitmap(bitmap);
			view.setTag(path);
			linearLayout.addView(view);
			listpath.add(path);
			listener(view, path, listpath);

			imageFile.add(path);
			
			viewType = 12;
		} else if (AppConstant.TAKE_VIDEO_ID == requestCode) {
			if (resultCode != RESULT_OK)
				return;
			linearLayout.removeAllViews();
			final View view = getLayoutInflater().inflate(
					R.layout.thumd_image_item, null);
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.thumd_item_images);
			final ImageView startBtn = (ImageView) view
					.findViewById(R.id.startbtn);
			startBtn.setVisibility(View.VISIBLE);
			Bitmap bitmap = BitmapFactory.decodeFile(AppConstant.THUMB_PATH);

			imageView.setImageBitmap(bitmap);
			view.setTag("video");
			linearLayout.addView(view);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getBaseContext(),
							VideoViewBActivity.class);
					intent.putExtra("local_video", "mmqa");
					startActivity(intent);
				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							NewQuestionActivity.this);
					alertDialog.setItems(new String[] { "观看视频", "删除视频", "取消" },
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									switch (which) {
									case 0:
										Intent intent = new Intent(
												getBaseContext(),
												VideoViewBActivity.class);
										intent.putExtra("local_video", "mmqa");
										startActivity(intent);
										break;
									case 1:
										linearLayout.removeView(view);
										viewType = 0;
										break;
									case 2:
										dialog.dismiss();
									}
								}

							});
					alertDialog.show();

					return false;
				}
			});

			viewType = 11;
		}
	}

	// 监听函数，短按查看照片，长按选择（查看照片、删除照片、取消）
	public void listener(final View view, final String path, final ArrayList<String> listPath) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						MyViewPagerActivity.class);
				intent.putExtra("listPath", listPath);
				startActivity(intent);
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						NewQuestionActivity.this);
				alertDialog.setItems(new String[] { "查看图片", "删除图片", "取消" },
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									Intent intent = new Intent(
											getBaseContext(),
											MyViewPagerActivity.class);
									intent.putStringArrayListExtra("listPath", listPath);
									startActivity(intent);
									break;
								case 1:
									linearLayout.removeView(view);
									imageFile.remove(path);
									listpath.remove(path);
									if(listpath.size()==0)
										isImage = false;
									break;
								case 2:
									dialog.dismiss();
								}
							}

						});
				alertDialog.show();
				return false;
			}
		});
	}

}
