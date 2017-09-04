package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 天气质量类
 * Created by niezhenzhen on 2017-9-3.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }
}
