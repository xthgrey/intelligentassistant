package com.xth.intelligentassistant.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.xth.intelligentassistant.util.LogUtil;

import java.net.Inet4Address;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by XTH on 2017/9/29.
 *
 Level>-50      信号最强4格
 -50<Level<-65  信号3格
 -65<Level<-75  信号2格
 -75<Level<-90  信号1格
 -90<Level      信号0格
 */

public class LinkStm32 {
    private WifiInfo wifiInfo;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    private WifiManager.WifiLock mWifiLock;
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public LinkStm32(Context context) {
        wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
    }
    public void TestWifi(){
        LogUtil.i("总信息-->"+wifiInfo.toString());
        LogUtil.i("路由Mac地址-->"+wifiInfo.getBSSID());
        LogUtil.i("手机IP-->"+ipIntToString(wifiInfo.getIpAddress()));//需要转化
        LogUtil.i("网速-->"+wifiInfo.getLinkSpeed());
        LogUtil.i("信号强度-->"+wifiInfo.getRssi());
        LogUtil.i("wifi名-->"+wifiInfo.getSSID());
        LogUtil.i("手机MAC-->"+wifiInfo.getMacAddress());
        LogUtil.i("内网ID-->"+wifiInfo.getNetworkId());
    }
    private String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }
    public boolean OpenWifi() {//打开wifi,开关wifi，系统会自动识别保存过密码的wifi去连接
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    public boolean closeWifi() {//关闭wifi
        if (!wifiManager.isWifiEnabled()) {
            return true;
        } else {
            return wifiManager.setWifiEnabled(false);
        }
    }
    // 检查当前WIFI状态
    public int checkState() {
        return wifiManager.getWifiState();
    }
    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }
    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }
    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }
    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        wifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }
    public void startScan() {
        wifiManager.startScan();
        // 得到扫描结果
        wifiList = wifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = wifiManager.getConfiguredNetworks();
    }
    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return wifiList;
    }
    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((wifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }
    // 得到MAC地址
    public String getMacAddress() {
        return (wifiInfo == null) ? "NULL" : wifiInfo.getMacAddress();
    }
    // 得到接入点的BSSID
    public String getBSSID() {
        return (wifiInfo == null) ? "NULL" : wifiInfo.getBSSID();
    }
    // 得到IP地址
    public int getIPAddress() {
        return (wifiInfo == null) ? 0 : wifiInfo.getIpAddress();
    }
    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (wifiInfo == null) ? "NULL" : wifiInfo.toString();
    }
    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b =  wifiManager.enableNetwork(wcgID, true);
    }
    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }
    WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();//支持这个配置的身份验证协议
        config.allowedGroupCiphers.clear();//组密码支持的这个配置的设置
        config.allowedKeyManagement.clear();//这个配置支持的组密钥管理协议
        config.allowedPairwiseCiphers.clear();//一组两两对WPA密码被该配置支持
        config.allowedProtocols.clear();//安全协议支持的这个配置的设置
        config.SSID = "\"" + SSID + "\"";
        if(Type == WifiCipherType.WIFICIPHER_NOPASS)
        {
            config.wepKeys[0] = "";//4 WEP密钥
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//这个配置支持的组密钥管理协议
            config.wepTxKeyIndex = 0;//默认的WEP关键指标,从0到3
        }
        if(Type == WifiCipherType.WIFICIPHER_WEP)
        {
            config.hiddenSSID = true;//这是一个网络,不SSID广播,所以一个SSID-specific必须用于扫描探针请求
            config.wepKeys[0]= "\""+Password+"\"";//4 WEP密钥
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);//支持这个配置的身份验证协议
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);//组密码支持的这个配置的设置
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);//组密码支持的这个配置的设置
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);//组密码支持的这个配置的设置
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);//组密码支持的这个配置的设置
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//这个配置支持的组密钥管理协议NONE
            config.wepTxKeyIndex = 0;//默认的WEP关键指标,从0到3
        }
        if(Type == WifiCipherType.WIFICIPHER_WPA)
        {
            config.preSharedKey = "\""+Password+"\"";//Pre-shared WPA-PSK使用的关键
            config.hiddenSSID = true;//这是一个网络,不SSID广播,所以一个SSID-specific必须用于扫描探针请求
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);//支持这个配置的身份验证协议
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);//组密码支持的这个配置的设置
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);//这个配置支持的组密钥管理协议WPA_PSK
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);//一组两两对WPA密码被该配置支持
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);//安全协议支持的这个配置的设置
            config.status = WifiConfiguration.Status.ENABLED;//当前状态的网络配置条目
        }
        return config;
    }
    // 得到连接的ID
    public int getNetworkId() {
        return (wifiInfo == null) ? 0 : wifiInfo.getNetworkId();
    }
    //    有配置连接方式
    public boolean Connect(WifiConfiguration wf) {
        if (!this.OpenWifi()) {
            return false;
        }
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句，即当状态为WIFI_STATE_ENABLING时，让程序在while里面跑
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        boolean bRet = wifiManager.enableNetwork(wf.networkId, true);
        wifiManager.saveConfiguration();
        return bRet;
    }
//    无配置连接方式
    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
        if (!this.OpenWifi()) {
            return false;
        }
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = this
                .CreateWifiInfo(SSID, Password, Type);
        int netID = wifiManager.addNetwork(wifiConfig);
        boolean bRet = wifiManager.enableNetwork(netID, true);
        wifiManager.saveConfiguration();
        return bRet;
    }
}
