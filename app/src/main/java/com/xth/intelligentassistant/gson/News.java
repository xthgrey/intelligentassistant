package com.xth.intelligentassistant.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/6/7.
 */

public class News {
    public int code;
    public String text;
    @SerializedName("list")
    public NewsList list[];
    public class NewsList {
        public String article;
        public String source;
        public String icon;
        public String detailurl;
        public NewsList() {
            article = "";
            source = "";
            icon = "";
            detailurl = "";
        }
    }

    public News() {
        code = 0;
        text = "";
        for (int i=0;i<3;i++){
            list[i] = new NewsList();
        }
    }
}
