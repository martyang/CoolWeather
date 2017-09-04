package com.example.niezhenzhen.coolweather.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 生活建议信息类
 * Created by niezhenzhen on 2017-9-3.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public WashCar washCar;

    @SerializedName("sport")
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class WashCar{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
