package com.media.Activity;


import com.constants.AppConstant;
import com.media.httpservice.StringGet;
import com.media.Thread.ThreadExecutor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AbstractMMQAActivity{
	private EditText register_username;
//	private EditText register_password;
//	private EditText register_name;
//	private LinearLayout register_mainLayout;
//	private LinearLayout register_tagsLayout;
	private Button left_button;
	private Button right_button;
	private TextView title;
//	private LinearLayout[] register_tagsList = new LinearLayout[10];
//	private ReturnTags returnTags;
//	private List<String> tags;
//	private Button[][] tag = new Button[10][5];
//	private int[][] temp_int = new int[10][5];
//	private LinearLayout tagsListRoot;
	private CheckBox register_checkBox;
//	private LinearLayout register_addTags;
//	private LinearLayout register_addTags1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);

		left_button = (Button) findViewById(R.id.left_button);
		title = (TextView) findViewById(R.id.title);
		right_button = (Button) findViewById(R.id.right_button);
		right_button.setClickable(true);
		register_username = (EditText) findViewById(R.id.register_username);
//		register_password = (EditText) findViewById(R.id.register_password);
//		register_name = (EditText) findViewById(R.id.register_name);
		register_checkBox = (CheckBox) findViewById(R.id.register_checkBox);
//		register_mainLayout = (LinearLayout) findViewById(R.id.register_mainLayout);
//		register_tagsLayout = (LinearLayout) findViewById(R.id.register_tagsLayout);
//		register_addTags = (LinearLayout) findViewById(R.id.register_addTags);
//		register_addTags1 = (LinearLayout) findViewById(R.id.register_addTags1);

		InitConfig();
		right_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String username = register_username.getText().toString();
				if(TextUtils.isEmpty(username)){
					Toast.makeText(getBaseContext(), "请填写用户名", Toast.LENGTH_SHORT).show();
				}else{
					String url = AppConstant.serverUrl + "auth/firstLogin";		
					boolean isTeacher = register_checkBox.isChecked();	
					ThreadExecutor.execute(new StringGet(mHandler, url+"?username="+username+"&isTeacher="+isTeacher));
				}
				right_button.setClickable(false);
			}
		});

	/*	register_addTags.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				register_mainLayout.setVisibility(View.GONE);
				register_tagsLayout.setVisibility(View.VISIBLE);
				Toast.makeText(getBaseContext(), "请点击您喜欢的标签，最多25个", 1).show();
			}
		});

		register_addTags1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				register_mainLayout.setVisibility(View.VISIBLE);
				register_tagsLayout.setVisibility(View.GONE);
			}
		});*/

		left_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:
				Toast.makeText(getBaseContext(), "系统错误，请稍候重试", Toast.LENGTH_SHORT).show();
				break;
			case AppConstant.HANDLER_MESSAGE_NORMAL:
				String result = (String) msg.obj;
				if(result.equals("success")){
					Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getBaseContext(), "失败，你已经激活过了", Toast.LENGTH_SHORT).show();
				}		
				break;
			default:
				break;
			}
			finish();
		}
	};
	
	public void InitConfig() {
		title.setText("用户初次激活页面"); // 初始化页面，设置title
		right_button.setVisibility(0); // 设置右边按钮可见
		right_button.setText("激活");

		/*String url = AppConstant.serverUrl + "auth/getWholeTags";
		RestTemplate restTemplate = new RestTemplate(true);
		String result = restTemplate.getForObject(url, String.class);
		returnTags = new ReturnTags(result);
		tags = returnTags.convertString();
		Log.v("tags", tags.toString());

		for (int i = 0; i < 10; i++) { // 初始化temp_int[][]二维数组，该数组用于统计标签个数
			for (int j = 0; j < 5; j++) {
				temp_int[i][j] = 0;
			}
		}

		tagsListRoot = (LinearLayout) findViewById(R.id.register_tagsList);
		for (int i = 0; i < 10; i++) {
			register_tagsList[i] = (LinearLayout) tagsListRoot.getChildAt(i);
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				tag[i][j] = (Button) register_tagsList[i].getChildAt(j);
				tag[i][j].setText(tags.get(5 * i + j));
				tag[i][j].setBackgroundResource(R.drawable.white_btn);
				final int a = i;
				final int b = j;
				tag[i][j].setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						switch (temp_int[a][b]) {
						case 0:
							temp_int[a][b] = 1;
							if (countTags(temp_int) < 26) {
								v.setBackgroundResource(R.drawable.white_btn_clicked);
							} else {
								temp_int[a][b] = 0;
							}
							break;
						case 1:
							temp_int[a][b] = 0;
							if (countTags(temp_int) < 26) {
								v.setBackgroundResource(R.drawable.white_btn);
							} else {
								temp_int[a][b] = 1;
							}

							break;
						}
					}
				});
			}
		}*/
	}

	/*public int countTags(int[][] int_temp) { // 统计标签个数，即对temp_int求和
		int result = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				result = result + int_temp[i][j];
			}
		}

		return result;
	}

	public List<String> finalTags(int[][] int_temp, List<String> tags_temp) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				if (int_temp[i][j] == 1)
					result.add(tags.get(5 * i + j));
			}
		}
		return result;
	}*/

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}
