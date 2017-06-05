package com.xth.intelligentassistant.db;

import org.litepal.crud.DataSupport;

/**
 * Created by XTH on 2017/6/1.
 */

public class OperateDB {

    public static void addName(Sence sence, String senceName) {
        sence.setSenceName(senceName);
        sence.save();
    }

    public static void addName(Device device, String senceName, String deviceName) {
        device.setSenceName(senceName);
        device.setDeviceName(deviceName);
        device.save();
    }

    public static void updateName(Sence sence, String newName, String oldName) {
        sence.setSenceName(newName);
        sence.updateAll("sencename = ?",oldName);

        Device device = new Device();
        device.setSenceName(newName);
        device.updateAll("sencename = ?",oldName);
    }

    public static void updateName(Device device, String senceName,String deviceName) {
        device.setDeviceName(deviceName);
        device.updateAll("sencename = ?",senceName);
    }

    public static void deleteName(String senceName) {
        DataSupport.deleteAll(Sence.class, "sencename = ?", senceName);
        DataSupport.deleteAll(Device.class, "sencename = ?", senceName);
    }

    public static void deleteName(String senceName,String deviceName) {
        DataSupport.deleteAll(Device.class,"sencename = ? and devicename = ?",senceName, deviceName);
    }
}
