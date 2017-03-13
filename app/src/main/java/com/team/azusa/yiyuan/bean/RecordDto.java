package com.team.azusa.yiyuan.bean;


public class RecordDto {

	private long time;				//购买时间
	private String yunCode;			//云购码
	private int buyNum;			//云购次数
	
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getYunCode() {
		return yunCode;
	}
	public void setYunCode(String yunCode) {
		this.yunCode = yunCode;
	}
	public int getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	
	
}
