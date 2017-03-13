package com.team.azusa.yiyuan.bean;

import java.util.Date;

public class ShowOrder{

	private static final long serialVersionUID = 1L;
	private String id; // ID
	private String title; // 晒单标题
	private String content; // 内容
	private String imgUrl; // 图片，由多个图片的URL通过","连接起来
	private int zan; // 赞个数
	private Date time; // 晒单时间

	private User user;
	private Product product;

	public ShowOrder() {
	}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
