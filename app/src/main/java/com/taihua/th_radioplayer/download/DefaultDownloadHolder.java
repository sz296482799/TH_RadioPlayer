package com.taihua.th_radioplayer.download;

import android.view.View;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;

/**
 * Created by wyouflf on 15/11/11.
 */
public class DefaultDownloadHolder extends DownloadHolder {

    public DefaultDownloadHolder(DownloadInfo downloadInfo) {
        super(downloadInfo);
    }

    @Override
    public void onWaiting() {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onLoading(long total, long current) {

    }

    @Override
    public void onSuccess(File result) {
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
    }

    @Override
    public void onCancelled(Callback.CancelledException cex) {
    }
}