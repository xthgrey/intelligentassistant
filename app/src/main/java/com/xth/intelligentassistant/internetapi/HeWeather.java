package com.xth.intelligentassistant.internetapi;

import android.text.TextUtils;

import com.xth.intelligentassistant.main.Weather;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by XTH on 2017/6/8.
 */

public class HeWeather {
    private Weather weather;
    private int WeatherCallBackFlag;//0:等待，1：成功，2：返回为空，3：请求错误

    public Weather getWeather() {
        return weather;
    }

    public int getWeatherCallBackFlag() {
        return WeatherCallBackFlag;
    }

    public HeWeather() {
        weather = new Weather();
        WeatherCallBackFlag = 0;
    }
    public void queryWeather(String city, String county) {
        String address = Constant.WEATHERADDRESS + city + Constant.WEATHERKEY;
        weather.setCity(city);
        weather.setCounty(county);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                WeatherCallBackFlag = Constant.WEATHER_REQUEST_ERROR;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                WeatherCallBackFlag = dealResponse(responseText);
            }
        });
    }

    private int dealResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray heWeather5 = jsonObject.getJSONArray(Constant.HEWEATHER5);
                JSONObject heWeather5Object = heWeather5.getJSONObject(0);
                JSONObject heWeather5Now = heWeather5Object.getJSONObject(Constant.HEWEATHER5NOW);
                weather.setTmp(heWeather5Now.getString(Constant.HEWEATHER5TMP) + Constant.TMPTEXT);
                weather.setHum(heWeather5Now.getString(Constant.HEWEATHER5HUM));
                JSONObject heWeather5NowCond = heWeather5Now.getJSONObject(Constant.HEWEATHER5NOWCOND);
                weather.setCondcode(heWeather5NowCond.getString(Constant.HEWEATHER5NOWCONDCODE));
                weather.setCondText(heWeather5NowCond.getString(Constant.HEWEATHER5NOWCONDTXT));
                return Constant.WEATHER_CALL_BACK;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Constant.WEATHER_EMPTY;
    }
}
