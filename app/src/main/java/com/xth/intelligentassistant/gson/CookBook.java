package com.xth.intelligentassistant.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by XTH on 2017/6/7.
 */

public class CookBook {
    public int code;
    public String text;
    @SerializedName("list")
    public CookBookList list[];
    public class CookBookList {
        public String name;
        public String icon;
        public String info;
        public String detailurl;
        public CookBookList() {
            name = "";
            icon = "";
            info = "";
            detailurl = "";
        }
    }

    public CookBook() {
        code = 0;
        text = "";
        for (int i=0;i<1;i++){
            list[i] = new CookBookList();
        }
    }
}
