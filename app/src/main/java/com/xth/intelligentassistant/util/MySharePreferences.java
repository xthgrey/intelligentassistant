package com.xth.intelligentassistant.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by XTH on 2017/5/22.
 */

public class MySharePreferences {

    //    private Context context;
    private String voiceSelect;//0:普通女生，1：普通男生，2：特别男生，3：情感男生<度逍遥>，4：情感儿童声<度丫丫>
    private Boolean textVoiceFlag;//false:Text显示语音图标,true:voice显示文字图标
    private Boolean voiceSexChoice;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public Boolean getVoiceSexChoice() {
        return voiceSexChoice;
    }

    public void setVoiceSexChoice(Boolean voiceSexChoice) {
        voiceSexChoice = !voiceSexChoice;
        this.voiceSexChoice = voiceSexChoice;
        editor.putBoolean(Constant.VOICE_SEX_CHOICE, voiceSexChoice);
        editor.apply();//保存提交
    }

    public void setVoiceSelect(String voiceSelect) {
        this.voiceSelect = voiceSelect;
        editor.putString(Constant.VOICE_SELECT, voiceSelect);
        editor.apply();
    }

    public void setTextVoiceFlag(Boolean textVoiceFlag) {
        textVoiceFlag = !textVoiceFlag;
        this.textVoiceFlag = textVoiceFlag;
        editor.putBoolean(Constant.TEXT_VOICE_FLAG, textVoiceFlag);
        editor.apply();//保存提交
    }

    public Boolean getTextVoiceFlag() {
        return textVoiceFlag;
    }

    public String getVoiceSelect() {
        return voiceSelect;
    }

    public MySharePreferences(Context context) {
//        this.context = context;
        pref = context.getSharedPreferences(Constant.PREF_FILE, context.MODE_PRIVATE);
        editor = pref.edit();
        voiceSelect = pref.getString(Constant.VOICE_SELECT, Constant.GENERALGIRL_VALUE);
        textVoiceFlag = pref.getBoolean(Constant.TEXT_VOICE_FLAG, false);
        voiceSexChoice = pref.getBoolean(Constant.VOICE_SEX_CHOICE, false);
    }
}
