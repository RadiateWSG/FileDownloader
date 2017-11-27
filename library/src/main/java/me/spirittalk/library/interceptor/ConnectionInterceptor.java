package me.spirittalk.library.interceptor;

import java.io.IOException;

import me.spirittalk.library.DownloadStatus;
import me.spirittalk.library.DownloadStatusCallback;
import me.spirittalk.library.FileDownloader;
import me.spirittalk.library.connection.DownloadConnection;
import me.spirittalk.library.connection.FileDownloadUrlConnection;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileDownloadLog;

/**
 * Created by spirit on 2017/11/26.
 */

public class ConnectionInterceptor implements Interceptor {
    private final FileDownloader downloader;

    public ConnectionInterceptor(FileDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void intercept(Chain chain) throws IOException {
        DownloadModel model = chain.getDownloadModel();
        FileDownloadLog.i(this, "Connection intercept id[%d]", model.getId());

        // start connect
        DownloadStatusCallback callback = ((DownloadInterceptorChain) chain).getStatusCallback();
        callback.connecting(model);

        DownloadConnection connection = new FileDownloadUrlConnection(model, callback);
        connection.addTimeoutMillis(downloader.connectTimeoutMillis(), downloader.readTimeoutMillis());
        connection.connection();

        ((DownloadInterceptorChain) chain).proceed(model, connection);
    }
}
