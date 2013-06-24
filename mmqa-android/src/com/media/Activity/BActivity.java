package com.media.Activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.constants.AppConstant;
import com.media.Adapter.BAdapter;
import com.media.Thread.ThreadExecutor;
import com.media.Util.ExitApplication;
import com.media.Util.JsonUtil;
import com.media.Util.StringUtil;
import com.media.component.xlistview.XListView;
import com.media.component.xlistview.XListView.IXListViewListener;
import com.media.db.AccountInfoService;
import com.media.httpservice.HTTP;
import com.media.httpservice.HttpDataService;
import com.media.httpservice.HttpResponseEntity;
import com.media.httpservice.QuestionService;
import com.media.info.Question;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BActivity extends AbstractMMQAActivity implements IXListViewListener{

	private XListView listView;
	private List<Question> questions = new ArrayList<Question>();
	private AccountInfoService accountInfoService;
	private LinearLayout layout_search;
	private String key;
	private long headTime;
	private long rearTime;
	private BAdapter bAdapter;
	private boolean isRefresh;
	private boolean isLoad;
	private String listUrl;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isChangeB) {
			refreshHandler(null);
			isChangeB = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_whole);
		listView = (XListView) findViewById(R.id.main_whole_ListView);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		listView.setXListViewListener(this);
		layout_search = (LinearLayout) findViewById(R.id.layout_search);
		accountInfoService = new AccountInfoService(this);
		key = accountInfoService.getSessionId();
		layout_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toSearchActivity();
			}
		});
		headTime = rearTime = new Date().getTime();
		listUrl = AppConstant.serverUrl + "question/getAll";

		refreshHandler(null);		
	}

	public void refreshHandler(View v) {
		questions.clear();
		isLoad = false;
		ThreadExecutor.execute(new QuestionService(false, 0, 20, listUrl, "",questionHandler , BActivity.this));
		isChangeB = false;
		showProgressDialog("正在获取关注的问题，请稍等...");
	}

	public void toSearchActivity() {
		Intent toSearch = new Intent(getApplicationContext(),
				SearchActivity.class);
		startActivity(toSearch);
	}
	
	private Handler questionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConstant.HANDLER_MESSAGE_NORMAL:
				List<Question> list = (List<Question>) msg.obj;
				if (questions == null || questions.size() == 0) {
					questions.addAll(list);
					if(list.size()>0){
						headTime = list.get(0).getUpdateTime().getTime();
						rearTime = list.get(list.size()-1).getUpdateTime().getTime();	
					}				
				} else if (isRefresh == true) {
					addHead(list);
					if(list.size()>0)
						headTime = list.get(0).getUpdateTime().getTime();
				} else {
					if (isLoad == true) {
						questions.addAll(list);
						if(list.size()>0)
							rearTime = list.get(list.size()-1).getUpdateTime().getTime();
					}
				}

				bAdapter = new BAdapter(BActivity.this, questions);
				listView.setAdapter(bAdapter);
				bAdapter.notifyDataSetChanged();
				dismissProgressDialog();
				if(isRefresh || isLoad)
				onLoad(list);

				break;

			case AppConstant.HANDLER_MESSAGE_NULL:
				listView.stopRefresh();
				break;

			case AppConstant.HANDLER_HTTPSTATUS_ERROR:

				break;

			default:
				break;
			}
		}
		private void addHead(List<Question> list) {
			list = new ArrayList<Question>(list);
			list.addAll(questions);
			questions = list;
		}

};

	@Override
	public void onRefresh() {
		isRefresh = true;
		ThreadExecutor.execute(new QuestionService(true, headTime, 20, listUrl, "",questionHandler , BActivity.this));
	}

	@Override
	public void onLoadMore() {
		isLoad = true;
		ThreadExecutor.execute(new QuestionService(false, rearTime, 20, listUrl, "",questionHandler , BActivity.this));
	}
	
	private void onLoad(List<Question> list) {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
		if (isLoad == true) {
			if (list.size() != 0) {
				listView.setSelection(questions.size() -1);
			} else if (list.size() == 0) {
				listView.setSelection(questions.size()-1);
				listView.setShowMoreNotice("已到达底部");
			}
		}
		isRefresh = false;
		isLoad = false;
	}
	
	private long timeStampe = 0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(timeStampe == 0){		
			timeStampe = System.currentTimeMillis();
			Toast.makeText(getApplicationContext(), "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
		}else{
			if(System.currentTimeMillis() - timeStampe < 2000){
				ExitApplication.getInstance().exit();
			}else{
				timeStampe = 0;
			}
		}
	}
}
