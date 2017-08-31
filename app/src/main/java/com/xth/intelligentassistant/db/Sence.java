package com.xth.intelligentassistant.db;

import org.litepal.crud.DataSupport;

/**
 * Created by XTH on 2017/6/1.
 */

public class Sence extends DataSupport{
    private String senceName;
    private String senceId;

    public String getSenceId() {
        return senceId;
    }

    public void setSenceId(String senceId) {
        this.senceId = senceId;
    }

    public String getSenceName() {
        return senceName;
    }

    public void setSenceName(String senceName) {
        this.senceName = senceName;
    }
}
