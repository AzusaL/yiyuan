package com.team.azusa.yiyuan.callback;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.AccountDetail;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Delete_exe on 2016/3/22.
 */
public abstract class AccountDetialsCallback extends Callback<Map<String, Object>> {

    public Map<String, Object> parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Map<String, Object> map = new HashMap<>();
        ArrayList<AccountDetail> list;
        String sum = "";
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("accountDetails").toString();
            Log.e("在account里面", "parseNetworkResponse:    " + list_response);
            Log.e("在account里面", "parseNetworkResponse: 长度" + list_response.length());
            sum = jsonObject.get("sum").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != list_response && list_response.length() > 2) {
            ObjectMapper mapper = new ObjectMapper();
            list = mapper.readValue(list_response, new TypeReference<List<AccountDetail>>() {
            });
        } else {
            list = new ArrayList<>();
            sum = "0";
        }
        map.put("list", list);
        map.put("sum", sum);
        return map;
    }

}
