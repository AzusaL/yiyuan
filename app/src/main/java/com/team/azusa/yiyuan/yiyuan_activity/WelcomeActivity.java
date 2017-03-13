package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.User;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getUserId();
    }

    private void turnToMainActivity() {
        //获取屏幕宽度和高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        ConstanceUtils.screenWidth = outMetrics.widthPixels;
        ConstanceUtils.screenHight = outMetrics.heightPixels;
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void getUserId() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_userBuy")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                turnToMainActivity();
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String responses = jsonObject.get("messageInfo").toString();
                    ObjectMapper mapper = new ObjectMapper();
                    Log.e("main", response);
                    if ("未登陆".equals(responses)) {
                        UserUtils.userisLogin = false;
                        UserUtils.user = null;
                    } else {
                        if ("已登陆".equals(responses)) {
                            UserUtils.userisLogin = true;
                            String userJson = jsonObject.get("user").toString();
                            UserUtils.user = mapper.readValue(userJson, User.class);
                        }
                    }
                    turnToMainActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
