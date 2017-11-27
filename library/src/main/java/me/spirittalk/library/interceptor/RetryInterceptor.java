package me.spirittalk.library.interceptor;

import java.io.IOException;

import me.spirittalk.library.FileDownloader;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileDownloadLog;
import me.spirittalk.library.util.RetryException;

/**
 * Created by spirit on 2017/11/26.
 */

public class RetryInterceptor implements Interceptor {
    private final FileDownloader downloader;
    private int retryCount;

    public RetryInterceptor(FileDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void intercept(Chain chain) throws IOException {
        DownloadModel model = chain.getDownloadModel();
        while (true) {
            FileDownloadLog.i(this, "Retry intercept id[%d]-retry[%d]", model.getId(), retryCount);
            try {
                chain.proceed(model);
                break;
            } catch (IOException e) {
                if (e instanceof RetryException && ++retryCount <= downloader.getRetryCount()) {
                    continue;
                } else {
                    throw new IOException(String.format("retry %d times, still fail", retryCount));
                }
            }
        }
    }
}
