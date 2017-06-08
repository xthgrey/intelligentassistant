package com.xth.intelligentassistant.internetapi;

import android.text.TextUtils;
import android.widget.Toast;

import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.HttpUtil;
import com.xth.intelligentassistant.util.LogUtil;

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

public class BingPic {
    private String bingPicAdress;

    public BingPic() {
        bingPicAdress = "";
        queryBingPicApi();
    }

    public String getBingPicAdress() {
        return bingPicAdress;
    }

    private void queryBingPicApi() {
        String address = Constant.BING_API;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                bingPicAdress = "";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                bingPicAdress = responseDeal(responseText);
            }
        });
    }
    private String responseDeal(String response){
        String s = "";
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray(Constant.BING_PIC_IMAGES);
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                String urlObject = jsonObject1.getString(Constant.BING_PIC_URL);
                return Constant.BING_PIC + urlObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
}
