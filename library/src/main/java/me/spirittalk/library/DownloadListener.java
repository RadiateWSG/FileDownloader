package me.spirittalk.library;

/**
 * Created by spirit on 2017/11/26.
 */

public interface DownloadListener {
    /**
     * 删除重置
     * @param task
     */
    void onReset(ITask task);

    /**
     * enqueue，等待被下载
     *
     * @param soFarBytes 已下载
     * @param totalBytes 总大小
     */
    void onPending(String name, final long soFarBytes, final long totalBytes);

    /**
     * 准备开始下载
     */
//    void onPreStart();

    /**
     * 已经开始下载
     */
    void onStart();

    void onConnect();

    /**
     * 连接成功
     *
     * @param soFarBytes 当前已下载
     * @param totalBytes 总大小
     */
    void onConnectSuc(final long soFarBytes, final long totalBytes);

    /**
     * 下载进度
     *
     * @param soFarBytes 当前已下载
     */
    void onProgress(final long soFarBytes, final long totalBytes);

    /**
     * 下载完成
     */
    void onComplete();

    /**
     * 暂停下载
     *
     * @param soFarBytes 当前已下载
     * @param totalBytes 总大小
     */
    void onPause(final long soFarBytes, final long totalBytes);

    /**
     * 下载出错
     *
     * @param e Throwable
     */
    void onFailure(final Throwable e);

    /**
     * 重试
     */
    void onRetry(long soFarBytes, long totalBytes);
}
