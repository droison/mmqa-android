package com.media.Adapter;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.media.Activity.QuestionViewActivity;
import com.media.Activity.R;
import com.media.info.Question;

public class BAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<Question> questions;
	private LayoutInflater mInflater;
	
	public BAdapter(Context mContext,List<Question> questions){
		super();
		this.mContext = mContext;
		this.questions = questions;
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
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.main_whole_item, null);
			viewHolder = new ViewHolder();

			viewHolder.questionId = (TextView) convertView.findViewById(R.id.main_whole_item_questionId);
			viewHolder.title = (TextView) convertView.findViewById(R.id.main_whole_item_title);
			viewHolder.main_attention_item_count = (TextView) convertView.findViewById(R.id.main_whole_item_count);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.questionId.setText(question.getId());
		viewHolder.title.setText(question.getTitle());
		viewHolder.main_attention_item_count.setText("(" + question.getCountAnswer()
						+ ")");
		
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
	
	class ViewHolder{
		TextView questionId;
		TextView title;
		TextView main_attention_item_count;
	}

}
