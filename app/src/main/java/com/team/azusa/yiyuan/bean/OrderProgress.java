package com.team.azusa.yiyuan.bean;

import java.util.Date;

/**
 * Created by Azusa on 2016/3/22.
 */
public class OrderProgress {
    private String message;  //订单进度更新的信息
    private Date time;  //订单进度更新的时间

    public OrderProgress() {
    }

    public OrderProgress(String message, Date time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
