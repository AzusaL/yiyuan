package com.team.azusa.yiyuan.bean;

public class Advert {

    private String id;
    private String url; // 图片地址
    private String key_word; // 关键词
    private int flag; // 0为未开启，1为开启

    public Advert() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey_word() {
        return key_word;
    }

    public void setKey_word(String key_word) {
        this.key_word = key_word;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
