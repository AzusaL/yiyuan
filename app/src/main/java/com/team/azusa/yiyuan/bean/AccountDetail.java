package com.team.azusa.yiyuan.bean;

import java.util.Date;

public class AccountDetail{

    private String id;
    private int money;
    private String type;
    private String qudao;
    private Date time;

    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQudao() {
        return qudao;
    }

    public void setQudao(String qudao) {
        this.qudao = qudao;
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


}
