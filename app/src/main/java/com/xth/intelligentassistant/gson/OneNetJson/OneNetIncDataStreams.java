package com.xth.intelligentassistant.gson.OneNetJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/9/5.
 */

public class OneNetIncDataStreams {
    public int errno;
    public String error;
    public Data data;

    public class Data {
        @SerializedName("ds_uuid")
        public String dsUuid;
    }
}
