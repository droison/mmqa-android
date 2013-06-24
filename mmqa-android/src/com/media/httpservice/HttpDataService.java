package com.media.httpservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.media.db.AccountInfoService;
import com.media.info.Account;
import com.media.info.AnswerUpload;
import com.media.info.Question;
import com.media.info.QuestionUpload;

public class HttpDataService {

	private HttpDataService() {

	}

	private static HttpDataService instance = null;

	public static String sessionId;
	private List<Question> attentionQs;
	private Account account;
	private AnswerUpload answerUpload;
	private QuestionUpload questionUpload;
	private static AccountInfoService accountInfoService;
	private String[] token = {"","",""};
	private long tokenTime = 0;

	public String[] getToken() {
		return token;
	}

	public void setToken(String[] token) {
		this.token = token;
	}

	public long getTokenTime() {
		return tokenTime;
	}

	public void setTokenTime(long tokenTime) {
		this.tokenTime = tokenTime;
	}

	public static HttpDataService getInstance(Context mContext) {
		if (instance == null)
		{
			instance = new HttpDataService();
			accountInfoService = new AccountInfoService(mContext);
			if(accountInfoService.isExist())
			{
				instance.setAccount(accountInfoService.getAccount());
				instance.setSessionId(accountInfoService.getSessionId());
			}
			
		}
		return instance;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<Question> getAttentionQs() {
		if (attentionQs == null)
			attentionQs = new ArrayList<Question>();
		return attentionQs;
	}

	public void setAttentionQs(List<Question> attentionQs) {
		this.attentionQs = attentionQs;
	}

	public Account getAccount() {
		if(account == null)
			account = new Account();
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AnswerUpload getAnswerUpload() {
		if (answerUpload == null)
			answerUpload = new AnswerUpload();
		return answerUpload;
	}

	public void setAnswerUpload(AnswerUpload answerUpload) {
		this.answerUpload = answerUpload;
	}

	public QuestionUpload getQuestionUpload() {
		if (questionUpload == null)
			questionUpload = new QuestionUpload();
		return questionUpload;
	}

	public void setQuestionUpload(QuestionUpload questionUpload) {
		this.questionUpload = questionUpload;
	}
	
}
