package com.taihua.th_radioplayer;

import android.app.Application;
import com.taihua.th_radioplayer.global.Config;
import org.xutils.x;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(Config.DEBUG);
    }
}
