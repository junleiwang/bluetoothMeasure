package com.example.ccqzy.beans;

public class NotifyNewsInfo {
	private String id;
	private String name;
	private boolean isImportance;
	private boolean isAttention;
	private String creator;
	private String noticeReaderId;
	private String readStatus;
	private String publishDate;
	//private Attaches attaches;
	private String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getNoticeReaderId() {
		return noticeReaderId;
	}
	public void setNoticeReaderId(String noticeReaderId) {
		this.noticeReaderId = noticeReaderId;
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
//	public Attaches getAttaches() {
//		return attaches;
//	}
//	public void setAttaches(Attaches attaches) {
//		this.attaches = attaches;
//	}
	@Override
	public String toString() {
		return "NotifyNewsInfo [id=" + id + ", name=" + name
				+ ", isImportance=" + isImportance + ", isAttention="
				+ isAttention + ", creator=" + creator + ", noticeReaderId="
				+ noticeReaderId + ", readStatus=" + readStatus
				+ ", publishDate=" + publishDate + ", attaches="
				+ ", content=" + content + "]";
	}
	
	
	
}
