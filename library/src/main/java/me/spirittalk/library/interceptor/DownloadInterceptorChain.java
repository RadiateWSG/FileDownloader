package me.spirittalk.library.interceptor;

import java.io.IOException;
import java.util.List;

import me.spirittalk.library.DownloadStatusCallback;
import me.spirittalk.library.connection.DownloadConnection;
import me.spirittalk.library.model.DownloadModel;

/**
 * Created by spirit on 2017/11/26.
 */

public class DownloadInterceptorChain implements Interceptor.Chain {
    private final DownloadModel downloadModel;
    private final List<Interceptor> interceptors;
    private final DownloadConnection connection;
    private final int index;
    private final DownloadStatusCallback callback;

    public DownloadInterceptorChain(List<Interceptor> interceptors, DownloadConnection connection,
                                    int index, DownloadModel model, DownloadStatusCallback callback) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.index = index;
        this.downloadModel = model;
        this.callback = callback;
    }

    @Override
    public DownloadModel getDownloadModel() {
        return downloadModel;
    }

    @Override
    public void proceed(DownloadModel downloadModel) throws IOException {
        proceed(downloadModel, connection);
    }

    public void proceed(DownloadModel downloadModel, DownloadConnection connection) throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        Interceptor interceptor = interceptors.get(index);
        DownloadInterceptorChain next = new DownloadInterceptorChain(interceptors, connection, index + 1, downloadModel, callback);
        interceptor.intercept(next);
    }

    @Override
    public DownloadConnection connection() {
        return connection;
    }

    public DownloadStatusCallback getStatusCallback() {
        return callback;
    }
}
