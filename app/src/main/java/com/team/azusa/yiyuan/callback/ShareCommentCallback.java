package com.team.azusa.yiyuan.callback;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.bean.ShowOrderCommentDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azusa on 2016/1/21.
 * 晒单评论的回调
 */
public abstract class ShareCommentCallback extends Callback<ArrayList<ShowOrderCommentDto>> {

    @Override
    public ArrayList<ShowOrderCommentDto> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Log.e("main",respon);
        ArrayList<ShowOrderCommentDto> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("comments").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<ShowOrderCommentDto>>() {
        });
        return list;
    }
}


