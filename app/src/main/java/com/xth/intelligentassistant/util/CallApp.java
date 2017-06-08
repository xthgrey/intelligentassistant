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

import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.xth.intelligentassistant.MainActivity;
import com.xth.intelligentassistant.gson.TuringAnalyze;

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
        appInfos = queryAllAppInfo();
    }

    /**
     * 搜索
     * @param s
     * @return
     */
    public String callBrowser(String s) {
        if (s.indexOf(Constant.SEARCH) != -1) {
            s = s.substring(s.indexOf(Constant.SEARCH) + Constant.SEARCH.length(), s.length());//取出字符串中搜索后面的字符串
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, s);//后续可以通过对话搜索相应的内容
            context.startActivity(intent);
            return Constant.SEARCHING + s;
        }
        return s;
    }

    /**
     * 呼叫
     * @param s
     * @return
     */
    public String callPhone(String s) {
        String number = "";
        contacts = readContacts();
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
    public String scan(String s){
        if(s.indexOf(Constant.SCAN) != -1){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 4);
            }
        }
        s = s.substring(s.indexOf(Constant.SCAN) ,Constant.SCAN.length());
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

    /**
     * 检测文字对应的APP
     * @param s
     * @return
     */
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

    /**
     * 将文字转换成程序识别的文字
     * @param s
     * @return
     */
    public String turnString(String s) {
        String content;
        //检测内容为打开app
        content = checkOpenApp(s);
        if(!content.equals(s)){
            return content;
        }
        //检测内容为搜索
        content = callBrowser(s);
        if(!content.equals(s)){
            return content;
        }
        //检测内容为呼叫
        content = callPhone(s);
        if(!content.equals(s)){
            return content;
        }
        return content;
    }
    public String turingTurnString(int code,String content){
        //图灵返回代码
        switch (code){
            case Constant.TURING_CHARACTER:
            case Constant.TURING_KEY_ERROR:
            case Constant.TURING_INFO_EMPTY:
            case Constant.TURING_REQUEST_EXHAUST:
            case Constant.TURING_DATA_FORMAT_ERROR:
                content = TuringAnalyze.character.text;
                break;
            case Constant.TURING_COOKBOOK:
                content = TuringAnalyze.cookBook.text;
                break;
            case Constant.TURING_LINK:
                content = TuringAnalyze.link.text;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TuringAnalyze.link.url));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
                break;
            case Constant.TURING_NEWS:
                content = TuringAnalyze.news.text;
                break;
            default:
                break;
        }
        return content;
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
