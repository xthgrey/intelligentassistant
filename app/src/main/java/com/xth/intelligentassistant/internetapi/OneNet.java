package com.xth.intelligentassistant.internetapi;

import com.google.gson.Gson;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.db.Sence;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetErr;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetGetDe;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetIncDevice;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetRegisterDe;
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
    private static final String devices = "/devices";
    private static final String API_KEY_NAME = "api-key";
    private static final String API_KEY_VALUE = "VJjx0ugy6uTIVVNC2d16Un=elPs=";

    private OkHttpClient client;
    private Gson gson;

    public OneNet() {
        client = new OkHttpClient();
        gson = new Gson();
    }


    private void sendRegisterDe(String sn, String title, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("sn", sn);
            json.put("title", title);
            LogUtil.i(json.toString());
            Request request = new Request.Builder()
                    .url(host + registerDe + registerCode)
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendIncDe(String title, Boolean privated, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("private", privated);
            LogUtil.i(host + devices + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + devices)
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateDe(String deviceId, String title, Boolean privated, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("private", privated);
            LogUtil.i(host + devices + "/" + deviceId + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + devices + "/" + deviceId)
                    .put(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDe(String deviceId, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + devices + "/" + deviceId)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
    private void deleteDe(String deviceId, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + devices + "/" + deviceId)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void registerDevice(String sn, final String title) {
        sendRegisterDe(sn, title, new Callback() {
            OneNetRegisterDe oneNetRegisterDe;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetRegisterDe = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetRegisterDe = gson.fromJson(jsonString, OneNetRegisterDe.class);
                if (oneNetRegisterDe.errno == 0) {
                    LogUtil.i(oneNetRegisterDe.data.deviceId + "\n");
                    LogUtil.i(oneNetRegisterDe.data.key);
                    OperateDB.updateSenceId(new Sence(), title, oneNetRegisterDe.data.deviceId);//保存返回的设备id到数据库中的sence
                } else {
                    LogUtil.i(oneNetRegisterDe.error);
                }

            }
        });
    }

    public void incDevice(final String title, Boolean privated) {
        sendIncDe(title, privated, new Callback() {
            OneNetIncDevice oneNetIncDevice;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetIncDevice = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetIncDevice = gson.fromJson(jsonString, OneNetIncDevice.class);
                if (oneNetIncDevice.errno == 0) {
                    LogUtil.i(oneNetIncDevice.data.deviceId + "\n");
                    OperateDB.updateSenceId(new Sence(), title, oneNetIncDevice.data.deviceId);//保存返回的设备id到数据库中的sence
                } else {
                    LogUtil.i(oneNetIncDevice.error);
                }

            }
        });
    }

    public void updateDevice(final String deviceId, String title, Boolean privated) {
        updateDe(deviceId, title, privated, new Callback() {
            OneNetErr oneNetErr;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetErr = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetErr = gson.fromJson(jsonString, OneNetErr.class);
                if (oneNetErr.errno == 0) {
                    LogUtil.i(oneNetErr.error + "\n");
                    getDevice(deviceId);
                    //OperateDB.updateSenceId(new Sence(),title,oneNetRegisterDe.data.deviceId);//保存返回的设备id到数据库中的sence
                } else {
                    LogUtil.i(oneNetErr.error);
                }

            }
        });
    }
    public void getDevice(String deviceId) {
        getDe(deviceId,new Callback() {
            OneNetGetDe oneNetGetDe;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetGetDe = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetGetDe = gson.fromJson(jsonString, OneNetGetDe.class);
                if (oneNetGetDe.errno == 0) {
                    LogUtil.i(oneNetGetDe.error + "\n");
                    LogUtil.i(oneNetGetDe.data.createTime + "\n");
                    LogUtil.i(oneNetGetDe.data.id + "\n");
                    LogUtil.i(oneNetGetDe.data.protocol + "\n");
                    LogUtil.i(oneNetGetDe.data.title + "\n");
                    LogUtil.i(oneNetGetDe.data.authInfo.sys + "\n");
                    LogUtil.i(oneNetGetDe.data.online + "\n");
                    LogUtil.i(oneNetGetDe.data.privated + "\n");
                } else {
                    LogUtil.i(oneNetGetDe.error);
                }
            }
        });
    }
    public void deleteDevice(String deviceId) {
        deleteDe(deviceId,new Callback() {
            OneNetErr oneNetErr;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetErr = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetErr = gson.fromJson(jsonString, OneNetErr.class);
                if (oneNetErr.errno == 0) {
                    LogUtil.i(oneNetErr.error + "\n");
                } else {
                    LogUtil.i(oneNetErr.error);
                }
            }
        });
    }
}
