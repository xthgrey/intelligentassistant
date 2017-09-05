package com.xth.intelligentassistant.gson.OneNetJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/9/5.
 */

public class OneNetGetDataStreams {
    public int errno;
    public String error;
    public Data data;

    public class Data {
        public String id;
        public String[] tags;
        public String unit;
        @SerializedName("unit_symbol")
        public String unitSymbol;
        @SerializedName("create_time")
        public String createTime;
        @SerializedName("update_at")
        public String updateAt;
    }
}
