package com.example.niezhenzhen.coolweather.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niezhenzhen.coolweather.R;
import com.example.niezhenzhen.coolweather.db.Country;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;
import com.example.niezhenzhen.coolweather.weather.Forecast;
import com.example.niezhenzhen.coolweather.weather.Weather;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 主天气界面
 * Created by niezhenzhen on 2017-8-20.
 */

public class WeatherFragment extends Fragment {
    List<Country> showCountry;
    Country show_country;
    TextView current_city;
    TextView update_time;
    TextView sun_text;
    TextView current_temp;
    TextView aqi_number;
    TextView pm25_number;
    TextView comfort_text;
    TextView car_wash_text;
    TextView sport_text;
    TextView more;
    ListView future_weather_list;
    ImageView weather_icon;
    ImageView back;
    private ProgressDialog progressDialog;
    List<Forecast> weatherList;
    SharedPreferences sp;
    List<String> select_country_name = new ArrayList<>();
    PopupWindow popupWindow = null;
    boolean backShowed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.weather_fram, null);
        initView(view);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        showCountry = DataSupport.where("selected = ?", G.SELECTE).find(Country.class);
        if (!showCountry.isEmpty()) {
            G.log("收藏城市个数："+showCountry.size());
            for (Country c : showCountry) {
                select_country_name.add(c.getCountryName());
            }
            show_country = showCountry.get(0);
            String weatherStr = sp.getString(G.WEATHER_KEY, null);
            if (weatherStr != null) {
                GsonUtil gsonUtil = new GsonUtil();
                Weather weather = gsonUtil.parseWeather(weatherStr);
                showWeather(weather);
            } else {
                requestWeather(show_country.getWeatherId());
            }
        }
        return view;
    }

    public void requestWeather(final String weatherId) {
        showProgress();
        final String weatherUrl = G.weatherUrl + weatherId + G.AUTH_KEY;
        HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(getContext(), "获取天气信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                GsonUtil gsonUtil = new GsonUtil();
                Weather weather = gsonUtil.parseWeather(responseText);
                if (weather != null && "ok".equals(weather.status)) {
                    closeProgress();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(G.WEATHER_KEY, responseText);
                    editor.apply();
                    showWeather(weather);
                } else {
                    Toast.makeText(getContext(), "获取天气信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showWeather(Weather weather) {
        String cityName = weather.basic.cityName;
        String uptateTime = weather.basic.update.updateTime.split(" ")[1];
        String temp = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        current_city.setText(cityName);
        update_time.setText(uptateTime);
        sun_text.setText(weatherInfo);
        G.log("当前温度：" + weather.now.temperature);
        current_temp.setText(temp);
        aqi_number.setText(weather.aqi.city.aqi);
        pm25_number.setText(weather.aqi.city.pm25);
        comfort_text.setText("舒适度："+weather.suggestion.comfort.info);
        car_wash_text.setText("洗车指数："+weather.suggestion.washCar.info);
        sport_text.setText("运动指数："+weather.suggestion.sport.info);
        weatherList = weather.forecastList;
        G.log("未来天气预报数：" + weatherList.size());
        future_weather_list.setAdapter(new MyListAdapter(getContext(), R.layout.weather_item_layout, weatherList));
    }

    public class MyListAdapter extends ArrayAdapter<Forecast> {

        private int rescourceId;

        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Forecast> objects) {
            super(context, resource, objects);
            rescourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView;
            ViewHolder viewHolder;
            if (convertView == null) {
                itemView = LayoutInflater.from(getContext()).inflate(rescourceId, parent, false);
                viewHolder = new ViewHolder();
                //将view组件存入Viewholder中
                viewHolder.date = (TextView) itemView.findViewById(R.id.future_weather_date);
                viewHolder.suntext = (TextView) itemView.findViewById(R.id.future_weather_sun);
                viewHolder.max = (TextView) itemView.findViewById(R.id.future_weather_max_temp);
                viewHolder.min = (TextView) itemView.findViewById(R.id.future_weather_min_temp);
                itemView.setTag(viewHolder);//将viewholder存入itemView中，setTag()方法
            } else {
                itemView = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.date.setText(weatherList.get(position).date);
            viewHolder.suntext.setText(weatherList.get(position).more.info);
            viewHolder.max.setText(weatherList.get(position).temperature.max);
            viewHolder.min.setText(weatherList.get(position).temperature.min);
            return itemView;
        }

        class ViewHolder {
            TextView date;
            TextView suntext;
            TextView max;
            TextView min;
        }
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void initView(View v) {
        more = (TextView) v.findViewById(R.id.more);
        current_city = (TextView) v.findViewById(R.id.city_name);
        update_time = (TextView) v.findViewById(R.id.update_time);
        sun_text = (TextView) v.findViewById(R.id.sun_text);
        current_temp = (TextView) v.findViewById(R.id.weather_temp);
        aqi_number = (TextView) v.findViewById(R.id.aqi_number);
        pm25_number = (TextView) v.findViewById(R.id.pm25_number);
        comfort_text = (TextView) v.findViewById(R.id.comfort_text);
        car_wash_text = (TextView) v.findViewById(R.id.car_wash_text);
        sport_text = (TextView) v.findViewById(R.id.sport_text);
        future_weather_list = (ListView) v.findViewById(R.id.future_weather_list);
        weather_icon = (ImageView) v.findViewById(R.id.weather_image);
        back = (ImageView) v.findViewById(R.id.back_weather);
        more.setOnClickListener(new View.OnClickListener() {
            WindowManager windowManager = getActivity().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            @Override
            public void onClick(View v) {
                if (!select_country_name.isEmpty()) {
                    View moreView = getActivity().getLayoutInflater().inflate(R.layout.more_layout, null);
                    ListView more_country = (ListView) moreView.findViewById(R.id.more_country_list);
                    more_country.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, select_country_name));

                    display.getSize(point);
                    if(popupWindow ==null){
                        popupWindow = new PopupWindow(moreView, point.x, 500);
                    }
                    popupWindow.showAsDropDown(v);
                    backShowed = true;
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backShowed){
                    popupWindow.dismiss();
                    back.setVisibility(View.GONE);
                }
            }
        });
    }


}
