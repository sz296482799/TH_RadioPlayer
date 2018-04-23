package com.taihua.th_radioplayer.domain;

public class DeviceID {
    private String deviceid;

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return "{"
                + "deviceid:" + deviceid
                + "}";
    }
}
