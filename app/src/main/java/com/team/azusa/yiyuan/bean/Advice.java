package com.team.azusa.yiyuan.bean;

import java.util.Date;


public class Advice {

    private String id;
    private String type;            //建议类型(0:投诉与建议,1:商品配送,2:售后服务)
    private String telephone;        //联系电话
    private String comment;            //意见
    private String phoneDetail;        //手机详细信息
    private int flag;                //0未解决，1已解决
    private Date time;                //时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhoneDetail() {
        return phoneDetail;
    }

    public void setPhoneDetail(String phoneDetail) {
        this.phoneDetail = phoneDetail;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
