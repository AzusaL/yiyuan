package com.team.azusa.yiyuan.callback;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.ShowOrderDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azusa on 2016/1/21.
 * 我的晒单回调
 */
public abstract class ShowOrderCallback extends Callback<ArrayList<ShowOrderDto>> {

    @Override
    public ArrayList<ShowOrderDto> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        ArrayList<ShowOrderDto> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("showOrders").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<ShowOrderDto>>() {
        });
        return list;
    }
}


