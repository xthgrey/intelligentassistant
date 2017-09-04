package com.xth.intelligentassistant.gson.OneNetJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/9/4.
 */

public class OneNetGetDe {
    public int errno;
    public String error;
    public Data data;

    public class Data {
        @SerializedName("private")
        public Boolean privated;
        public String protocol;
        @SerializedName("create_time")
        public String createTime;
        public Boolean online;
        public String id;
        @SerializedName("auth_info")
        public AuthInfo authInfo;
        public String title;
    }
    public class AuthInfo{
        @SerializedName("SYS")
        public String sys;
    }
}
