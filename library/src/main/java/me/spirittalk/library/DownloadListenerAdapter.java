package me.spirittalk.library;

/**
 * Created by spirit on 2017/11/27.
 */

public class DownloadListenerAdapter implements DownloadListener {
    @Override
    public void onReset(ITask task) {

    }

    @Override
    public void onPending(String name, long soFarBytes, long totalBytes) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onConnectSuc(long soFarBytes, long totalBytes) {

    }

    @Override
    public void onProgress(long soFarBytes, long totalBytes) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onPause(long soFarBytes, long totalBytes) {

    }

    @Override
    public void onFailure(Throwable e) {

    }

    @Override
    public void onRetry(long soFarBytes, long totalBytes) {

    }
}
