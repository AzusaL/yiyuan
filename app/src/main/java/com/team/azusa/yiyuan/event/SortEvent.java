package com.team.azusa.yiyuan.event;

/**
 * Created by Azusa on 2016/1/23.
 */
public class SortEvent {
    private String sort; //分类
    private int what;  //区分发送的是分类还是排序

    public SortEvent(String sort, int what) {
        this.sort = sort;
        this.what = what;
    }

    public String getSort() {
        return sort;
    }

    public int getWhat() {
        return what;
    }

}
