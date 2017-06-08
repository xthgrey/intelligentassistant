package com.xth.intelligentassistant.gson;

import com.google.gson.Gson;
import com.xth.intelligentassistant.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XTH on 2017/6/7.
 */

public class TuringAnalyze {
    public static Character character;
    public static CookBook cookBook;
    public static Link link;
    public static News news;
    public static int code;

    public static int TuringResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            code = (int) jsonObject.get(Constant.TURING_CODE);
            String result = jsonObject.toString();
            switch (code){
                case Constant.TURING_CHARACTER:
                case Constant.TURING_KEY_ERROR:
                case Constant.TURING_INFO_EMPTY:
                case Constant.TURING_REQUEST_EXHAUST:
                case Constant.TURING_DATA_FORMAT_ERROR:
                    character = new Gson().fromJson(result, Character.class);
                    break;
                case Constant.TURING_COOKBOOK:
                    cookBook = new Gson().fromJson(result, CookBook.class);
                    break;
                case Constant.TURING_LINK:
                    link = new Gson().fromJson(result, Link.class);
                    break;
                case Constant.TURING_NEWS:
                    news = new Gson().fromJson(result, News.class);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }
}
