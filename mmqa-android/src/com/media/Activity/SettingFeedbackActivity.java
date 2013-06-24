
package com.media.Activity;

import com.media.mtj.BaiduSDK;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingFeedbackActivity extends BaiduSDK {
    private TextView titlebar_text;

    private Button feedbackReturn;

    private Button feedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_feedback);
        setView();
        addListener();
    }

    private void setView() {
        titlebar_text = (TextView)findViewById(R.id.title);
        titlebar_text.setText("意见反馈");
        feedbackReturn = (Button)findViewById(R.id.left_button);
        feedbackReturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        feedbackButton = (Button)findViewById(R.id.feedback_button);

    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    private void addListener() {
        feedbackReturn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        feedbackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(SettingFeedbackActivity.this, "您的意见已收到，谢谢", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });
    }
}
