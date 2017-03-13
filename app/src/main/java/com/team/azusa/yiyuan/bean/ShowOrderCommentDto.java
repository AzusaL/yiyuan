package com.team.azusa.yiyuan.bean;

import java.util.Date;

public class ShowOrderCommentDto {

	private String id;					//评论ID
	private String content;				//评论内容
	private Date time;					//评论时间
	private String commentUserID;		//评论者ID
	private String commentUserName;		//评论者姓名
	private String commentUserImgUrl;	//评论者头像
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getCommentUserID() {
		return commentUserID;
	}
	public void setCommentUserID(String commentUserID) {
		this.commentUserID = commentUserID;
	}
	public String getCommentUserName() {
		return commentUserName;
	}
	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}
	public String getCommentUserImgUrl() {
		return commentUserImgUrl;
	}
	public void setCommentUserImgUrl(String commentUserImgUrl) {
		this.commentUserImgUrl = commentUserImgUrl;
	}
	
	
}
