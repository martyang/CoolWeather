package com.example.niezhenzhen.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by niezhenzhen on 2017-8-20.
 * 省/直辖市
 */

public class Province extends DataSupport{
    /**
     * 省/直辖市id
     */
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

}
