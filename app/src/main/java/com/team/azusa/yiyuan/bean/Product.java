package com.team.azusa.yiyuan.bean;

public class Product{

	private String id;
	private String title;
	private String titleImgUrl;
	private String content;
	private String contentImgUrl;
	private int value;
	private int num;
	private String xiangou;
	private Category category;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitleImgUrl() {
		return titleImgUrl;
	}
	public void setTitleImgUrl(String titleImgUrl) {
		this.titleImgUrl = titleImgUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentImgUrl() {
		return contentImgUrl;
	}
	public void setContentImgUrl(String contentImgUrl) {
		this.contentImgUrl = contentImgUrl;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getXiangou() {
		return xiangou;
	}
	public void setXiangou(String xiangou) {
		this.xiangou = xiangou;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	
	
	
}
