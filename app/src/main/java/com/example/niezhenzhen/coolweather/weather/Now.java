package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 当前天气类
 * Created by niezhenzhen on 2017-9-3.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
