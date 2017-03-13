package com.team.azusa.yiyuan.bean;


public class JoinRecordDto {

    public String joinUserId;                  //参与者ID
    public String joinUserName;                //参与者姓名
    public String joinUserImgUrl;             //参与者头像
    public String joinUseraddress;            //参与者地址
    public int buyNum;                         //购买次数
    public long joinTime;                     //参与时间
    public String productTitle;				//商品名称
    public int yunNum;					//云期数

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getYunNum() {
        return yunNum;
    }

    public void setYunNum(int yunNum) {
        this.yunNum = yunNum;
    }

    public String getJoinUserId() {
        return joinUserId;
    }

    public void setJoinUserId(String joinUserId) {
        this.joinUserId = joinUserId;
    }

    public String getJoinUserName() {
        return joinUserName;
    }

    public void setJoinUserName(String joinUserName) {
        this.joinUserName = joinUserName;
    }

    public String getJoinUserImgUrl() {
        return joinUserImgUrl;
    }

    public void setJoinUserImgUrl(String joinUserImgUrl) {
        this.joinUserImgUrl = joinUserImgUrl;
    }

    public String getJoinUseraddress() {
        return joinUseraddress;
    }

    public void setJoinUseraddress(String joinUseraddress) {
        this.joinUseraddress = joinUseraddress;
    }


    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

}
