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

    public static void updateName(Device device, String deviceName,int position) {
        device.setDeviceName(deviceName);
        device.update(position + 1);
    }

    public static void deleteName(String senceName, int position) {
        DataSupport.delete(Sence.class, position + 1);
        DataSupport.deleteAll(Device.class, "sencename = ?", senceName);
    }

    public static void deleteName(Device device, int position) {
        DataSupport.delete(Device.class, position + 1);
    }
}
