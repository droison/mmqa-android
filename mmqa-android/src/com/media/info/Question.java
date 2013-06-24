package com.media.info;

import java.util.Date;
import java.util.List;

/**
 * 问题
 * 
 * @author chaisong
 * 
 */

public class Question extends View {
	private static final long serialVersionUID = -3286986862874761190L;

	/**
	 * 最新回答时间
	 */

	private Date answerTime;

	/**
	 * 问题标签，最多3个
	 */
	private List<String> tags;

	/**
	 * 所有的回答
	 */
	private List<Answer> answers;
	private String countAnswer;
	private String lastAnswerAuthor;
	private List<Answer> bestAnswer;
	private boolean beComplete;

	public String getLastAnswerAuthor() {
		return lastAnswerAuthor;
	}

	public void setLastAnswerAuthor(String lastAnswerAuthor) {
		this.lastAnswerAuthor = lastAnswerAuthor;
	}

	public String getCountAnswer() {
		return countAnswer;
	}

	public void setCountAnswer(String countAnswer) {
		this.countAnswer = countAnswer;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public Date getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Answer> getBestAnswer() {
		return bestAnswer;
	}

	public void setBestAnswer(List<Answer> bestAnswer) {
		this.bestAnswer = bestAnswer;
	}

	public boolean getBeComplete() {
		return beComplete;
	}

	public void setBeComplete(boolean beComplete) {
		this.beComplete = beComplete;
	}

}
