package com.xth.intelligentassistant.gson.OneNetJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/9/4.
 */

public class OneNetIncDevice {
    public int errno;
    public String error;
    public Data data;

    public class Data {

        @SerializedName("device_id")
        public String deviceId;
    }
}
