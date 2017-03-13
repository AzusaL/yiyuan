package com.team.azusa.yiyuan.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.User;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Azusa on 2016/4/17.
 * 用户对象回调
 */
public abstract class UserCallback extends Callback<User> {
    @Override
    public User parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        User user;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("user").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        user = mapper.readValue(list_response, User.class);
        return user;
    }
}
