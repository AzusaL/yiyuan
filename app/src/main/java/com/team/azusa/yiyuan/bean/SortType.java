package com.team.azusa.yiyuan.bean;


/**
 * Created by Azusa on 2016/1/23.
 */
public class SortType {
    private String stringdatas;
    private int imgid;
    private int imgcheckid;
    private boolean ischeck;

    public SortType(String stringdatas, int imgcheckid, int imgid, boolean ischeck) {
        this.stringdatas = stringdatas;
        this.imgcheckid = imgcheckid;
        this.imgid = imgid;
        this.ischeck = ischeck;
    }

    public SortType(String stringdatas, boolean ischeck) {
        this.stringdatas = stringdatas;
        this.ischeck = ischeck;
    }

    public int getImgcheckid() {
        return imgcheckid;
    }

    public void setImgcheckid(int imgcheckid) {
        this.imgcheckid = imgcheckid;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getStringdatas() {
        return stringdatas;
    }

    public int getImgid() {
        return imgid;
    }

    public void setStringdatas(String stringdatas) {
        this.stringdatas = stringdatas;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }
}
