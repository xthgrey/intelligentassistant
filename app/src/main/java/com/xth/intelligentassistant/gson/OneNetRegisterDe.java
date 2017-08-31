package com.xth.intelligentassistant.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/8/30.
 */

public class OneNetRegisterDe {
    public int errno;
    public String error;
    public Data data;

    public class Data {
        public String key;
        @SerializedName("device_id")
        public String deviceId;
    }
}
