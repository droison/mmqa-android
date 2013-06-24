package com.media.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.media.Adapter.SetAdapter;
import com.media.Util.ExitApplication;
import com.media.db.AccountInfoService;
import com.media.mtj.BaiduSDK;

public class SetActivity extends BaiduSDK {

	private Button left_button;
	private TextView title;
	private ListView setList;
	private Button logout_btn;
	private AccountInfoService accountInfoService;
	private SetAdapter mAdapter;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		left_button = (Button) findViewById(R.id.left_button);
		title = (TextView) findViewById(R.id.title);
		setList = (ListView) findViewById(R.id.setList);
		logout_btn = (Button) findViewById(R.id.logout_btn);
		accountInfoService = new AccountInfoService(this);
		ExitApplication.getInstance().addActivity(this);
		title.setText("系统设置");
		setListView();

		logout_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				accountInfoService.delete();
				Intent toLogin = new Intent();
				toLogin.setClass(getApplicationContext(), LoginActivity.class);
				startActivity(toLogin);
				ExitApplication.getInstance().exit();
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
	
	private void setListView() {
		mAdapter = new SetAdapter(getData(), this);
		setList.setAdapter(mAdapter);
		setList.setClickable(true);
	}

	private List<HashMap<String, Object>> getData() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("set_item_icon", R.drawable.set_item_update);
		map1.put("set_item_title", "检查更新");
		list.add(map1);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("set_item_icon", R.drawable.set_item_feedback);
		map2.put("set_item_title", "意见反馈");
		list.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("set_item_icon", R.drawable.set_item_weibo);
		map3.put("set_item_title", "官方微博");
		list.add(map3);

		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("set_item_icon", R.drawable.set_item_about);
		map4.put("set_item_title", "关于多媒体问答系统");
		list.add(map4);

		System.out.println(list);
		return list;
	}

}
