package com.media.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.media.Activity.R;
import com.media.Activity.VideoViewBActivity;
import com.media.Activity.VideoViewFActivity;
import com.media.info.Answer;

public class CAAdapter extends BaseAdapter{
	
	private Context mContext;
	private Answer[] answers;
	private LayoutInflater mInflater;
	
	public CAAdapter(Context mContext,Answer[] answers){
		super();
		this.mContext = mContext;
		this.answers = answers;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int i ;
		if(answers!=null){
		i = answers.length;}
		else{
			i = 0;
		}
		return i;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return answers[arg0];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Answer answer = answers[position];
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
		
		viewHolder.questionId.setText(answer.getId());
		viewHolder.title.setText(answer.getTitle());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent toVideoView = new Intent();
				String cameraInfo = answer.getVideo().getCameraInfo();
				if (cameraInfo.equals("android.front") || cameraInfo.equals("android.native")) {
					toVideoView.setClass(mContext,
							VideoViewFActivity.class);
				} else {
					toVideoView.setClass(mContext,
							VideoViewBActivity.class);
				}

				Bundle bundle = new Bundle();
				bundle.putString("videoId", answer.getVideo().getId());
				bundle.putString("videoUrl",answer.getVideo().getVideoUrl());
				toVideoView.putExtras(bundle);
				mContext.startActivity(toVideoView);
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
