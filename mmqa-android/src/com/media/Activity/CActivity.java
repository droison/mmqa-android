package com.media.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.constants.AppConstant;
import com.media.Adapter.CAAdapter;
import com.media.Adapter.CQAdapter;
import com.media.Util.ExitApplication;

import com.media.Util.JsonUtil;
import com.media.Util.RoundCorner;
import com.media.Util.StringUtil;

import com.media.Util.SaveImage;

import com.media.db.AccountInfoService;
import com.media.httpservice.HTTP;
import com.media.httpservice.HttpDataService;
import com.media.httpservice.HttpResponseEntity;
import com.media.info.Account;
import com.media.info.Answer;
import com.media.info.Question;
import com.media.info.QuestionUpload;
import com.media.service.QiNiuUploadService;

public class CActivity extends AbstractMMQAActivity {

	private TextView card_title;
	private Button refresh;
	private ImageButton set;
	private AccountInfoService accountInfoService;
	private ListView askListView;
	private ListView answerListView;
	private RelativeLayout askBtn;
	private RelativeLayout answerBtn;
	private String username;
	private Question[] questions;
	private Answer[] answers;
	private ImageView pic;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	// 创建一个以当前时间为名称的文件
	File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// setTitle("AAAA");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_iask);

		setUpView();

		new SynQandATask().execute();

		addListener();
	}

	//
	/*
	 * public void setPortrait() { File file = new File(AppConstant.USER_IMAGE);
	 * if (file.exists()) { Bitmap bitmap =
	 * BitmapFactory.decodeFile(AppConstant.USER_IMAGE);
	 * pic.setImageBitmap(RoundCorner.toRoundCorner(bitmap, 20)); }
	 * 
	 * }
	 */

	private void setUpView() {
		pic = (ImageView) findViewById(R.id.pic);
		pic.setImageBitmap(RoundCorner.toRoundCorner(BitmapFactory.decodeFile(AppConstant.USER_IMAGE), 20));
		/* setPortrait(); */
		set = (ImageButton) findViewById(R.id.set);
		refresh = (Button) findViewById(R.id.iask_refresh);
		card_title = (TextView) findViewById(R.id.card_title);
		askListView = (ListView) findViewById(R.id.ask_list);
		answerListView = (ListView) findViewById(R.id.answer_list);
		askBtn = (RelativeLayout) findViewById(R.id.ask_btn);
		answerBtn = (RelativeLayout) findViewById(R.id.answer_btn);
		askBtn.setBackgroundResource(R.drawable.light_sharp);
		answerBtn.setBackgroundResource(R.drawable.light_sharp_bg);
		askListView.setVisibility(View.VISIBLE);
		answerListView.setVisibility(View.GONE);

		accountInfoService = new AccountInfoService(this);
		Account account = accountInfoService.getAccount();
		card_title.setText("个人中心");
		TextView temp = (TextView) findViewById(R.id.main_iask_cnName);
		temp.setText(account.getName());
		temp = (TextView) findViewById(R.id.main_iask_role);
		temp.setText(account.getRole());
		username = account.getUsername();
		set.setVisibility(0);
		refresh.setVisibility(0);
	}

	private void addListener() {

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		askBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				askBtn.setBackgroundResource(R.drawable.light_sharp);
				answerBtn.setBackgroundResource(R.drawable.light_sharp_bg);
				askListView.setVisibility(View.VISIBLE);
				answerListView.setVisibility(View.GONE);
			}
		});

		answerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				answerBtn.setBackgroundResource(R.drawable.light_sharp);
				askBtn.setBackgroundResource(R.drawable.light_sharp_bg);
				answerListView.setVisibility(View.VISIBLE);
				askListView.setVisibility(View.GONE);
			}
		});

		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toSet = new Intent();
				toSet.setClass(getBaseContext(), SetActivity.class);
				startActivity(toSet);

			}
		});
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SynQandATask().execute();
			}
		});
	}

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	private void showDialog() {
		final String items[] = { "大图预览", "拍照", "相册选择" };
		new AlertDialog.Builder(this).setTitle("头像设置")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent toImageActivity = new Intent(
									getBaseContext(), ImageActivity.class);
							toImageActivity.putExtra("imagePath",
									AppConstant.USER_IMAGE);
							CActivity.this.startActivity(toImageActivity);
							dialog.dismiss();
							break;
						case 1:
							dialog.dismiss();
							// 调用系统的拍照功能
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 指定调用相机拍照后照片的储存路径
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tempFile));
							startActivityForResult(intent,
									PHOTO_REQUEST_TAKEPHOTO);
							break;
						case 2:
							dialog.dismiss();
							Intent i = new Intent(Intent.ACTION_PICK, null);
							i.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(i, PHOTO_REQUEST_GALLERY);
						}
					}
				}).show();

		/*
		 * .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub dialog.dismiss(); // 调用系统的拍照功能 Intent
		 * intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //
		 * 指定调用相机拍照后照片的储存路径
		 * intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
		 * startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO); } })
		 * .setNegativeButton("相册", new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub dialog.dismiss(); Intent intent = new
		 * Intent(Intent.ACTION_PICK, null);
		 * intent.setDataAndType(MediaStore.Images
		 * .Media.EXTERNAL_CONTENT_URI,"image/*");
		 * startActivityForResult(intent, PHOTO_REQUEST_GALLERY); } }).show();
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 114);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 114);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) {
				final QuestionUpload qu = HttpDataService.getInstance(CActivity.this).getQuestionUpload();
				Account account = HttpDataService.getInstance(CActivity.this).getAccount();
				ArrayList<String> listPath = new ArrayList<String>();
				listPath.add(AppConstant.USER_IMAGE);
				qu.setImagePath(listPath);
				qu.setUserId(account.getId());
				qu.setUserName(account.getUsername());
				int viewType = 15;
				QiNiuUploadService.startservice(getApplicationContext(), qu, viewType);
				
				setPicToView(data);
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			BitmapDrawable bd = (BitmapDrawable) drawable;
			SaveImage s = new SaveImage(photo);
			s.savePortrait();
			pic.setImageBitmap((RoundCorner.toRoundCorner(bd.getBitmap(), 20)));

		}
	}

	private class SynQandATask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("正在加载个人信息...");
		}

		@Override
		protected void onPostExecute(Integer result) {
			dismissProgressDialog();
			setAskListView(questions);
			setAnswerListView(answers);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			getList();
			return null;
		}

	}

	private Integer getList() {
		Integer i = 0;
		try {
			String askUrl = AppConstant.serverUrl + "question/author/"
					+ username;
			String answerUrl = AppConstant.serverUrl + "answer/author/"
					+ username;

			HttpResponseEntity hre = HTTP.get(askUrl);
			questions = JsonUtil.getQuestionArray(
					StringUtil.byte2String(hre.getB()), CActivity.this);
			hre = HTTP.get(answerUrl);
			answers = JsonUtil.getAnswerArray(
					StringUtil.byte2String(hre.getB()), CActivity.this);

		} catch (Exception e) {
			Log.e("MainActivity", e.toString());
			i = 1;
		}
		return i;
	}

	private void setAskListView(Question[] q) {
		CQAdapter adapter = new CQAdapter(CActivity.this, questions);
		askListView.setAdapter(adapter);
	}

	private void setAnswerListView(Answer[] a) {
		CAAdapter adapter = new CAAdapter(CActivity.this, a);
		answerListView.setAdapter(adapter);

	}

	private long timeStampe = 0;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (timeStampe == 0) {
			timeStampe = System.currentTimeMillis();
			Toast.makeText(getApplicationContext(), "再按一次返回键退出程序",
					Toast.LENGTH_SHORT).show();
		} else {
			if (System.currentTimeMillis() - timeStampe < 2000) {
				ExitApplication.getInstance().exit();
			} else {
				timeStampe = 0;
			}
		}
	}
}
