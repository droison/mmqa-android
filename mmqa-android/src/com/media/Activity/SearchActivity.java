package com.media.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constants.AppConstant;
import com.media.info.Question;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Context;
import android.content.Intent;

public class SearchActivity extends AbstractMMQAActivity{

	private EditText search_key;
	private Button search_button;
	private ListView search_listView;
	private List<Question> questions;
	private String titleLike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
		search_key = (EditText) findViewById(R.id.search_key);
		search_button = (Button) findViewById(R.id.search_button);
		search_listView = (ListView) findViewById(R.id.search_listView);

		search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				titleLike = search_key.getText().toString();
				Log.v("search_key", titleLike);
				if (TextUtils.isEmpty(titleLike)) {
					displayResponse("请输入要查询的内容");
				} else {
					new FetchSecuredResourceTask().execute();
				}
			}
		});

		search_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) arg0
						.getItemAtPosition(arg2);
				String questionId = map.get("videoId").toString();
				Question question = searchQuestion(questions, questionId);
				Intent toQuestionView = new Intent();
				toQuestionView.setClass(getBaseContext(),
						QuestionViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("question", question);
				toQuestionView.putExtras(bundle);
				startActivity(toQuestionView);
			}
		});

	}

	public Integer searchByTitleLike(String title) {
		Integer i = 0;
		try {
			String url = AppConstant.serverUrl + "question/pageByTitleLike/0?&title=" + title;
			Log.d("questions", questions.toString());
		} catch (Exception e) {
			Log.e("SearchActivity", e.toString());
			i = 1;
		}
		return i;
	}

	private class FetchSecuredResourceTask extends
			AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("正在搜索，请稍等...");
		}

		@Override
		protected Integer doInBackground(Void... params) {

			Integer i = searchByTitleLike(titleLike);
			return i;
		}

		@Override
		protected void onPostExecute(Integer i) {
			dismissProgressDialog();
			switch (i) {
			case 1:
				displayResponse("搜索失败，请重试");
				break;
			case 0:
				setListView(questions);
				InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 输入法弹出则关闭，关闭则弹出
				search_key.setText("");
				break;
			default:
				displayResponse("系统错误，请重启应用重试");
				break;
			}
		}
	}

	private void setListView(List<Question> q) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (Question question : q) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("img",
						(question.getBeComplete()) ? R.drawable.ic_duihao
								: R.drawable.ic_wenhao);
				item.put("videoId", question.getId());
				item.put("title",
						question.getTitle() );
				item.put("count",  "(" + question.getCountAnswer()
						+ ")");
				item.put("tags", question.getTags());
				item.put("create_cnName", question.getAuthorName() + " ");
				item.put("createTime",
						dateFormat.format(question.getCreateTime()));
				item.put("lastAnswer_cnName", question.getLastAnswerAuthor()
						+ " ");
				item.put("lastAnswerTime",
						dateFormat.format(question.getAnswerTime()));
				data.add(item);
			}
			SimpleAdapter adapter = new SimpleAdapter(this, data,
					R.layout.main_attention_item,
					new String[] { "img", "videoId", "title", "count","tags",
							"create_cnName", "createTime", "lastAnswer_cnName",
							"lastAnswerTime" }, new int[] {
							R.id.isCompleteImage, R.id.questionId, R.id.title,R.id.main_attention_item_count,
							R.id.tag1, R.id.create_cnName, R.id.createTime,
							R.id.lastAnswer_cnName, R.id.lastAnswerTime });
			search_listView.setAdapter(adapter);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("ListView加载", e.getMessage());
			displayResponse("列表加载失败，请刷新重试");
		}
	}

	public Question searchQuestion(List<Question> listQ, String questionId) {
		Question question = new Question();
		int i = 0;
		while (i < listQ.size()) {
			if (listQ.get(i).getId().equals(questionId)) {
				question = listQ.get(i);
				break;
			}
			i++;
		}
		return question;
	}

}
