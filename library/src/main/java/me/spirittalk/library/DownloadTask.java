package me.spirittalk.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.spirittalk.library.interceptor.CheckInterceptor;
import me.spirittalk.library.interceptor.ConnectionInterceptor;
import me.spirittalk.library.interceptor.DownloadInterceptorChain;
import me.spirittalk.library.interceptor.FetchDataInterceptor;
import me.spirittalk.library.interceptor.Interceptor;
import me.spirittalk.library.interceptor.RetryInterceptor;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileUtils;

/**
 * Created by spirit on 2017/11/26.
 */

final class DownloadTask implements ITask {
    final FileDownloader downloader;
    //    final String originalUrl;
    final DownloadModel originalModel;
    private boolean executed;
    private volatile boolean canceled;
    private DownloadStatusCallback callback;

    public DownloadTask(FileDownloader fileDownloader, DownloadModel model) {
        this.downloader = fileDownloader;
        this.originalModel = model;
    }

    @Override
    public void enqueue(DownloadListener listener) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        callback = new DownloadStatusCallback(listener);
        downloader.dispatcher.enqueue(new DownloadRunnable());
        // 队列中
        callback.pending(originalModel);
    }

    final class DownloadRunnable extends Thread {
        DownloadTask get() {
            return DownloadTask.this;
        }

        @Override
        public void run() {
            boolean signalledCallback = false;
            try {
                proceedInterceptorChain();
                if (canceled) {
                    signalledCallback = true;
                    callback.error(new IOException("Canceled"), originalModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (!signalledCallback) {
                    callback.error(e, originalModel);
                }
            } finally {
                downloader.dispatcher.fininshed(this);
            }
        }
    }

    void proceedInterceptorChain() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new CheckInterceptor(downloader));
        interceptors.add(new RetryInterceptor(downloader));
        interceptors.add(new ConnectionInterceptor(downloader));
        interceptors.add(new FetchDataInterceptor());
        Interceptor.Chain chain = new DownloadInterceptorChain(interceptors, null, 0, originalModel, callback);
        chain.proceed(originalModel);
    }

    @Override
    public synchronized boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void deleteFile() {
        if (FileUtils.deleteFile(originalModel.getPath())) {
            callback.reset(this, originalModel);
        }
    }

    public void cancel() {
        canceled = true;
    }
}
