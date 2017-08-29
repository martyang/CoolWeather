package com.example.niezhenzhen.coolweather.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.niezhenzhen.coolweather.R;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 城市选择界面
 * Created by niezhenzhen on 2017-8-20.
 */

public class ChooseAreaFragment extends Fragment implements View.OnClickListener {

    private GsonUtil mGsonUtil;
    private String chinaUrl = "http://guolin.tech/api/china";
    ListView provinceList;
    RecyclerView cityRecy;
    Button otherCityBtn;
    Button hotCityBtn;
    View provinceFragment;
    View chooseCity;
    ArrayList<String> hostCitys = new ArrayList<>();
    String[] cityArr = {"北京","上海","广州","深圳","武汉","长沙","成都","重庆","大连","哈尔滨","杭州","济南","昆明","合肥","青岛","三亚","天津","西安","郑州","厦门","香港"};
    ArrayList<String> provinces = new ArrayList<>();
    String[] provinceArr = {"北京","上海","广州","深圳","武汉","长沙","成都","重庆","大连","哈尔滨","杭州","济南","昆明","合肥"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        chooseCity = inflater.inflate(R.layout.choose_fram,null);
        if(hostCitys.isEmpty()){
            getList(cityArr);
        }
        provinceList = (ListView) chooseCity.findViewById(R.id.province_list);
        cityRecy = (RecyclerView) chooseCity.findViewById(R.id.city_content);
        otherCityBtn = (Button) chooseCity.findViewById(R.id.other_city);
        hotCityBtn = (Button) chooseCity.findViewById(R.id.hot_city);
        provinceFragment = chooseCity.findViewById(R.id.province_frame);

        otherCityBtn.setOnClickListener(this);
        cityRecy.setLayoutManager(new GridLayoutManager(getActivity(),3));
        cityRecy.setAdapter(new CityAdapter());
        return chooseCity;
    }

    private void getList(String[] citys){
        for (String s :citys){
            hostCitys.add(s);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.other_city:
                provinceFragment.setVisibility(View.VISIBLE);
                if(provinces.isEmpty()){
                    for (String str:provinceArr) {
                        provinces.add(str);
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,provinces);
                provinceList.setAdapter(arrayAdapter);
                break;
            case R.id.hot_city:

                break;
        }
    }

    private void getProvinceData(){
        HttpUtil.sendHttpRequest(chinaUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    GsonUtil gsonUtil = new GsonUtil();
                    gsonUtil.parseJsonToProvince(response.message());
                }
            }
        });
    }

    /**
     * RecyclerView的适配器
     */
    class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHoder>{

        @Override
        public CityViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            CityViewHoder cityViewHoder = new CityViewHoder(LayoutInflater.from(getActivity()).inflate(R.layout.city_item,parent,false));
            return cityViewHoder;
        }

        @Override
        public void onBindViewHolder(CityViewHoder holder, int position) {
            holder.cityText.setText(hostCitys.get(position));
        }


        @Override
        public int getItemCount() {
            return hostCitys.size();
        }

        class CityViewHoder extends RecyclerView.ViewHolder{

            TextView cityText;

            public CityViewHoder(View itemView) {
                super(itemView);
                cityText = (TextView) itemView.findViewById(R.id.city_text);
            }
        }
    }

}
