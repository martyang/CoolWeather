package com.example.niezhenzhen.coolweather.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niezhenzhen.coolweather.R;
import com.example.niezhenzhen.coolweather.db.Province;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 城市选择界面
 * Created by niezhenzhen on 2017-8-20.
 */

public class ChooseAreaFragment extends Fragment implements View.OnClickListener {

    private GsonUtil mGsonUtil;
    ListView provinceListView;
    RecyclerView cityRecy;
    Button otherCityBtn;
    Button hotCityBtn;
    View provinceFragment;
    View chooseCity;
    boolean provinceIsVisiable = false;
    ArrayList<String> hostCitys = new ArrayList<>();
    String[] cityArr = {"北京","上海","广州","深圳","武汉","长沙","成都","重庆","大连","哈尔滨","杭州","济南","昆明","合肥","青岛","三亚","天津","西安","郑州","厦门","香港"};
    ArrayList<String> provinces = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        chooseCity = inflater.inflate(R.layout.choose_fram,null);
        if(hostCitys.isEmpty()){
            getList(cityArr);
        }
        provinceListView = (ListView) chooseCity.findViewById(R.id.province_list);
        cityRecy = (RecyclerView) chooseCity.findViewById(R.id.city_content);
        otherCityBtn = (Button) chooseCity.findViewById(R.id.other_city);
        hotCityBtn = (Button) chooseCity.findViewById(R.id.hot_city);
        provinceFragment = chooseCity.findViewById(R.id.province_frame);

        otherCityBtn.setOnClickListener(this);
        hotCityBtn.setOnClickListener(this);
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
                if(readProvinceFromDB()&&!provinceIsVisiable){
                    provinceFragment.setVisibility(View.VISIBLE);
                    provinceIsVisiable = true;
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,provinces);
                    provinceListView.setAdapter(arrayAdapter);
                }
                break;
            case R.id.hot_city:
                if(provinceIsVisiable){
                    Log.i(G.TAG,"隐藏列表");
                    provinceFragment.setVisibility(View.GONE);
                    provinceIsVisiable = false;
                }
                break;
        }
    }

    /**
     * 从网络下省数据
     */
    private void getProvinceDataFromNet(){
        HttpUtil.sendHttpRequest(G.chinaUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"读取数据失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                GsonUtil gsonUtil = new GsonUtil();
                gsonUtil.parseJsonToProvince(response.body().string());
            }
        });
    }

    /**
     * 读取数据库中的省信息，添加数数据列表
     * @return 读取到数据返回true
     */
    public boolean readProvinceFromDB(){
        List<Province> provinceData = DataSupport.findAll(Province.class);
        if(provinceData.isEmpty()){

            getProvinceDataFromNet();
            provinceData = DataSupport.findAll(Province.class);

            if(provinceData.isEmpty()){
                /*数据库是空的，没有数据*/
                return false;
            }
            for (Province p:provinceData) {
                provinces.add(p.getProvinceName());
            }
        }else{
            for (Province p:provinceData) {
                provinces.add(p.getProvinceName());
            }
        }
        return true;
    }

    private void getCityFromNet(){

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
