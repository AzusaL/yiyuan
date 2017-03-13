package com.team.azusa.yiyuan.callback;


import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.OrderProgressDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Azusa on 2016/1/21.
 * 我的晒单回调
 */
public abstract class OrderProgressCallback extends Callback<OrderProgressDto> {

    @Override
    public OrderProgressDto parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Log.e("main",respon);
        OrderProgressDto orderProgressDto;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("orderProgress").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        orderProgressDto = mapper.readValue(list_response, OrderProgressDto.class);
        return orderProgressDto;
    }
}


