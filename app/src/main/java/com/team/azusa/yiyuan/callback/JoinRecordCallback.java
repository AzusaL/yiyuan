package com.team.azusa.yiyuan.callback;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.JoinRecordDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azusa on 2016/1/21.
 * 参与记录的回调
 */
public abstract class JoinRecordCallback extends Callback<ArrayList<JoinRecordDto>> {

    @Override
    public ArrayList<JoinRecordDto> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        ArrayList<JoinRecordDto> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("joinRecordDto").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<JoinRecordDto>>() {
        });
        return list;
    }
}


