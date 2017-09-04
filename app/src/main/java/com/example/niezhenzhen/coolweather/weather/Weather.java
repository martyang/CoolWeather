package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气信息类，包含所有的数据
 * Created by niezhenzhen on 2017-9-3.
 */

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
