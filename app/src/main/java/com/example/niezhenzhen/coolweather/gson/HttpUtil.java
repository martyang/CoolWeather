package com.example.niezhenzhen.coolweather.gson;

import com.example.niezhenzhen.coolweather.db.Province;
import com.example.niezhenzhen.coolweather.service.G;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by niezhenzhen on 2017-8-20.
 * 请求网络数据
 */

public class HttpUtil {

    /**
     * 请求网络数据
     * @param address url地址
     * @param callback 处理返回结果的回调
     */
    public static void sendHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendCityRequest(List<Province> provinces,okhttp3.Callback callback){
        String addres = G.chinaUrl+"/"+provinces.get(0).getProvinceCode();
        sendHttpRequest(addres,callback);
    }
}
