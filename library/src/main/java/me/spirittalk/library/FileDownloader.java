package me.spirittalk.library;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileUtils;

/**
 * Created by spirit on 2017/11/26.
 */

public class FileDownloader {
    final Dispatcher dispatcher;
    static Context mContext;
    final File directory;
    final int connectTimeout;
    final int readTimeout;
    final int writeTimeout;
    final boolean isWifiRequired;
    final int retryCount;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private FileDownloader() {
        this(new Builder());
    }

    private FileDownloader(Builder builder) {
        if (mContext == null) throw new RuntimeException("you must call init() first");
        this.dispatcher = new Dispatcher(builder.maxTask);
        this.directory = builder.directory != null ? builder.directory : FileUtils.getOwnCacheDirectory(mContext, "fileCache");
//        this.context = context;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.isWifiRequired = builder.isWifiRequired;
        this.retryCount = builder.retryCount;
    }

    public ITask newTask(String url) {
        return new DownloadTask(this, DownloadModel.newModel(url, directory.getAbsolutePath()));
    }

    public DownloadTask newTask(DownloadModel model) {
        return new DownloadTask(this, model);
    }

    public static final class Builder {
        File directory;
        int maxTask = 3;
        //        ExecutorService executorService;
        int connectTimeout = 15_000;
        int readTimeout = 20_000;
        int writeTimeout = 20_000;
        boolean isWifiRequired = true;
        int retryCount = 3;

        public Builder directory(File directory) {
            if (directory == null) throw new IllegalArgumentException("directory == null");
            this.directory = directory;
            return this;
        }

        public Builder maxTask(int maxTask) {
            if (maxTask <= 0) throw new IllegalArgumentException("maxTask <= 0");
            this.maxTask = maxTask;
            return this;
        }

//        public Builder executorService(ExecutorService executorService) {
//            if (executorService == null) throw new IllegalArgumentException("executorService == null");
//            this.executorService = executorService;
//            return this;
//        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            connectTimeout = checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            readTimeout = checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            writeTimeout = checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder isWifiRequired(boolean isWifiRequired) {
            this.isWifiRequired = isWifiRequired;
            return this;
        }

        public Builder retryCount(int retryCount) {
            if (retryCount <= 0) throw new IllegalArgumentException("retryCount <= 0");
            this.retryCount = retryCount;
            return this;
        }

        private static int checkDuration(String name, long duration, TimeUnit unit) {
            if (duration < 0) throw new IllegalArgumentException(name + " < 0");
            if (unit == null) throw new NullPointerException("unit == null");
            long millis = unit.toMillis(duration);
            if (millis > Integer.MAX_VALUE)
                throw new IllegalArgumentException(name + " too large.");
            if (millis == 0 && duration > 0)
                throw new IllegalArgumentException(name + " too small.");
            return (int) millis;
        }

        public FileDownloader build() {
            return new FileDownloader(this);
        }
    }

    public int connectTimeoutMillis() {
        return connectTimeout;
    }

    public int readTimeoutMillis() {
        return readTimeout;
    }

    public int writeTimeoutMillis() {
        return writeTimeout;
    }

    public boolean isWifiRequired() {
        return isWifiRequired;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
