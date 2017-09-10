package com.example.niezhenzhen.coolweather.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.niezhenzhen.coolweather.db.City;
import com.example.niezhenzhen.coolweather.db.Province;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 在连接wifi网络时遍历更新所有的城市信息
 * Created by niezhenzhen on 2017-9-2.
 */

public class UpdataAllCity extends Service {

    public boolean checkNet(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();
        if(wifiState==WifiManager.WIFI_STATE_ENABLED){
            G.log("wifi已开启");
            return true;
        }
        return false;
    }

    public void updataAllCity(){
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if(!provinceList.isEmpty()){
            String address = G.chinaUrl+"/"+provinceList.get(0).getProvinceCode();
            HttpUtil.sendHttpRequest(address,new CityUpdateCallback());
        }else {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updataAllCity();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    class CityUpdateCallback implements okhttp3.Callback{
        public static final int PROVINCE = 1;
        public static final int CITY = 2;
        private List<Province> provinces = DataSupport.findAll(Province.class);
        private List<City> cityList;
        int province_index = 0;
        int city_index = 0;
        StringBuilder stringbuilder = new StringBuilder(G.chinaUrl);
        @Override
        public void onFailure(Call call, IOException e) {
//            cityList = DataSupport.where("provinceId = ?",provinces.get(province_index).getProvinceCode()+"").find(City.class);
//            if(!cityList.isEmpty()){
//                String address = stringbuilder.append("/").append(provinces.get(province_index).getProvinceCode())
//                        .append("/").append(cityList.get(0).getCityCode()).toString();
//                HttpUtil.sendHttpRequest(address,new CityUpdateCallback());
//            }
            province_index++;
            G.log("provinceCode:"+province_index);
            if(province_index<provinces.size()){
                String address = stringbuilder.append("/").append(provinces.get(province_index).getProvinceCode()).toString();
                HttpUtil.sendHttpRequest(address,this);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String content = response.body().string();
            GsonUtil gsonUtil = new GsonUtil();
            gsonUtil.parseJsonToCity(content,provinces.get(province_index).getProvinceCode());
//            cityList = DataSupport.where("provinceId = ?",provinces.get(province_index).getProvinceCode()+"").find(City.class);
//            if(!cityList.isEmpty()){
//                String address = stringbuilder.append("/").append(provinces.get(province_index).getProvinceCode())
//                        .append("/").append(cityList.get(0).getCityCode()).toString();
//                HttpUtil.sendHttpRequest(address,new CityUpdateCallback());
//            }
            province_index++;
            G.log("provinceCode:"+province_index);
            if(province_index<provinces.size()){
                String address = stringbuilder.append("/").append(provinces.get(province_index).getProvinceCode()).toString();
                HttpUtil.sendHttpRequest(address,this);
            }else {
                G.log("开始更新country数据");
                cityList = DataSupport.where("provinceId = ?",provinces.get(0).getProvinceCode()+"").find(City.class);
                String address = stringbuilder.append("/").append(provinces.get(0).getProvinceCode())
                        .append("/").append(cityList.get(0).getCityCode()).toString();
                HttpUtil.sendHttpRequest(address,new CountryUpdateCallback(cityList));
            }
        }
    }

    class CountryUpdateCallback implements okhttp3.Callback{
        private List<City> cityList;
        private List<Province> provices;
        int cityIndex = 0;
        int provinceIndex = 0;
        GsonUtil gsonUtil = new GsonUtil();
        int provinceCode;
        StringBuilder builder = new StringBuilder(G.chinaUrl);

        public CountryUpdateCallback(List<City> cityList) {
            this.cityList = cityList;
            provinceCode = cityList.get(0).getProvinceId();
            provices = DataSupport.findAll(Province.class);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            cityIndex++;
            G.log("City index:"+cityIndex);
            if(cityIndex<cityList.size()){
                String address = builder.append("/").append(provinceCode).append("/").append(cityList.get(cityIndex).getCityCode()).toString();
                HttpUtil.sendHttpRequest(address,this);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String content = response.body().string();
            gsonUtil.parseJsonToCountry(content,cityList.get(cityIndex).getCityCode());
            cityIndex++;
            G.log("City index:"+cityIndex);
            if(cityIndex<cityList.size()){
                String address = builder.append("/").append(provinceCode).append("/").append(cityList.get(cityIndex).getCityCode()).toString();
                HttpUtil.sendHttpRequest(address,this);
            }else {
                provinceIndex++;
                G.log("开始下一个省的country更新"+provinceIndex);
                G.log("重置数据");
                provinceCode = provices.get(provinceIndex).getProvinceCode();
                cityList = DataSupport.where("provinceId=?",String.valueOf(provinceCode)).find(City.class);
                cityIndex = 0;
                String address = builder.append("/").append(provinceCode).append("/")
                        .append(cityList.get(cityIndex).getCityCode()).toString();
                HttpUtil.sendHttpRequest(address,this);
            }
        }
    }
}
