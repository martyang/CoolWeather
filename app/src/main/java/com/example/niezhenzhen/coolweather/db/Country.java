package com.example.niezhenzhen.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by niezhenzhen on 2017-8-20.
 * 县/县级市
 */

public class Country extends DataSupport{
    private int id;
    private int cityId;
    private String countryName;
    private String weatherId;
    private String selected;

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
