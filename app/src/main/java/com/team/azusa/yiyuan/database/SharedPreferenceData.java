package com.team.azusa.yiyuan.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.utils.JsonUtils;

import java.util.ArrayList;

/**
 * Created by Azusa on 2015/12/26.
 */
public class SharedPreferenceData {
    public SharedPreferenceData() {
    }

    private static final SharedPreferenceData sharedPrefrenceData = new SharedPreferenceData();

    public static SharedPreferenceData getInstance() {
        return sharedPrefrenceData;
    }

    //保存最近一次下拉刷新的时间
    public void saveLastRefreshTime(Context context, String time, String where) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("time" + where, time).commit();
        editor.clear();
    }

    //获得最近一次下拉刷新的时间
    public String getLastRefreshTime(Context context, String where) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        return sp.getString("time" + where, "");
    }

    //保存用户名
    public void saveLoginName(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loginname", name).commit();
        editor.clear();
    }

    //获得用户名
    public String getLoginName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        return sp.getString("loginname", "");
    }

    //保存搜索记录
    public void saveSearchtext(Context context, String text, String userid) {
        ArrayList<String> list = getSearchList(context, userid);
        if (list.contains(text)) {
            list.remove(list.indexOf(text));
        }
        list.add(0, text);
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("searchtext" + userid, JsonUtils.getJsonStringformat(list)).commit();
        editor.clear();
    }

    //获得用户搜索记录
    public ArrayList<String> getSearchList(Context context, String userid) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        return JsonUtils.getlistfromString(sp.getString("searchtext" + userid, ""));
    }

    //清除搜索记录
    public void clearSearchtext(Context context, String userid) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("searchtext" + userid, "").commit();
        editor.clear();
    }

    //保存购物车信息
    public void saveGoodsCarData(Context context, ArrayList<GoodsCar> list) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String datas = JsonUtils.getJsonStringformat(list);
        editor.putString("goodsCarData", datas).commit();
        editor.clear();
    }

    //获得保存的购物车信息
    public ArrayList<GoodsCar> getGoodsCarData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        return JsonUtils.getcarlistfromString(sp.getString("goodsCarData", ""));
    }

    //保存是否打开无图模式配置
    public void saveImageConfig(Context context, boolean isImageDownload) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isImageDownlaod", isImageDownload).commit();
        editor.clear();
    }

    //获取是否打开无图模式配置
    public boolean isUserDownloadImage(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sharedpreference", Context.MODE_PRIVATE);
        return sp.getBoolean("isImageDownlaod", false);
    }
}
