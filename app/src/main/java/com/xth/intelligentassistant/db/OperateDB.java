package com.xth.intelligentassistant.db;

import org.litepal.crud.DataSupport;

/**
 * Created by XTH on 2017/6/1.
 */

public class OperateDB {
    public static void addName(Sence sence, String value) {
        sence.setSenceName(value);
        sence.save();
    }

    public static void addName(Device device, String value, String senceName) {
        device.setDeviceName(value);
        device.setSenceName(senceName);
        device.save();
    }

    public static void modificateName(Sence sence, String value, int position) {
        sence.setSenceName(value);
        sence.update(position);
    }

    public static void modificateName(Device device, String value, int position, String senceName) {
        device.setDeviceName(value);
        device.setSenceName(senceName);
        device.update(position);
    }

    public static void deleteName(Sence sence, int position) {
        DataSupport.deleteAll(Device.class, "senceName = ?", sence.getSenceName());
        sence.delete(Sence.class, position);
    }

    public static void deleteName(Device device, int position) {
        device.delete(Device.class, position);
    }
}
