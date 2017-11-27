package me.spirittalk.library;

/**
 * Created by spirit on 2017/11/27.
 */

public interface ITask {
    void enqueue(DownloadListener listener);

    void cancel();

    boolean isExecuted();

    boolean isCanceled();

    void deleteFile();
}
