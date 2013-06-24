package com.media.info;

public class Image extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6210081498407784018L;

	private String fileSize;
	private int weith;
	private int high;
	private String imageUrl;// 新增截图属性
	private String imageThumbUrl;

	public String getImageThumbUrl() {
		return imageThumbUrl;
	}

	public void setImageThumbUrl(String imageThumbUrl) {
		this.imageThumbUrl = imageThumbUrl;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public int getWeith() {
		return weith;
	}

	public void setWeith(int weith) {
		this.weith = weith;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
