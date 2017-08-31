package com.xth.intelligentassistant.db;

import android.support.annotation.NonNull;

import com.xth.intelligentassistant.util.LogUtil;

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
    public static  void updateSenceId(Sence sence,String senceName,String senceId){
        sence.setSenceId(senceId);
        sence.updateAll("sencename = ?",senceName);
    }

    public static void updateName(Device device, String senceName,String deviceOldName,String deviceNewName) {
        device.setDeviceName(deviceNewName);
        device.updateAll("sencename = ? and devicename = ?",senceName,deviceOldName);
    }

    public static void deleteName(String senceName) {
        DataSupport.deleteAll(Sence.class, "sencename = ?", senceName);
        DataSupport.deleteAll(Device.class, "sencename = ?", senceName);
    }

    public static void deleteName(String senceName,String deviceName) {
        DataSupport.deleteAll(Device.class,"sencename = ? and devicename = ?",senceName, deviceName);
    }

    @NonNull
    public static Boolean isHaveInDB(String senceName){
        for (Sence sence:DataSupport.findAll(Sence.class)){
            if(sence.getSenceName().equals(senceName)){
                return true;
            }
        }
        return false;
    }

    public static Boolean isHaveInDB(String senceName, String deviceName){
        for (Device device:DataSupport.findAll(Device.class)){
            if((device.getSenceName().equals(senceName)) && (device.getDeviceName().equals(deviceName))){
                return true;
            }
        }
        return false;
    }
}
