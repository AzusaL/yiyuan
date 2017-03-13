package com.team.azusa.yiyuan.event;

/**
 * Created by Azusa on 2016/1/24.
 */
public class SearchTextEven {
    private String search_key;

    public SearchTextEven() {
    }

    public SearchTextEven(String search_key) {
        this.search_key = search_key;
    }

    public String getSearch_key() {
        return search_key;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }
}
