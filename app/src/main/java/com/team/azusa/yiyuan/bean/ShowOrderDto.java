package com.team.azusa.yiyuan.bean;

import java.util.Date;

public class ShowOrderDto {

    private String userId;                //用户ID
    private String userName;            //用户名
    private String userImgUrl;            //用户头像

    private String showOrderId;            //ID
    private String title;                //晒单标题
    private String content;                //内容
    private String imgUrl;                //图片，由多个图片的URL通过","连接起来
    private int zan;                    //赞个数
    private Date time;                    //晒单时间
    private int commentCount;            //评论个数
    private String productName;        //商品名字

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getShowOrderId() {
        return showOrderId;
    }

    public void setShowOrderId(String showOrderId) {
        this.showOrderId = showOrderId;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }


}
