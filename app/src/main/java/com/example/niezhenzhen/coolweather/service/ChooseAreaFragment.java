package com.example.niezhenzhen.coolweather.service;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niezhenzhen.coolweather.R;
import com.example.niezhenzhen.coolweather.db.City;
import com.example.niezhenzhen.coolweather.db.Country;
import com.example.niezhenzhen.coolweather.db.Province;
import com.example.niezhenzhen.coolweather.gson.GsonUtil;
import com.example.niezhenzhen.coolweather.gson.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.crud.DataSupport.findAll;
import static org.litepal.crud.DataSupport.where;

/**
 * 城市选择界面
 * Created by niezhenzhen on 2017-8-20.
 */

public class ChooseAreaFragment extends Fragment implements OnClickListener {
    private GsonUtil mGsonUtil;
    ListView provinceListView;
    RecyclerView cityRecy;
    ImageView back;
    Button otherCityBtn;
    Button hotCityBtn;
    View provinceFragment;
    View chooseCity;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    List<Country> selectedCity = new ArrayList<>();
    FragmentManager fragmentManager;
    ProgressDialog progressDialog;
    /*当前位于哪一层显示*/
    int layer = G.MAIN_LAYER;
    /*省是否可见的标志位*/
    boolean provinceIsVisiable = false;
    /*当前显示的城市的省id*/
    int provinceId;
    CityAdapter mCityAdapter;
    /*存放当前显示的城市名称*/
    ArrayList<String> hostCitys = new ArrayList<>();
    /*存放当前选择的省的城市列表*/
    ArrayList<City> cityList = new ArrayList<>();
    String[] cityArr = {"北京", "上海", "广州", "深圳", "武汉", "长沙", "成都", "重庆", "大连", "哈尔滨", "杭州", "济南", "昆明", "合肥", "青岛", "三亚", "天津", "西安", "郑州", "厦门", "香港"};
    /*省的名字列表*/
    ArrayList<String> provinces = new ArrayList<>();
    /*存放省的列表*/
    ArrayList<Province> provinceList = new ArrayList<>();
    /*县的名字列表*/
    ArrayList<String> countrys = new ArrayList<>();
    /*存放县市的列表*/
    ArrayList<Country> countryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        chooseCity = inflater.inflate(R.layout.choose_fram, null);
        selectedCity = DataSupport.where("selected = ?", G.SELECTE).find(Country.class);
        G.log("selected:" + selectedCity.size());
        if (selectedCity.isEmpty()) {
            //收藏的城市为空则加载热门城市
            getHotCity(cityArr);
            initHotCity();
        } else {
            G.log("获取收藏城市");
            getSelectedData();
        }
        provinceListView = (ListView) chooseCity.findViewById(R.id.province_list);
        cityRecy = (RecyclerView) chooseCity.findViewById(R.id.city_content);
        otherCityBtn = (Button) chooseCity.findViewById(R.id.other_city);
        hotCityBtn = (Button) chooseCity.findViewById(R.id.hot_city);
        provinceFragment = chooseCity.findViewById(R.id.province_frame);
        back = (ImageView) chooseCity.findViewById(R.id.back);

        mGsonUtil = new GsonUtil();
        fragmentManager = getActivity().getSupportFragmentManager();
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        otherCityBtn.setOnClickListener(this);
        hotCityBtn.setOnClickListener(this);
        cityRecy.setLayoutManager(gridLayoutManager);
        mCityAdapter = new CityAdapter();
        cityRecy.setAdapter(mCityAdapter);
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前省id，用于后续的查询
                provinceId = provinceList.get(position).getProvinceCode();
                getCityFromDB(provinceId);
                //更新城市数据
                G.log("显示" + provinceList.get(position).getProvinceName() + "省的城市信息");
//              cityRecy.setLayoutManager(gridLayoutManager);
                mCityAdapter.notifyDataSetChanged();
                layer = G.CITY_LAYER;
            }
        });
        //back键不同情况下的行为管理
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (layer) {
                    //返回到市级界面
                    case G.COUNTRY_LAYER:
                        getCityFromDB(provinceId);
                        mCityAdapter.notifyDataSetChanged();
                        layer = G.CITY_LAYER;
                        break;
                    //返回到主选择界面,没有收藏城市则到热门城市界面
                    case G.CITY_LAYER:
                        if (selectedCity.isEmpty()) {
                            goBackMain(getHotCity(cityArr));
                        } else {
                            goBackMain(getSelectedData());
                        }
                        break;
                    case G.PROVINCE_LAYER:
                        if (selectedCity.isEmpty()) {
                            goBackMain(getHotCity(cityArr));
                        } else {
                            goBackMain(getSelectedData());
                        }
                        break;
                    case G.HOTCITY_LAYER:
                        if (selectedCity.isEmpty()) {

                        } else {
                            goBackMain(getSelectedData());
                        }
                        break;
                    case G.MAIN_LAYER:

                        break;
                }
            }
        });
        return chooseCity;
    }

    private ArrayList<String> getHotCity(String[] citys) {
        hostCitys.clear();
        for (String s : citys) {
            hostCitys.add(s);
        }
        return hostCitys;
    }

    /**
     * 将数组中的热门城市添加到countryList中
     */
    public void initHotCity() {
        List<Country> list;
        countryList.clear();
        for (String name : cityArr) {
            list = DataSupport.where("countryName = ?", name).find(Country.class);
            if (!list.isEmpty()) {
                countryList.add(list.get(0));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_city:
                if (!provinceIsVisiable) {
                    readProvinceFromDB();
                }
                break;
            case R.id.hot_city:
                goBackMain(getHotCity(cityArr));
                initHotCity();
                layer = G.HOTCITY_LAYER;
                break;
        }
    }

    /**
     * 返回到城市选择界面
     */
    private void goBackMain(ArrayList<String> data) {
        Log.i(G.TAG, "隐藏列表");
        provinceFragment.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        provinceIsVisiable = false;
        layer = G.MAIN_LAYER;
        cityList.clear();
        hostCitys = data;
        mCityAdapter.notifyDataSetChanged();
    }

    /**
     * 根据传入的地址和类型下载数据
     *
     * @param address 下载地址
     * @param type    下载数据类型，
     * @param id      数据id
     */

    public void getDataFromNet(String address, final String type, final int id) {
        showProgress();
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getActivity(), "读取数据失败，请检查网络", Toast.LENGTH_SHORT);
                        toast.show();
                        closeProgress();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                G.log("从网络下载数据");
                String responseText = response.body().string();
                boolean result = false;
                if (type.equals(G.PROVINCE_TYPE)) {
                    result = mGsonUtil.parseJsonToProvince(responseText);
                } else if (type.equals(G.CITY_TYPE)) {
                    result = mGsonUtil.parseJsonToCity(responseText, id);
                } else if (type.equals(G.COUNTRY_TYPE)) {
                    result = mGsonUtil.parseJsonToCountry(responseText, id);
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if (G.PROVINCE_TYPE.equals(type)) {
                                readProvinceFromDB();
                            } else if (G.CITY_TYPE.equals(type)) {
                                getCityFromDB(id);
                            } else if (G.COUNTRY_TYPE.equals(type)) {
                                getCountryFromDB(id);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 读取数据库中的省信息，添加数数据列表
     */
    public void readProvinceFromDB() {
        List<Province> provinceData = findAll(Province.class);
        if (provinceData.isEmpty()) {
            getDataFromNet(G.chinaUrl, G.PROVINCE_TYPE, 0);
        } else {
            provinces.clear();
            provinceList.clear();
            G.log("从数据库读取数据-province");
            for (Province p : provinceData) {
                provinces.add(p.getProvinceName());
                provinceList.add(p);
            }
            G.log("显示省数据");
            provinceFragment.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            provinceIsVisiable = true;
            layer = G.PROVINCE_LAYER;
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, provinces);
            provinceListView.setAdapter(arrayAdapter);
        }
    }

    /**
     * 从数据库获取城市信息，并显示
     *
     * @param provinceId 对应的省id
     */
    public void getCityFromDB(int provinceId) {
        List<City> list = where("provinceId=?", String.valueOf(provinceId)).find(City.class);
        if (list.isEmpty()) {
            String address = G.chinaUrl + "/" + provinceId;
            getDataFromNet(address, G.CITY_TYPE, provinceId);
        } else {
            hostCitys.clear();
            cityList.clear();
            G.log("数据长度" + list.size());
            G.log("从数据库读取数据-city");
            for (City city : list) {
                hostCitys.add(city.getCityName());
                cityList.add(city);
            }
            mCityAdapter.notifyDataSetChanged();
        }
    }

    public void getCountryFromDB(int cityId) {
        List<Country> countryData = where("cityId=?", String.valueOf(cityId)).find(Country.class);
        if (countryData.isEmpty()) {
            String address = G.chinaUrl + "/" + provinceId + "/" + cityId;
            getDataFromNet(address, G.COUNTRY_TYPE, cityId);
        } else {
            hostCitys.clear();
            countryList.clear();
            G.log("从数据库读取数据-country");
            for (Country country : countryData) {
                hostCitys.add(country.getCountryName());
                countryList.add(country);
            }
            mCityAdapter.notifyDataSetChanged();
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

    /**
     * RecyclerView的适配器
     */
    class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CityViewHolder cityViewHoder = new CityViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.city_item, parent, false));
            return cityViewHoder;
        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            holder.cityText.setText(hostCitys.get(position));
            holder.setOnItemClickListenter(position);
        }


        @Override
        public int getItemCount() {
            return hostCitys.size();
        }

        class CityViewHolder extends RecyclerView.ViewHolder {

            TextView cityText;

            public CityViewHolder(View itemView) {
                super(itemView);
                cityText = (TextView) itemView.findViewById(R.id.city_text);
            }

            public void setOnItemClickListenter(int positions) {
                cityText.setOnClickListener(new MyItemClickListenter(positions));
            }
        }

        class MyItemClickListenter implements OnClickListener {
            int position;

            MyItemClickListenter(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                //可进入下级城市的点击处理,显示下级城市
                if (layer == G.CITY_LAYER) {
                    int cityId = cityList.get(position).getCityCode();
                    G.log("position:" + position);
                    G.log("cityID:" + cityId);
                    getCountryFromDB(cityId);
                    layer = G.COUNTRY_LAYER;

                } else {
                    //可获取天气的城市点击处理
                    G.log("选择" + countryList.get(position).getCountryName());
                    Country sc = countryList.get(position);
                    sc.setSelected(G.SELECTE);
                    sc.save();
                    selectedCity = getSelectedCountry();
                    if (!selectedCity.isEmpty()) {
//                        cityRecy.setLayoutManager(linearLayoutManager);
                        goBackMain(getSelectedData());
                        layer = G.MAIN_LAYER;
                    }
                }
            }
        }
    }

    public List<Country> getSelectedCountry() {
        return DataSupport.where("selected = ?", G.SELECTE).find(Country.class);
    }

    private ArrayList<String> getSelectedData() {
        selectedCity = DataSupport.where("selected = ?", "true").find(Country.class);
        G.log("selectedCity:" + selectedCity.size());
        if (!selectedCity.isEmpty()) {
            hostCitys.clear();
            for (Country c : selectedCity) {
                hostCitys.add(c.getCountryName());
            }
        }
        return hostCitys;
    }

    @Override
    public void onPause() {
        super.onPause();
        //在切换到其他界面时还原数据
        provinceIsVisiable = false;
        layer = G.MAIN_LAYER;
    }
}
