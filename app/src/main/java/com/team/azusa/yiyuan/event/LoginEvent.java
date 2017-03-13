package com.team.azusa.yiyuan.event;

/**
 * Created by Azusa on 2016/1/19.
 */
public class LoginEvent {
    private boolean loginSuccess;

    public LoginEvent(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

}
