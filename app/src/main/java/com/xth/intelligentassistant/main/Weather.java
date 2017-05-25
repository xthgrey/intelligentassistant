package com.xth.intelligentassistant.main;

/**
 * Created by XTH on 2017/5/24.
 */

public class Weather {
    private String city;//城市
    private String county;//区域
    private String condText;//天气
    private String condcode;//天气代码
    private String tmp;//温度
    private String hum;//相对湿度

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCondText() {
        return condText;
    }

    public void setCondText(String condText) {
        this.condText = condText;
    }

    public String getCondcode() {
        return condcode;
    }

    public void setCondcode(String condcode) {
        this.condcode = condcode;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }
}
