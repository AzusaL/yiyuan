package com.team.azusa.yiyuan.bean;


import java.util.ArrayList;

public class BuyRecordInfo {

    private String yunNumId;                  //云期数ID
    private String productId;                //商品ID
    private String proImgUrl;                //商品缩略图
    private String title;                    //商品名称
    private String xianGou;                    //是否为限购，1是，0否
    private int value;                        //商品价值
    private int yunNum;                        //商品期数
    private String status;                    //状态 ，0为进行中，1为人数已满未开奖，2为已开奖
    private int buyNum;                        //参与次数
    private String winnerId;                //获奖者ID
    private String winnerName;                //获奖者名字
    private String winnerImgUrl;            //头像
    private long jieXiaoTime;                //揭晓时间
    private String winCode;                    //中奖码（幸运云购码）
    private String buyRecordId;               //购买记录的id

    public String getBuyRecordId() {
        return buyRecordId;
    }

    public void setBuyRecordId(String buyRecordId) {
        this.buyRecordId = buyRecordId;
    }

    public String getYunNumId() {
        return yunNumId;
    }

    public void setYunNumId(String yunNumId) {
        this.yunNumId = yunNumId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

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

    public long getJieXiaoTime() {
        return jieXiaoTime;
    }

    public void setJieXiaoTime(long jieXiaoTime) {
        this.jieXiaoTime = jieXiaoTime;
    }

    public String getWinCode() {
        return winCode;
    }

    public void setWinCode(String winCode) {
        this.winCode = winCode;
    }

    public String getWinnerImgUrl() {
        return winnerImgUrl;
    }

    public void setWinnerImgUrl(String winnerImgUrl) {
        this.winnerImgUrl = winnerImgUrl;
    }

}
