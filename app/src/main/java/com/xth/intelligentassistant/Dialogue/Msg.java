package com.xth.intelligentassistant.Dialogue;

/**
 * Created by XTH on 2017/5/15.
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }
    public void dialogAnswer(String content){
        this.type = TYPE_RECEIVED;
        this.content = content;
    }
}
