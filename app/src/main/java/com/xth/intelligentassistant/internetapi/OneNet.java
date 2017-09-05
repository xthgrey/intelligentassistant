package com.xth.intelligentassistant.internetapi;

import com.google.gson.Gson;
import com.xth.intelligentassistant.db.Device;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.db.Sence;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetErr;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetGetDataStreams;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetGetDe;
import com.xth.intelligentassistant.gson.OneNetJson.OneNetIncDataStreams;
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
 * OneNet 中device对应数据库的sence，datastreams对应数据库中的device
 */

public class OneNet {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String host = "http://api.heclouds.com";
    private static final String registerSence = "/register_de?";
    private static final String registerCode = "register_code=4s6bBQQb1imhUDQv";
    private static final String sences = "/devices";
    private static final String devices = "/datastreams";
    private static final String API_KEY_NAME = "api-key";
    private static final String API_KEY_VALUE = "VJjx0ugy6uTIVVNC2d16Un=elPs=";

    private OkHttpClient client;
    private Gson gson;

    public OneNet() {
        client = new OkHttpClient();
        gson = new Gson();
    }


    private void registerSe(String sn, String title, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("sn", sn);
            json.put("title", title);
            LogUtil.i(json.toString());
            Request request = new Request.Builder()
                    .url(host + registerSence + registerCode)
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void incSe(String title, Boolean privated, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("private", privated);
            LogUtil.i(host + sences + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + sences)
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateSe(String senceName, String title, Boolean privated, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        Sence sence = OperateDB.isHaveInDB(senceName);
        try {
            json.put("title", title);
            json.put("private", privated);
            LogUtil.i(host + sences + "/" + sence.getSenceId() + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + sences + "/" + sence.getSenceId())
                    .put(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSe(String senceName, okhttp3.Callback callback) {
        Sence sence = OperateDB.isHaveInDB(senceName);
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + sences + "/" + sence.getSenceId())
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void deleteSe(String senceName, okhttp3.Callback callback) {
        Sence sence = OperateDB.isHaveInDB(senceName);
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + sences + "/" + sence.getSenceId())
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void incDe(String senceName, String deviceName, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        Sence sence = OperateDB.isHaveInDB(senceName);
        try {
            json.put("id", deviceName);
            json.put("unit", deviceName);
            LogUtil.i(host + sences + "/" + sence.getSenceId() + devices + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + sences + "/" + sence.getSenceId() + devices)
                    .post(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateDe(String senceName, String deviceName, String newDeviceName, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        Sence sence = OperateDB.isHaveInDB(senceName);
        Device device = OperateDB.isHaveInDB(senceName, deviceName);
        try {
            json.put("unit", newDeviceName);
            LogUtil.i(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId() + " ---- " + json.toString());
            Request request = new Request.Builder()
                    .addHeader(API_KEY_NAME, API_KEY_VALUE)
                    .url(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId())
                    .put(RequestBody.create(JSON, json.toString()))
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getDe(String senceName, String deviceName, okhttp3.Callback callback) {
        Sence sence = OperateDB.isHaveInDB(senceName);
        Device device = OperateDB.isHaveInDB(senceName, deviceName);
        LogUtil.i(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId());
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId())
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void deleteDe(String senceName, String deviceName, okhttp3.Callback callback) {
        Sence sence = OperateDB.isHaveInDB(senceName);
        Device device = OperateDB.isHaveInDB(senceName, deviceName);
        LogUtil.i(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId());
        Request request = new Request.Builder()
                .addHeader(API_KEY_NAME, API_KEY_VALUE)
                .url(host + sences + "/" + sence.getSenceId() + devices + "/" + device.getDeviceId())
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void registerSence(String sn, final String title) {
        registerSe(sn, title, new Callback() {
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

    public void incSence(final String title, Boolean privated) {
        incSe(title, privated, new Callback() {
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

    public void updateSence(final String senceName, final String title, Boolean privated) {
        updateSe(senceName, title, privated, new Callback() {
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
                    getSence(senceName);
                    OperateDB.updateName(new Sence(), senceName, title);
                } else {
                    LogUtil.i(oneNetErr.error);
                }

            }
        });
    }

    public void getSence(String senceName) {
        getSe(senceName, new Callback() {
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

    public void deleteSence(final String senceName) {
        deleteSe(senceName, new Callback() {
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
                    OperateDB.deleteName(senceName);
                } else {
                    LogUtil.i(oneNetErr.error);
                }
            }
        });
    }

    public void incDevice(final String senceName, final String deviceName) {
        incDe(senceName, deviceName, new Callback() {
            OneNetIncDataStreams oneNetIncDataStreams;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetIncDataStreams = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetIncDataStreams = gson.fromJson(jsonString, OneNetIncDataStreams.class);
                if (oneNetIncDataStreams.errno == 0) {
                    LogUtil.i(oneNetIncDataStreams.data.dsUuid + "\n");
                    OperateDB.updateDeviceId(new Device(), senceName, deviceName, deviceName);//设备ID即为首次设备名，后续修改设备名设备ID不变
                } else {
                    LogUtil.i(oneNetIncDataStreams.error);
                }

            }
        });
    }

    public void updateDevice(final String senceName, final String deviceName, final String newDeviceName) {
        updateDe(senceName, deviceName, newDeviceName, new Callback() {
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
                    OperateDB.updateName(new Device(), senceName, deviceName, newDeviceName);//设备ID即为首次设备名，后续修改设备名设备ID不变
                    getDevice(senceName, newDeviceName);//获取新设备信息
                } else {
                    LogUtil.i(oneNetErr.error);
                }

            }
        });
    }

    public void getDevice(String senceName, String deviceName) {
        getDe(senceName, deviceName, new Callback() {
            OneNetGetDataStreams oneNetGetDataStreams;

            @Override
            public void onFailure(Call call, IOException e) {
                oneNetGetDataStreams = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUtil.i(jsonString);
                oneNetGetDataStreams = gson.fromJson(jsonString, OneNetGetDataStreams.class);
                if (oneNetGetDataStreams.errno == 0) {
                    LogUtil.i(oneNetGetDataStreams.error + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.createTime + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.id + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.unit + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.unitSymbol + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.updateAt + "\n");
                    LogUtil.i(oneNetGetDataStreams.data.tags + "\n");
                } else {
                    LogUtil.i(oneNetGetDataStreams.error);
                }
            }
        });
    }

    public void deleteDevice(final String senceName, final String deviceName) {
        deleteDe(senceName, deviceName, new Callback() {
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
                    OperateDB.deleteName(senceName,deviceName);
                } else {
                    LogUtil.i(oneNetErr.error);
                }
            }
        });
    }
}
