package com.team.azusa.yiyuan.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.team.azusa.yiyuan.bean.YunNumDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azusa on 2016/1/24.
 */
public class JsonUtils {

    //将一个类转换陈json字符串
    public static <T> String getJsonStringformat(T oject) {
        ObjectMapper mapper = new ObjectMapper();
        String JsonString = "";
        try {
            JsonString = mapper.writeValueAsString(oject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return JsonString;
        }
        return JsonString;
    }

    //将一个json字符串转成list
    public static ArrayList<String> getlistfromString(String string) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> list = new ArrayList<>();
        if (StringUtil.isEmpty(string)) {
            return list;
        }
        try {
            list = mapper.readValue(string, new TypeReference<ArrayList<String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //将一个json字符串转成list
    public static ArrayList<GoodsCar> getcarlistfromString(String string) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<GoodsCar> list = new ArrayList<>();
        if (StringUtil.isEmpty(string)) {
            return list;
        }
        try {
            list = mapper.readValue(string, new TypeReference<ArrayList<GoodsCar>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //将一个Json字符串转换成对应的类
    public static <T> T getObjectfromString(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<RecordDto> getRecordDtoFromString(String string) {
        ObjectMapper mapper = new ObjectMapper();
        List<RecordDto> recordDto = new ArrayList<RecordDto>();
        if (StringUtil.isEmpty(string)) {
            return recordDto;
        }
        try {
            recordDto = mapper.readValue(string, new TypeReference<List<RecordDto>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recordDto;
    }

}
