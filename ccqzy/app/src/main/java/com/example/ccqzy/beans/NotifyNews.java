package com.example.ccqzy.beans;

public class NotifyNews {
	private String id;
	private String name;
	private String readerId;
	private boolean isImportance;
	private boolean isAttention;
	private String creator;
	private String readStatus;
	private String publishDate;
	private String imguri;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isImportance() {
		return isImportance;
	}
	public void setIsImportance(boolean isImportance) {
		this.isImportance = isImportance;
	}
	public boolean isAttention() {
		return isAttention;
	}
	public void setIsAttention(boolean isAttention) {
		this.isAttention = isAttention;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getReadStatus() {
		return readStatus;
	}
	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getImguri() {
		return imguri;
	}
	public void setImguri(String imguri) {
		this.imguri = imguri;
	}




}
