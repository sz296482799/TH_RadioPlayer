package com.taihua.th_radioplayer.manager;


public class NetWorkManager {

    private static NetWorkManager mInstance;

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


}
