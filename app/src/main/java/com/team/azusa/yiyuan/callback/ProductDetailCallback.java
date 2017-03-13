package com.team.azusa.yiyuan.callback;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.bean.YunNumDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Azusa on 2016/1/21.
 * 获得商品详细信息的回调
 */
public abstract class ProductDetailCallback extends Callback<YunNumDto> {
    @Override
    public YunNumDto parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Log.e("main",""+respon);
        YunNumDto yunNumDto;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("yunNumDto").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        yunNumDto = mapper.readValue(list_response, YunNumDto.class);
        return yunNumDto;
    }
}


