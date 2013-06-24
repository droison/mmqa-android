package com.media.Util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.media.info.Account;
import com.media.info.Answer;
import com.media.info.Question;

import android.content.Context;
import android.util.Log;

/*
 * author chaisong
 * 
 * 类说明：解析由服务器得到的json数据
 * */

public class JsonUtil {

	/**
	 * Define a TAG
	 */
	private static final String TAG = "JSONUTIL";

	// 获取课程列表
	public static List<Question> getQuestionList(String json, Context mContext) {
		List<Question> questions = new ArrayList<Question>();
		try {
			JSONArray ja = new JSONArray(json);
			for (int i = 0; i < ja.length(); i++) {
				questions.add(getQuestionInfo(ja.getJSONObject(i).toString(), mContext));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return questions;
	}

	// 获取课程列表
	public static List<Answer> getAnswerList(String json, Context mContext) {
		List<Answer> answers = new ArrayList<Answer>();
		try {
			JSONArray ja = new JSONArray(json);
			for (int i = 0; i < ja.length(); i++) {
				answers.add(getAnswerInfo(ja.getJSONObject(i).toString(), mContext));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return answers;
	}

	public static Question getQuestionInfo(String json, Context mContext) {

		Question q = JSON.parseObject(json, Question.class);

		return q;
	}

	public static Answer getAnswerInfo(String json, Context mContext) {

		Answer a = JSON.parseObject(json, Answer.class);

		return a;
	}

	public static Account getAccoutInfo(String json, Context mContext) {
		Account a = JSON.parseObject(json, Account.class);
		List<String> tags = Sort.sortByChina(a.getTags());
		a.setTags(tags);
		return a;
	}

	public static Question[] getQuestionArray(String json, Context mContext) {
		List<Question> questionList = getQuestionList(json, mContext);
		int size = questionList.size();
		if (size == 0) {
			return null;
		} else {
			Question[] questions = new Question[size];
			for (int i = 0; i < size; i++) {
				questions[i] = questionList.get(i);
			}
			return questions;
		}

	}

	public static Answer[] getAnswerArray(String json, Context mContext) {
		List<Answer> answerList = getAnswerList(json, mContext);
		int size = answerList.size();
		if (size == 0) {
			return null;
		} else {
			Answer[] answers = new Answer[size];
			for (int i = 0; i < size; i++) {
				answers[i] = answerList.get(i);
			}
			return answers;
		}

	}

}
