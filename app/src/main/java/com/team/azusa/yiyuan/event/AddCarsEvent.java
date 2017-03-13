package com.team.azusa.yiyuan.event;

/**
 * Created by Azusa on 2016/1/30.
 */
public class AddCarsEvent {
    private int count;

    public AddCarsEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
