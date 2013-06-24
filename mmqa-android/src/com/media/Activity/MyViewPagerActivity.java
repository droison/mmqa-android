package com.media.Activity;

import io.vov.utils.Log;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.constants.AppConstant;
import com.media.Activity.ImageActivity.MyHandler;
import com.media.Activity.ImageActivity.MyThread;
import com.media.Util.ImageUtil;
import com.media.Util.SaveImage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyViewPagerActivity extends Activity {
	private ViewPager viewPager;
	private List<ImageView> imageViews;
	private String[] titles;
	private List<View> dots;
	private int[] dos = new int[] { R.id.v_dot0, R.id.v_dot1, R.id.v_dot2,
			R.id.v_dot3, R.id.v_dot4 };
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private ArrayList<String> listPath = new ArrayList<String>();
	private Bitmap bitmap;
	private Handler myHandler;
	private SaveImage saveImage;
	private Bundle bundle;
	private static final int SAVE_OK = 0;
	private ProgressBar progressBar;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myviewpage);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		imageViews = new ArrayList<ImageView>();
		bundle = getIntent().getExtras();
		if (bundle.getStringArrayList("listPath") != null) {
			
			listPath = bundle.getStringArrayList("listPath");
			for (int i = 0; i < listPath.size(); i++) {
				ImageView imageView = new ImageView(this);
				bitmap = ImageUtil.getSmallBitmap(listPath.get(i));
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}
		} else {
			for (int i = 0; i < bundle.getStringArrayList("urllist").size(); i++) {
				String imageName = bundle
						.getStringArrayList("urllist")
						.get(i)
						.substring(
								bundle.getStringArrayList("urllist").get(i)
										.lastIndexOf("/"));
				String path = AppConstant.BASE_DIR_CACHE + imageName;
				listPath.add(path);
			}

			for (int j = 0; j < listPath.size(); j++) {
				File file = new File(listPath.get(j));
				ImageView imageView = new ImageView(this);
				imageView.setTag(listPath.get(j));
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}
			myHandler = new MyHandler();
			MyThread myThread = new MyThread();
			new Thread(myThread).start();
		}


		dots = new ArrayList<View>();
		for (int i = 0; i < listPath.size(); i++) {
			findViewById(dos[i]).setVisibility(View.VISIBLE);
			dots.add(findViewById(dos[i]));
		}

		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {
				bitmap = BitmapFactory.decodeFile(listPath.get(currentItem));
				imageViews.get(currentItem).setImageBitmap(bitmap);
				progressBar.setVisibility(View.GONE);
			}
		}
	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = myHandler.obtainMessage();
			File file = new File(AppConstant.BASE_DIR_CACHE,bundle.getStringArrayList("urllist").get(currentItem).substring(bundle.getStringArrayList("urllist")
					.get(currentItem).lastIndexOf("/")+1));
			System.out.println(bundle.getStringArrayList("urllist").get(currentItem).substring(bundle.getStringArrayList("urllist")
					.get(currentItem).lastIndexOf("/")+1));
			if(!file.exists()){
					try {
						URL url = new URL(bundle.getStringArrayList("urllist").get(
								currentItem));
						saveImage = new SaveImage(url);
						saveImage.saveVideoToLocal(bundle
								.getStringArrayList("urllist")
								.get(currentItem)
								.substring(
										bundle.getStringArrayList("urllist")
												.get(currentItem).lastIndexOf("/")));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			msg.what = MyViewPagerActivity.SAVE_OK;
			MyViewPagerActivity.this.myHandler.sendMessage(msg);
		}
	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		super.onStart();
	}

	@Override
	protected void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
			progressBar.setVisibility(0);
			myHandler = new MyHandler();
			MyThread myThread = new MyThread();
			new Thread(myThread).start();
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return listPath.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}