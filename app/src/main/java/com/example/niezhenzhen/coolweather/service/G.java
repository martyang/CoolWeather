package com.example.niezhenzhen.coolweather.service;

import android.util.Log;

/**
 * 常量
 * Created by yangmin on 2017/8/31.
 */

public class G {
    public static final String TAG = "CoolWeather";
    public static final String chinaUrl = "http://guolin.tech/api/china";
    public static final String weatherUrl = "http://guolin.tech/api/weather?cityid=";
    public static final String AUTH_KEY = "&key=8cafcd4cb7454f258f1140e7a0a06ad3";
    public static final String WEATHER_KEY = "weather";
    public static void log(String log) {
        Log.i(TAG, log);
    }
    /*主选择界面*/
    public static final int MAIN_LAYER = 100;
    /*省信息选择界面*/
    public static final int PROVINCE_LAYER = 101;
    /*城市信息界面*/
    public static final int CITY_LAYER = 102;
    /*县市信息界面*/
    public static final int COUNTRY_LAYER = 103;
    /*热门城市界面*/
    public static final int HOTCITY_LAYER = 104;
    public static final String COUNTRY_TAG = "COOLWEATHER_COUNTRY";
    public static final String NEW = "更新";
    public static final String LIFTY = "生活";
    public static final String CITY = "城市";
    public static final String SETTING = "设置";
    public static final String SELECTE = "true";
    public static final String PROVINCE_TYPE = "PROVINCE";
    public static final String CITY_TYPE = "CITY";
    public static final String COUNTRY_TYPE = "COUNTRY";

}
