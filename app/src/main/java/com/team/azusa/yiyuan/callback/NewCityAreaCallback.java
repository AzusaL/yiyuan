package com.team.azusa.yiyuan.callback;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.ProvCityAreaStreet;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 我的新添加地址获取省市区级联回调
 */
public abstract class NewCityAreaCallback extends Callback<ArrayList<ProvCityAreaStreet>> {

    @Override
    public ArrayList<ProvCityAreaStreet> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        ArrayList<ProvCityAreaStreet> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("list").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<ProvCityAreaStreet>>() {
        });
        return list;
    }
}


