package com.example.niezhenzhen.coolweather.gson;

import android.text.TextUtils;

import com.example.niezhenzhen.coolweather.db.City;
import com.example.niezhenzhen.coolweather.db.Country;
import com.example.niezhenzhen.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by niezhenzhen on 2017-8-20.
 * 解析Json数据
 */

public class GsonUtil {

    /**
     * 解析省/直辖市数据，并保存到数据库
     * @param jsonData json数据
     * @return 处理成功返回true，否则返回false
     */
    public boolean parseJsonToProvince(String jsonData){
        if(!TextUtils.isEmpty(jsonData)){
            try {
                JSONArray allProvinces = new JSONArray(jsonData);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析市级数据,并保存到数据库
     * @param jsonData 返回的Json数据
     * @param provinceID 市对应的省的id
     * @return 保存成功返回true，否则返回false
     */
    public boolean parseJsonToCity(String jsonData,int provinceID){
        if(!TextUtils.isEmpty(jsonData)){
            try {
                JSONArray allCitys = new JSONArray(jsonData);
                for(int i=0;i<allCitys.length();i++){
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    City city =new City();
                    city.setProvinceId(provinceID);
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析县级数据，并保存到数据库
     * @param jsonData 需要解析的json数据
     * @param cityID 所属的城市id
     * @return 成功返回true，否则返回false
     */
    public boolean parseJsonToCountry(String jsonData,int cityID){
        if(!TextUtils.isEmpty(jsonData)){
            try {
                JSONArray allCountrys = new JSONArray(jsonData);
                for(int i=0;i<allCountrys.length();i++){
                    JSONObject countryObject = allCountrys.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(countryObject.getString("name"));
                    country.setCityId(cityID);
                    country.setWeatherId(countryObject.getInt("weather_id"));
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
