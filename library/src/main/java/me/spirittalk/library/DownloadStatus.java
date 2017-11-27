package me.spirittalk.library;

/**
 * Created by spirit on 2017/11/26.
 */

public enum DownloadStatus {
    normal(0),
    pending(1),
    started(2),
    connecting(3),
    connectSuc(4),
    progress(5),
    completed(6),
    retry(7),
    error(-1),
    pause(-2);

    public int value;

    DownloadStatus(int value) {
        this.value = value;
    }

    public static DownloadStatus valueOf(Integer value) {
        if (value == null) {
            return normal;
        }
        for (DownloadStatus status : DownloadStatus.values()) {
            if (value == status.value) {
                return status;
            }
        }
        return normal;
    }
}
