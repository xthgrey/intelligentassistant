package com.xth.intelligentassistant.util;

public class Constant {

    public static final String YUYIN_API_KEY = "VsCpOUtv6vquaw5ZMKhLLZAs";
    public static final String YUYIN_SECRET_KEY = "15004d92543351f67bf6873f9ec907ea";
    public static final String YUYIN_APP_ID = "9628655";
    public static final String TURING_SECRET_KEY = "850845256cd927cc";
    public static final String TURING_API_KEY = "b2d3853543864ce19ad5330335141ec2";

    public static final String GENERALGIRL = "欢迎使用智能助手普通女声";
    public static final String GENERALBOY = "欢迎使用智能助手普通男声";
    public static final String SPECIALBOY = "欢迎使用智能助手特别男声";
    public static final String EMOTIONBOY = "欢迎使用智能助手情感男声";
    public static final String EMOTIONCHILD = "欢迎使用智能助手情感儿童声";
    public static final String GENERALGIRL_VALUE = "0";
    public static final String GENERALBOY_VALUE = "1";
    public static final String SPECIALBOY_VALUE = "2";
    public static final String EMOTIONBOY_VALUE = "3";
    public static final String EMOTIONCHILD_VALUE = "4";

    public static final String SAMPLE_DIR_NAME = "baiduTTS";
    public static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_ch_speech_female.dat";
    public static final String SPEECH_MALE_MODEL_NAME = "bd_etts_ch_speech_male.dat";
    public static final String TEXT_MODEL_NAME = "bd_etts_ch_text.dat";
    public static final String SPEECH_FEMALE_MODEL_NAME_EN = "bd_etts_speech_female_en.dat";
    public static final String SPEECH_MALE_MODEL_NAME_EN = "bd_etts_speech_male_en.dat";
    public static final String TEXT_MODEL_NAME_EN = "bd_etts_text_en.dat";


    public static final String RECORD_AUDIO = "无法调用麦克";
    public static final String CALL_PHONE = "无法通话";
    public static final String READ_CONTACTS = "无法读取联系人";
    public static final String ACCESS_COARSE_LOCATION = "无法获取网络定位权限";

    public static final String ERROR_AUDIO = "音频问题";
    public static final String ERROR_SPEECH_TIMEOUT = "没有语音输入";
    public static final String ERROR_CLIENT = "其它客户端错误";
    public static final String ERROR_INSUFFICIENT_PERMISSIONS = "权限不足";
    public static final String ERROR_NETWORK = "网络问题";
    public static final String ERROR_NO_MATCH = "没有匹配的识别结果";
    public static final String ERROR_RECOGNIZER_BUSY = "引擎忙";
    public static final String ERROR_SERVER = "服务端错误";
    public static final String ERROR_NETWORK_TIMEOUT = "连接超时";

    public static final String MAP_LOCATION_TIP = "您正位于：\n";
    public static final String MAP_LOCATION_ERROR_4 = "网络连接异常";
    public static final String MAP_LOCATION_ERROR_DEFAULT = "请确保网络正常";

    public static final String VOICE_SELECT = "voiceSelect";
    public static final String TEXT_VOICE_FLAG = "text_voice_flag";
    public static final String VOICE_SEX_CHOICE = "voiceSexChoice";
    public static final String PREF_FILE = "data";
    public static final String LANGUAGE = "language";
    public static final String ENGLISH_LANGUAGE = "en-GB";
    public static final String CHINESE_LANGUAGE = "cmn-Hans-CN";

    public static final String OPENING = "正在打开：";
    public static final String SEARCH = "搜索";
    public static final String SEARCHING = "正在搜索：";
    public static final String CALL = "呼叫";
    public static final String CALLING = "正在呼叫";
    public static final String SCAN = "扫一扫";
    public static final String ANALYSIS = "解析结果:";
    public static final String ERROR_ANALYZE = "解析失败";

    public static final String WEATHERADDRESS = "https://free-api.heweather.com/v5/now?city=";
    public static final String WEATHERKEY = "&key=03f0e8903449476fa99331976a24ac6a";
    public static final String WEATHER_REQUEST_ERROR_TXT = "天气请求出错";
    public static final String HEWEATHER5 = "HeWeather5";
    public static final String WEATHER = "weather";
    public static final String PNG = ".png";
    public static final String TMPTEXT = "℃";
    public static final String HEWEATHER5NOW = "now";
    public static final String HEWEATHER5NOWCOND = "cond";
    public static final String HEWEATHER5NOWCONDCODE = "code";
    public static final String HEWEATHER5NOWCONDTXT = "txt";
    public static final String HEWEATHER5TMP = "tmp";
    public static final String HEWEATHER5HUM = "hum";

    public static final String MODIFYTXT = "修改";
    public static final String LONGCLICKEIDT = "长按编辑";
    public static final String SWIPE_SENCE_KEY = "sence";
    public static final String SWIPE_DIVICE_KEY = "divice";

    public static final String SENCE_NAME = "场景名称";
    public static final String DEVICE_NAME = "设备名称";
    public static final String EDIT_SENCE_NAME = "修改场景名称";
    public static final String EDIT_DEVICE_NAME = "修改设备名称";
    public static final String CONFIRM = "确定";
    public static final String CANCEL = "取消";
    public static final String ERROR_SENCE_NAME = "警告：场景名不能相同";
    public static final String ERROR_DEVICE_NAME = "警告：相同场景设备名不能相同";
    public static final String ERROR_EMPTY_NAME = "警告：名字不能是空白的";

    public static final String BING_API = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    public static final String BING_PIC = "http://cn.bing.com";
    public static final String BING_PIC_IMAGES = "images";
    public static final String BING_PIC_URL = "url";

    public static final int WEATHER_CALL_BACK = 1;
    public static final int WEATHER_EMPTY = 2;
    public static final int WEATHER_REQUEST_ERROR = 3;
    public static final int BINGPIC_CALL_BACK = 4;


    public static final int MODIFY = 0;
    public static final int DELETE = 1;
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_IMAGE = 2;

    public static final int TURING_CHARACTER = 100000;
    public static final int TURING_LINK = 200000;
    public static final int TURING_NEWS = 302000;
    public static final int TURING_COOKBOOK = 308000;
    public static final int TURING_KEY_ERROR = 40001;
    public static final int TURING_INFO_EMPTY = 40002;
    public static final int TURING_REQUEST_EXHAUST = 40004;
    public static final int TURING_DATA_FORMAT_ERROR = 40007;
    public static final String TURING_CODE = "code";
}
