package com.media.info;

import java.util.Date;
import java.util.List;

/**
 * 问题
 * 
 * @author suzhj
 * 
 */
abstract public class View extends BaseEntity {

	private static final long serialVersionUID = -8691875913808917442L;

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;

	/**
	 * 视频信息
	 */
	private Video video;
	
	private Voice voice;
	
	private List<Image> images;

	/**
	 * 作者登陆帐号
	 */
	private String author;

	/**
	 * 头像
	 */
	private String icon;
	/**
	 * 作者姓名
	 */
	private String authorName;

	/**
	 * 作者角色
	 */
	private String role;
	
	/**
	 * view类型，1：video，2：图片，3、图片+语音，4：语音，5：文字   只有回答有5，问题没有
	 */
	private int viewType;

	/**
	 * 作者发表时间
	 */
	private Date createTime = new Date();

	/**
	 * 作者更新内容时间
	 */
	private Date updateTime;

	
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
		
	public Voice getVoice() {
		return voice;
	}

	public void setVoice(Voice voice) {
		this.voice = voice;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

}
