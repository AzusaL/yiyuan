package com.team.azusa.yiyuan.bean;

import java.io.Serializable;

public class YunNumDto implements Serializable{

	private String yunNumId;				//期数的ID
	private String productId;				//商品ID
	private String proImgUrl;				//商品缩略图
	private String title;					//商品名称
	private String content;					//商品详细信息
	private String contentImgUrl;			//图文详情的URL
	private String xianGou;					//是否为限购，1是，0否
	private int value;						//商品价值
	private int yunNum;						//商品期数
	private int buyNum;						//已购买次数
	private int totalNum;						//总购买次数
	private String status;					//状态 ，0为进行中，1为人数已满未开奖，2为已开奖

	private int daojishi;					//倒计时

	private long showOrderCount;			//晒单数

	private WinnerDto winner;				//获奖者
	private int count;
	private ProductDto productDto = new ProductDto();
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProImgUrl() {
		return proImgUrl;
	}
	public void setProImgUrl(String proImgUrl) {
		this.proImgUrl = proImgUrl;
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
	public String getContentImgUrl() {
		return contentImgUrl;
	}
	public void setContentImgUrl(String contentImgUrl) {
		this.contentImgUrl = contentImgUrl;
	}
	public String getXianGou() {
		return xianGou;
	}
	public void setXianGou(String xianGou) {
		this.xianGou = xianGou;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getYunNum() {
		return yunNum;
	}
	public void setYunNum(int yunNum) {
		this.yunNum = yunNum;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	public int getDaojishi() {
		return daojishi;
	}
	public void setDaojishi(int daojishi) {
		this.daojishi = daojishi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public long getShowOrderCount() {
		return showOrderCount;
	}
	public void setShowOrderCount(long showOrderCount) {
		this.showOrderCount = showOrderCount;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public WinnerDto getWinner() {
		return winner;
	}
	public void setWinner(WinnerDto winner) {
		this.winner = winner;
	}
	public String getYunNumId() {
		return yunNumId;
	}
	public void setYunNumId(String yunNumId) {
		this.yunNumId = yunNumId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public ProductDto getProductDto(){
		productDto.setProductId(productId);
		productDto.setYunNumId(yunNumId);
		productDto.setTitle(title);
		productDto.setImgUrl(proImgUrl);
		productDto.setXianGou(xianGou);
		productDto.setDaojishi(daojishi);
		productDto.setYunNum(yunNum);
		productDto.setValue(value);
		productDto.setBuyNum(buyNum);
		productDto.setTotalNum(totalNum);
		return productDto;
	}
}
