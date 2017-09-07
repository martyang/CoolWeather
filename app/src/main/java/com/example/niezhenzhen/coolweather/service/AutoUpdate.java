package com.example.niezhenzhen.coolweather.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.niezhenzhen.coolweather.db.City;
import com.example.niezhenzhen.coolweather.db.Province;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 后台更新服务，更新天气信息，更新背景图片
 * Created by yangmin on 2017/9/7.
 */

public class AutoUpdate extends Service {

    GsonUtil gsonUtil ;
    boolean hasUpdate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gsonUtil = new GsonUtil();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        hasUpdate = sp.getBoolean(G.DATABASE_UPTATE,false);

        if(!hasUpdate){
            G.log("开始后台更新数据...");
            //启动一个新线程去更新城市数据
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //更新省数据
                    HttpUtil.sendHttpRequest(G.chinaUrl, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            gsonUtil.parseJsonToProvince(response.body().string());
                            List<Province> provinces = DataSupport.findAll(Province.class);
                            //遍历更新各省的城市的数据
                            if(!provinces.isEmpty()){
                                for(final Province p:provinces){
                                    G.log("更新省");
                                    final String city_address = G.chinaUrl+"/"+p.getProvinceCode();
                                    HttpUtil.sendHttpRequest(city_address, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                            gsonUtil.parseJsonToCity(response.body().string(),p.getProvinceCode());
                                            List<City> cityList = DataSupport.where("provinceCode=?",String.valueOf(p.getProvinceCode())).find(City.class);
                                            //遍历更新各城市的县数据
                                            if(!cityList.isEmpty()){
                                                G.log("更新城市");
                                                for(final City city : cityList){
                                                    String countryAddress = city_address+"/"+city.getCityCode();
                                                    HttpUtil.sendHttpRequest(countryAddress, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {

                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {

                                                            gsonUtil.parseJsonToCountry(response.body().string(),city.getCityCode());
                                                        }
                                                    });
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            }).start();
            G.log("后台更新数据完成");
//            editor.putBoolean(G.DATABASE_UPTATE,true);
        }
        G.log("后台数据已更新，无需更新");
        return super.onStartCommand(intent, flags, startId);
    }
}
