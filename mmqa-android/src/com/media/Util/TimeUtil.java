package com.media.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtil {
	public static String converTime(Date date){
		long currentSeconds = System.currentTimeMillis()/1000;  //由于这里返回UTC 1970 年 1 月 1 日午夜开始经过的毫秒数
		//String dd = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
		long timestap = date.getTime()/1000;/*Long.parseLong(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date));*/
		long timeGap = currentSeconds-timestap;
		String timeStr = null;
		if(timeGap>24*60*60){
			timeStr = timeGap/(24*60*60) + "天前";
			
		}else if(timeGap>60*60){
			timeStr = timeGap/(60*60) + "小时前";
		}else if(timeGap>60){
			timeStr = timeGap/60 + "分钟前";
		}else{
			timeStr = "刚刚";
		}
		return timeStr;
		
	}

}
