package com.taihua.th_radioplayer.download;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by wyouflf on 15/11/10.
 */
public abstract class DownloadHolder {

    protected DownloadInfo downloadInfo;

    public DownloadHolder(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public final DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void update(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public abstract void onWaiting();

    public abstract void onStarted();

    public abstract void onLoading(long total, long current);

    public abstract void onSuccess(File result);

    public abstract void onError(Throwable ex, boolean isOnCallback);

    public abstract void onCancelled(Callback.CancelledException cex);
}
