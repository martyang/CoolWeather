package com.example.niezhenzhen.coolweather.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.niezhenzhen.coolweather.R;

/**
 * 主天气界面
 * Created by niezhenzhen on 2017-8-20.
 */

public class WeatherFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater(savedInstanceState).inflate(R.layout.weather_fram,null);
        return view;
    }
}
