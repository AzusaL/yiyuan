package com.team.azusa.yiyuan.bean;

import java.util.Date;
import java.util.List;

public class WinnerDto {

	private String winnerId;				//获奖者ID
	private String winnerName;				//获奖者名字
	private String winnerImgUrl;			//获奖者头像
	private String address;					//获奖者地址
	private long jieXiaoTime;				//揭晓时间
	private long yunGouTime;				//云购时间
	private String winCode;					//中奖码（幸运云购码）
	private List<RecordDto> recordDto;		//购买记录
	
	public String getWinnerId() {
		return winnerId;
	}
	public void setWinnerId(String winnerId) {
		this.winnerId = winnerId;
	}
	public String getWinnerName() {
		return winnerName;
	}
	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}
	public String getWinnerImgUrl() {
		return winnerImgUrl;
	}
	public void setWinnerImgUrl(String winnerImgUrl) {
		this.winnerImgUrl = winnerImgUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getJieXiaoTime() {
		return jieXiaoTime;
	}
	public void setJieXiaoTime(long jieXiaoTime) {
		this.jieXiaoTime = jieXiaoTime;
	}
	public long getYunGouTime() {
		return yunGouTime;
	}
	public void setYunGouTime(long yunGouTime) {
		this.yunGouTime = yunGouTime;
	}
	public String getWinCode() {
		return winCode;
	}
	public void setWinCode(String winCode) {
		this.winCode = winCode;
	}
	public List<RecordDto> getRecordDto() {
		return recordDto;
	}
	public void setRecordDto(List<RecordDto> recordDto) {
		this.recordDto = recordDto;
	}
	
}
