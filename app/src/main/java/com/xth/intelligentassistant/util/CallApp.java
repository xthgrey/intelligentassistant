package com.xth.intelligentassistant.util;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XTH on 2017/5/19.
 */

public class CallApp {
    private Context context;
    private Intent intent;
    private Uri uri;

    private PackageManager pm;
    private ArrayList<AppInfo> appInfos;
    private HashMap<String, String> contacts;

    public CallApp(Context context) {
        this.context = context;
        contacts = readContacts();
        appInfos = queryAllAppInfo();
    }

    private String callBrowser(String s) {
        if (s.indexOf(Constant.SEARCH) != -1) {
            s = s.substring(s.indexOf(Constant.SEARCH) + Constant.SEARCH.length(), s.length());//取出字符串中搜索后面的字符串
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, s);//后续可以通过对话搜索相应的内容
            context.startActivity(intent);
            return Constant.SEARCHING + s;
        }
        return s;
    }

    public String callPhone(String s) {
        String number = "";
        if (s.indexOf(Constant.CALL) != -1) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 2);
            }
            s = s.substring(s.indexOf(Constant.CALL) + Constant.CALL.length(), s.length());//取出字符串中呼叫后面的字符串
            for (String displayName : contacts.keySet()) {
                if (displayName.equals(s)) {
                    number = contacts.get(displayName);
                    break;
                }
            }
            uri = Uri.parse("tel:" + number);
            intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);
            return Constant.CALLING + s;
        }
        return s;
    }

    /**
     * 获取联系人信息
     */
    private HashMap<String, String> readContacts() {
        Cursor cursor = null;
        HashMap<String, String> contacts = new HashMap<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CONTACTS}, 3);
        }
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    LogUtil.d(displayName + ":"+number);
                    contacts.put(displayName, number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contacts;
    }

    private String checkOpenApp(String s) {
        for (AppInfo appInfo : appInfos) {
            LogUtil.d(appInfo.getAppLabel());
            if (s.toUpperCase().indexOf(appInfo.getAppLabel()) == -1) {//将输入的字母变成大写，然后与应用列表比较：不相等
                if (s.toUpperCase().matches(".*[A-Z]+.*")) {//判断字符串中是否存在字母
                    if (appInfo.getAppLabel().replaceAll("[^A-Z]", "").equals(s.toUpperCase().replaceAll("[^A-Z]", ""))) {//有些app里面包含英文，直接判断英文是不是一样

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfo.getPkgName());
                        context.startActivity(intent);
                        return Constant.OPENING + appInfo.getAppLabel();
                    }
                }
            } else {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfo.getPkgName());
                context.startActivity(intent);
                return Constant.OPENING + appInfo.getAppLabel();
            }
        }
        return s;
    }

    public String turnString(String s) {

        //检测内容为打开app
        s = checkOpenApp(s);
        //检测内容为搜索
        s = callBrowser(s);
        //检测内容为呼叫
        s = callPhone(s);
        return s;
    }

    // 根据查询条件，查询特定的ApplicationInfo
    private ArrayList<AppInfo> queryAllAppInfo() {
        pm = context.getPackageManager();
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> listAppcations = pm.queryIntentActivities(intent, 0);
        Collections.sort(listAppcations,
                new ResolveInfo.DisplayNameComparator(pm));//先按照名字正序
        Collections.reverse(listAppcations);//倒序
        appInfos = new ArrayList<AppInfo>();
        for (ResolveInfo app : listAppcations) {
            appInfos.add(getAppInfo(app));
        }
        return appInfos;
    }

    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(ResolveInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel(app.loadLabel(pm).toString().replaceAll("\\s*", ""));
        appInfo.setPkgName(app.activityInfo.packageName);
        appInfo.setClassName(app.activityInfo.name);
        LogUtil.d(appInfo.getAppLabel() + "---" + appInfo.getPkgName() + "----" + appInfo.getClassName());
        return appInfo;
    }
}
