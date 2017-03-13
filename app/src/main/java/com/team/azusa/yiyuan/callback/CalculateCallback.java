package com.team.azusa.yiyuan.callback;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.CalculateDto;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azusa on 2016/4/1.
 * 计算详情页面数据回调
 */
public abstract class CalculateCallback extends Callback<ArrayList<CalculateDto>> {

    @Override
    public ArrayList<CalculateDto> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Log.e("main",respon);
        ArrayList<CalculateDto> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("list").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<CalculateDto>>() {
        });
        return list;
    }
}


