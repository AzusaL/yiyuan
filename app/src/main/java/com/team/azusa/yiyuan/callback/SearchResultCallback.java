package com.team.azusa.yiyuan.callback;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Azusa on 2016/1/21.
 * 搜索页面回调接口
 */
public abstract class SearchResultCallback extends Callback<HashMap<String, Object>> {

    @Override
    public HashMap<String, Object> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<ProductDto> list;
        String product_response = null;
        String count_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            product_response = jsonObject.get("products").toString();
            count_response = jsonObject.get("count").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(product_response, new TypeReference<ArrayList<ProductDto>>() {
        });
        map.put("products", list);
        map.put("count", count_response);
        return map;
    }
}


