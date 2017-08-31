package com.xth.intelligentassistant.internetapi;

import com.google.gson.Gson;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.db.Sence;
import com.xth.intelligentassistant.gson.OneNetRegisterDe;
import com.xth.intelligentassistant.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by XTH on 2017/8/29.
 * 与OneNET平台接口对接，从而控制和读取ministm32上的数据
 */

public class OneNet {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String host = "http://api.heclouds.com";
    private static final String registerDe = "/register_de?";
    private static final String registerCode = "register_code=4s6bBQQb1imhUDQv";
    //private static final String json = "{\"sn\":\"1111\",\"title\":\"11111\"}";

    private OneNetRegisterDe oneNetRegisterDe;
    private OkHttpClient client;
    private Gson gson;

    public OneNet() {
        oneNetRegisterDe = new OneNetRegisterDe();
        client = new OkHttpClient();
        gson = new Gson();
    }

    private void SendRegisterDe(String sn, String title, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("sn",sn);
            json.put("title",title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.i(json.toString());
        Request request = new Request.Builder()
                .url(host + registerDe + registerCode)
                .post(RequestBody.create(JSON, json.toString()))
                .build();
        client.newCall(request).enqueue(callback);
    }
    public void RegisterDevice(String sn, final String title){
        SendRegisterDe(sn, title, new Callback() {
            OneNetRegisterDe oneNetRegisterDe;
            @Override
            public void onFailure(Call call, IOException e) {
                oneNetRegisterDe = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                oneNetRegisterDe = gson.fromJson(jsonString,OneNetRegisterDe.class);
                LogUtil.i(jsonString);
                if(oneNetRegisterDe.errno == 0){
                    LogUtil.i(oneNetRegisterDe.data.deviceId+"\n");
                    LogUtil.i(oneNetRegisterDe.data.key);
                    OperateDB.updateSenceId(new Sence(),title,oneNetRegisterDe.data.deviceId);//保存返回的设备id到数据库中的sence
                }else{
                    LogUtil.i(oneNetRegisterDe.error);
                }

            }
        });
    }
}
