package com.xth.intelligentassistant.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/6/7.
 */

public class Link {
    public int code;
    public String text;
    public String url;

    public Link() {
        code = 0;
        text = "";
        url = "";
    }
}
