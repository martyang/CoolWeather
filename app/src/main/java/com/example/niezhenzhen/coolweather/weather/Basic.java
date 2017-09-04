package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 天气basic信息
 * Created by niezhenzhen on 2017-9-3.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
