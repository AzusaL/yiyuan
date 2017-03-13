package com.team.azusa.yiyuan.callback;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.Advert;
import com.team.azusa.yiyuan.bean.JieXiaoDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 *  Created by Azusa on 2016/4/7.
 *  首页广告位回调接口
 */
public abstract class AdvertCallback extends Callback<ArrayList<Advert>> {

    @Override
    public ArrayList<Advert> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Log.e("main",respon);
        ArrayList<Advert> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("list").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<Advert>>() {
        });
        return list;
    }
}
