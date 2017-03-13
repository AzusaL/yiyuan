package com.team.azusa.yiyuan.callback;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.AddressMessage;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Delete_exe on 2016/3/7.
 * 我的收货地址回调
 */
public abstract class MyAddressCallback extends Callback<ArrayList<AddressMessage>> {
    @Override
    public ArrayList<AddressMessage> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        ArrayList<AddressMessage> list;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("addresses").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(list_response, new TypeReference<ArrayList<AddressMessage>>() {
        });
        return list;
    }
}
