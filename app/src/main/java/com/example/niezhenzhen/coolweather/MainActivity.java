package com.example.niezhenzhen.coolweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.niezhenzhen.coolweather.service.ChooseAreaFragment;
import com.example.niezhenzhen.coolweather.service.G;
import com.example.niezhenzhen.coolweather.service.SettingFragment;
import com.example.niezhenzhen.coolweather.service.SuggestionFragment;
import com.example.niezhenzhen.coolweather.service.WeatherFragment;

import org.litepal.tablemanager.Connector;

/**
 * 注意：在使用FragmentTabhost时必须使用@android：id固定的id，包括tabhost,tabcotent,tabs三个id
 */
public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener{

    FragmentTabHost fragmentTabHost;
    FragmentManager fragmentManager;
    /*tab标题*/
    private String[] tab_title = {G.NEW,G.LIFTY,G.CITY,G.SETTING};
    /*tab图标*/
    private int[] tab_image = {R.drawable.update,R.drawable.life,R.drawable.city,R.drawable.setting};
    /*conents*/
    private Class[] fragments = {WeatherFragment.class, SuggestionFragment.class, ChooseAreaFragment.class, SettingFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Connector.getDatabase();    //创建数据表
        initView();
        fragmentManager = getSupportFragmentManager();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET},100);
        }
    }

    /**
     * 初始化视图界面
     */
    private void initView(){
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        for(int i=0;i<tab_title.length;i++){
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(tab_title[i]).setIndicator(getTabhostView(i));
            fragmentTabHost.addTab(tabSpec,fragments[i],null);
        }
    }

    /**
     * 创建底部的tabs视图
     * @param index 对应的tabs下标
     * @return 返回对应的view
     */
    private View getTabhostView(int index){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tabs_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tabs_icon);
        imageView.setImageResource(tab_image[index]);
        TextView textView = (TextView) view.findViewById(R.id.tabs_title);
        textView.setText(tab_title[index]);
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
