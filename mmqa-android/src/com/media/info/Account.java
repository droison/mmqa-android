package com.media.info;

import java.util.Date;
import java.util.List;

public class Account extends BaseEntity {

	private static final long serialVersionUID = -3550903881672154702L;

	private String username;

	private String password;

	private String role;

	private String name;

	private List<String> tags;

	private List<String> group;

	private String icon;

	private Date syntime;

	private String synId;

	public String getSynId() {
		return synId;
	}

	public void setSynId(String synId) {
		this.synId = synId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getGroup() {
		return group;
	}

	public void setGroup(List<String> group) {
		this.group = group;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getSyntime() {
		return syntime;
	}

	public void setSyntime(Date syntime) {
		this.syntime = syntime;
	}

	@Override
	public String toString() {
		return "{\"id\":\"" + getId() + "\",\"name\":\"" + getName()
				+ "\",\"role\":\"" + getRole() + "\",\"tags\":" + getTags()
				+ "}";
	}

}
