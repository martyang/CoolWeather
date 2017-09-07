package com.example.niezhenzhen.coolweather.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 工具类
 * Created by yangmin on 2017/9/7.
 */

public class Util {

    public static String getCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 比较上次更新时间是否大于3小时，大于返回true
     * @param updateTime 需要比较的时间
     * @return 大于返回true
     */
    public static boolean compireTime(String updateTime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);
        long timespan = 0;
        if(updateTime!=null){
            try {
                Date last_time = dateFormat.parse(updateTime);
                Date current_time = new Date(System.currentTimeMillis());
                timespan = (current_time.getTime() -last_time.getTime())/(60*60*1000);
                G.log("距离上次更新："+timespan);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return timespan>3;
        }else {
            return false;
        }
    }
}
