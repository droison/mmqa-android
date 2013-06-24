package com.media.Activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.constants.AppConstant;
import com.media.Adapter.AAdapter;
import com.media.Thread.ThreadExecutor;
import com.media.Util.ExitApplication;
import com.media.component.xlistview.XListView;
import com.media.component.xlistview.XListView.IXListViewListener;
import com.media.httpservice.HttpDataService;
import com.media.httpservice.QuestionService;
import com.media.info.Question;

public class AActivity extends AbstractMMQAActivity implements
		IXListViewListener {

	private XListView listView;
	private ImageButton newQuestion;
	private List<Question> questions = HttpDataService.getInstance(this).getAttentionQs();
	private Button GZHT;
	private Button YJJ;
	private Button WJJ;
	private boolean isRefresh;
	private boolean isLoad = false;
	private AAdapter adapter;
	private String listUrl;
	private long headTime;
	private long rearTime;
	private String username;
	public static Context context;
	

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (isChangeA) {
			refreshHandler(null);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// i++;
	}

	private void setUpView() {
		newQuestion = (ImageButton) findViewById(R.id.newQuestion);
		listView = (XListView) this.findViewById(R.id.listView);
		GZHT = (Button) findViewById(R.id.GZHT);
		YJJ = (Button) findViewById(R.id.YJJ);
		WJJ = (Button) findViewById(R.id.WJJ);

		GZHT.setBackgroundResource(R.drawable.white_btn_clicked);

		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
	}

	private void initData() {
		username = HttpDataService.getInstance(this).getAccount().getUsername();
		headTime = rearTime = new Date().getTime();
		listUrl = AppConstant.serverUrl + "question/getByTags";
	}

	private void addListener() {
		GZHT.setOnClickListener(onClick);
		WJJ.setOnClickListener(onClick);
		YJJ.setOnClickListener(onClick);
		newQuestion.setOnClickListener(new OnClickListener() {
			// @Override
			@Override
			public void onClick(View v) {
				Intent newQuestion = new Intent();
				newQuestion.setClass(getBaseContext(),
						NewQuestionActivity.class);
				startActivity(newQuestion);

			}
		});

		listView.setXListViewListener(this);
	}

	View.OnClickListener onClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			listView.setShowMoreNotice("查看更多");
			questions.clear();
			switch (v.getId()) {
			case R.id.GZHT:
				GZHT.setBackgroundResource(R.drawable.white_btn_clicked);
				WJJ.setBackgroundResource(R.drawable.white_btn);
				YJJ.setBackgroundResource(R.drawable.white_btn);
				listUrl = AppConstant.serverUrl + "question/getByTags";
				questions.clear();
				headTime = rearTime = new Date().getTime();
				ThreadExecutor.execute(new QuestionService(false, headTime, 10, listUrl, username, questionHandler, AActivity.this));
				break;
			case R.id.WJJ:
				GZHT.setBackgroundResource(R.drawable.white_btn);
				WJJ.setBackgroundResource(R.drawable.white_btn_clicked);
				YJJ.setBackgroundResource(R.drawable.white_btn);
				listUrl = AppConstant.serverUrl + "question/pageByIsComplete?isComplete=false";
				questions.clear();
				headTime = rearTime = new Date().getTime();
				ThreadExecutor.execute(new QuestionService(false, headTime, 10, listUrl, username, questionHandler, AActivity.this));
				break;
			case R.id.YJJ:
				GZHT.setBackgroundResource(R.drawable.white_btn);
				WJJ.setBackgroundResource(R.drawable.white_btn);
				YJJ.setBackgroundResource(R.drawable.white_btn_clicked);
				listUrl = AppConstant.serverUrl + "question/pageByIsComplete?isComplete=true";
				questions.clear();
				headTime = rearTime = new Date().getTime();
				ThreadExecutor.execute(new QuestionService(false, headTime, 10, listUrl, username, questionHandler, AActivity.this));
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_attention);

		setUpView();
		initData();
		addListener();
		refreshHandler(null);
	}

	public void refreshHandler(View v) {
		questions.clear();
		showProgressDialog("正在获取关注的问题，请稍等...");
		ThreadExecutor.execute(new QuestionService(false, 0, 10, listUrl, username, questionHandler, this));
		isChangeA = false;
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

				adapter = new AAdapter(AActivity.this, questions, listView);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dismissProgressDialog();
				if(isRefresh || isLoad)
				onLoad(list);

				break;

			case AppConstant.HANDLER_MESSAGE_NULL:
				break;
			case AppConstant.HANDLER_HTTPSTATUS_ERROR:

				break;

			default:
				break;
			}
		}

		/**
		 * 添加头列表
		 * 
		 * @param list
		 *            列表
		 */
		private void addHead(List<Question> list) {
			list = new ArrayList<Question>(list);
			list.addAll(questions);
			questions = list;
		}
	};

	private void onLoad(List<Question> list) {
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setRefreshTime("刚刚");
		if (isLoad == true) {
			if (list.size() != 0) {
				listView.setSelection(questions.size() -1);
			} else if (list.size() == 0) {
				listView.setSelection(questions.size() -1);
				listView.setShowMoreNotice("已到达底部");
			}
		}
		isRefresh = false;
		isLoad = false;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		isRefresh = true;
		isLoad = false;
		refreshHandler(null);
		//ThreadExecutor.execute(new QuestionService(false, headTime, 10, listUrl, username, questionHandler, this));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		isLoad = true;
		ThreadExecutor.execute(new QuestionService(false, rearTime, 10, listUrl, username, questionHandler, this));
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
