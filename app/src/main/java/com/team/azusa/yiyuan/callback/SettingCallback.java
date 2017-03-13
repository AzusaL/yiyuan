package com.team.azusa.yiyuan.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import com.team.azusa.yiyuan.bean.Setting;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Delete_exe on 2016/3/24.
 */
public abstract class SettingCallback extends Callback<Setting> {
    @Override
    public Setting parseNetworkResponse(Response response) throws IOException {
        String respon = response.body().string();
        Setting setting;
        String list_response = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(respon);
            list_response = jsonObject.get("setting").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        setting = mapper.readValue(list_response, Setting.class);
        return setting;
    }
}
