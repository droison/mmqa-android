package com.media.Adapter;

import java.util.HashMap;
import java.util.List;

import com.media.Activity.R;
import com.media.Activity.SettingFeedbackActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/*
 * author chaisong
 * 
 * 类说明，课程适配器
 * */

public class SetAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private List<HashMap<String, Object>> list;

	private Context mContext = null;

	public SetAdapter(List<HashMap<String, Object>> list, Activity activity) {
		super();
		this.list = list;
		this.mContext = activity;
		mInflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final HashMap<String, Object> info = list.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.set_item, null);
			viewHolder = new ViewHolder();

			viewHolder.setImg = (ImageView) convertView
					.findViewById(R.id.set_item_icon);
			viewHolder.setText = (TextView) convertView
					.findViewById(R.id.set_item_title);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.setImg
				.setImageResource((Integer) (info.get("set_item_icon")));
		viewHolder.setText.setText((String) (info.get("set_item_title")));

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				switch ((Integer) (info.get("set_item_icon"))) {
				case R.drawable.set_item_update:
					Toast.makeText(mContext, "已经是最新版了", Toast.LENGTH_SHORT)
							.show();
					break;
				case R.drawable.set_item_feedback:
					mContext.startActivity(new Intent(mContext,
							SettingFeedbackActivity.class));
					break;
				case R.drawable.set_item_about:
					new ShowAboutDialog(mContext).show();
					break;
				case R.drawable.set_item_weibo:
					Uri uri = Uri.parse("http://www.weibo.com/");
					Intent intent = new Intent(Intent.ACTION_VIEW,uri);
					mContext.startActivity(intent);
					break;

				default:
					break;
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView setImg;
		TextView setText;
	}

	class ShowAboutDialog extends AlertDialog {
		protected ShowAboutDialog(Context context) {
			super(context);
			final View view = getLayoutInflater().inflate(
					R.layout.set_about_dialog, null);
			setButton("关闭", (OnClickListener) null);
			setIcon(R.drawable.ic_launcher);
			setTitle(mContext.getText(R.string.app_name) + "测试版");
			this.setMessage("北邮教育技术研究所诚意出品");
			setView(view);
		}
	}

}
