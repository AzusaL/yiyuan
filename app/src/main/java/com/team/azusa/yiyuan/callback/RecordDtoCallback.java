package com.team.azusa.yiyuan.callback;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Azusa on 2016/1/21.
 */
public abstract class RecordDtoCallback extends Callback<HashMap<String, Object>> {

    @Override
    public HashMap<String, Object> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<RecordDto> list;
        String yuncount;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("list").toString();
            yuncount = jsonObject.get("count").toString();
            map.put("count", yuncount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<RecordDto>>() {
        });
        map.put("record", list);
        int buyNum = 0;
        for (RecordDto dto : list) {
            buyNum += dto.getBuyNum();
        }
        map.put("buyNum", buyNum);
        return map;
    }
}


