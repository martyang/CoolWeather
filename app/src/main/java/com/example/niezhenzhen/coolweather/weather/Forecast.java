package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 未来天气，原始数据包含一个数组，每一个数组代表1天的天气，只需定义单日天气，
 * 然后在引用的时候使用集合类型来进行声明。
 * Created by niezhenzhen on 2017-9-3.
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
