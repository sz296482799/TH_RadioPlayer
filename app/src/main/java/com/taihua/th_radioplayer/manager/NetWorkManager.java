package com.taihua.th_radioplayer.manager;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
//import android.net.NetworkUtils;
import android.net.DhcpInfo;
//import android.net.ethernet.EthernetManager;
import android.net.wifi.*;
import android.content.Context;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.net.InetAddress;

import android.os.Handler;
import android.os.Message;

public class NetWorkManager {

    private static final String TAG = "NetWorkManager";

    public static final int NETWORK_MSG_WIFILIST = 0;

    private static final int SECURITY_NONE = 0;
    private static final int SECURITY_WEP = 1;
    private static final int SECURITY_PSK = 2;
    private static final int SECURITY_EAP = 3;

    private static NetWorkManager mInstance;
    //private EthernetManager mEthManager;
    private WifiManager mWifiManager;
    private Scanner mScanner;
    private IntentFilter mFilter;
    private BroadcastReceiver mReceiver;
    private Handler mHandler;

    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private NetWorkManager() {

    }

    public static NetWorkManager getInstance() {

        if (mInstance == null) {
            synchronized (NetWorkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, Handler handler) {
        //mEthManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mScanner = new Scanner();

        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        //mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        //mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(intent);
            }
        };
        context.registerReceiver(mReceiver, mFilter);

        mHandler = handler;
    }

    private void sendMessage(int what, int arg1, int arg2, Object obj) {
        if(mHandler != null) {
            Message msg = new Message();
            msg.what = what;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }

    public void startPppoe() {
//        mEthManager.setEthernetEnabled(false);
//        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE, null);
//        mEthManager.setEthernetEnabled(true);
    }

    public void startDhcp() {
//        mEthManager.setEthernetEnabled(false);
//        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP, null);
//        mEthManager.setEthernetEnabled(true);
    }

    public void startStatic(String ip, String netmask, String gateway, String dns1, String dns2) {
//        mEthManager.setEthernetEnabled(false);
//
//        String ipAddress = "";
//        if (ip != null && ip.length() > 0) {
//            ipAddress = ip;
//        }
//        else {
//            LogUtil.e(TAG, "Invalid ipv4 address");
//            return;
//        }
//
//        String netMask = "";
//        if (netmask != null) {
//            netMask = netmask;
//        }
//
//        String gateWay = "";
//        if (netmask != null) {
//            gateWay = gateway;
//        }
//        String DNS1 = "";
//        if (DNS1 != null) {
//            DNS1 = dns1;
//        }
//        String DNS2 = "";
//        if (DNS2 != null) {
//            DNS2 = dns2;
//        }
//
//        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
//        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
//        InetAddress inetmask = NetworkUtils.numericToInetAddress(netMask);
//        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
//        InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);
//
//        DhcpInfo dhcpInfo = new DhcpInfo();
//        try {
//            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(ipaddr);
//            dhcpInfo.gateway = NetworkUtils.inetAddressToInt(getwayaddr);
//            dhcpInfo.netmask = NetworkUtils.inetAddressToInt(inetmask);
//            dhcpInfo.dns1 = NetworkUtils.inetAddressToInt(idns1);
//            dhcpInfo.dns2 = NetworkUtils.inetAddressToInt(idns2);
//        } catch(IllegalArgumentException e) {
//            LogUtil.e(TAG, "Invalid ipv4 address");
//        }
//        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
//
//        mEthManager.setEthernetEnabled(true);
    }

    public void Wifi_Scan() {
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        else {
            mScanner.forceScan();
        }
    }

    public boolean connect(String ssid, String password) {

        if(ssid == null)
            return false;

        ScanResult result = getResults(ssid);
        if(result == null)
            return false;

        int security = SECURITY_NONE;
        if(result.capabilities.contains("WEP"))
            security = SECURITY_WEP;
        else if(result.capabilities.contains("PSK"))
            security = SECURITY_PSK;
        else if(result.capabilities.contains("EAP"))
            security = SECURITY_EAP;

        WifiConfiguration config = new WifiConfiguration();

        config.SSID = ssid;
        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                if(password != null && password.length() != 0) {
                    int length = password.length();
                    if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    }
                    else
                        config.wepKeys[0] = '"' + password + '"';
                }
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if(password != null && password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.wepKeys[0] = password;
                    }
                    else
                        config.wepKeys[0] = '"' + password + '"';
                }
                break;
        }

        //mWifiManager.connect;

        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
        updateAccessPoints();

        return true;
    }

    private ScanResult getResults(String ssid) {
        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID != null && result.equals(ssid)) {
                    return result;
                }
            }
        }
        return null;
    }

    /** Returns sorted list of access points */
    private List<ScanResult> constructAccessPoints() {

        //final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 ||
                        result.capabilities.contains("[IBSS]")) {
                    continue;
                }
            }
            Collections.sort(results, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult1, ScanResult scanResult2) {
                    if(scanResult1.level > scanResult2.level)
                        return 1;
                    return -1;
                }
            });
        }
        return results;
    }

    private void updateAccessPoints() {

        if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            List<ScanResult> result = constructAccessPoints();
            if(result != null && result.size() > 0) {
                sendMessage(NETWORK_MSG_WIFILIST, 0, 0, result);
            }
        }
    }

    private void handleEvent(Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if(WifiManager.WIFI_STATE_ENABLED == state) {
                mScanner.resume();
            }
            else {
                mScanner.pause();
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
//                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
//                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)
                ) {
            updateAccessPoints();
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            updateAccessPoints();
        }
    }

    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }
}
