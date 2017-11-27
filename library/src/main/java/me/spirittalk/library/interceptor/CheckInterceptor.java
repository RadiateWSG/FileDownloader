package me.spirittalk.library.interceptor;

import android.Manifest;

import java.io.IOException;

import me.spirittalk.library.DownloadStatus;
import me.spirittalk.library.FileDownloader;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileDownloadLog;
import me.spirittalk.library.util.FileDownloadUtils;

/**
 * Created by spirit on 2017/11/26.
 */

public class CheckInterceptor implements Interceptor {
    final FileDownloader downloader;
    public CheckInterceptor(FileDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void intercept(Chain chain) throws IOException {
        DownloadModel model = chain.getDownloadModel();
        FileDownloadLog.i(this, "Check intercept id[%d]", model.getId());

        // check network permission
        if (!FileDownloadUtils.hasPermission(downloader.getAppContext(), Manifest.permission.ACCESS_NETWORK_STATE))
            throw new RuntimeException("task can't start because wo don't have network permission.");
        // check wifi type
        if (downloader.isWifiRequired() && !FileDownloadUtils.isNetworkOnWifiType(downloader.getAppContext()))
            throw new RuntimeException("Only allows downloading this task on the wifi network type");
        // start download
        ((DownloadInterceptorChain) chain).getStatusCallback().started(model);
        chain.proceed(model);
    }
}
