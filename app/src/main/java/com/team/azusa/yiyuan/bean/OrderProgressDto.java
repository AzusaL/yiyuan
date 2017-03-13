package com.team.azusa.yiyuan.bean;

import java.util.Date;


public class OrderProgressDto {

    private String id;	//订单进度id
    private int flag;	//0为订单未确认,1为订单已确认
    private Date distributionAddrDate;		//配送信息确认时间
    private String rough;			//收货地址
    private String datail;			//详细地址
    private String postCompany;		 // 快递公司
    private String postId; 		// 快递单号
    private Date postDate; 		// 快递日期
    private String title;		//商品标题
    private int confirm;		//0为未确认收货,1为确认收货
    private Date confirmDate;	//确认收货时间
    private String name;		//收货人姓名
    private String mobile;	//收货人手机

    public OrderProgressDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Date getDistributionAddrDate() {
        return distributionAddrDate;
    }

    public void setDistributionAddrDate(Date distributionAddrDate) {
        this.distributionAddrDate = distributionAddrDate;
    }

    public String getRough() {
        return rough;
    }

    public void setRough(String rough) {
        this.rough = rough;
    }

    public String getDatail() {
        return datail;
    }

    public void setDatail(String datail) {
        this.datail = datail;
    }

    public String getPostCompany() {
        return postCompany;
    }

    public void setPostCompany(String postCompany) {
        this.postCompany = postCompany;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
