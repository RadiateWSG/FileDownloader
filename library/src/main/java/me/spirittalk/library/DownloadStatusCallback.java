package me.spirittalk.library;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import me.spirittalk.library.db.DaoManager;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileUtils;

/**
 * Created by spirit on 2017/11/27.
 */

public class DownloadStatusCallback {
    private DownloadListener listener;
    private DaoManager daoManager;
    private Handler handler;

    public DownloadStatusCallback(DownloadListener listener) {
        this.listener = listener;
        daoManager = DaoManager.getInstance();
        handler = new Handler(Looper.getMainLooper());
    }

    public void reset(final ITask task, final DownloadModel model) {
        model.reset();
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onReset(task);
            }
        });
        daoManager.updateModel(model);
    }

    public void pending(final DownloadModel model) {
        model.setStatus(DownloadStatus.pending);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPending(model.getId()+"", model.getSofar(), model.getTotal());
            }
        });
        daoManager.insertOrReplace(model);
    }

    public void started(DownloadModel model) {
        model.setStatus(DownloadStatus.started);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onStart();
            }
        });
        daoManager.updateStatue(model.getId(), model.getStatus());
    }

    public void connecting(final DownloadModel model) {
        model.setStatus(DownloadStatus.connecting);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onConnect();
            }
        });
        daoManager.updateModel(model);
    }

    public void connectSuc(final DownloadModel model) {
        model.setStatus(DownloadStatus.connectSuc);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onConnectSuc(model.getSofar(), model.getTotal());
            }
        });
        daoManager.updateModel(model);
    }

    public void progressing(final DownloadModel model) {
        model.setStatus(DownloadStatus.progress);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(model.getSofar(), model.getTotal());
            }
        });
        daoManager.updateModel(model);
    }

    public void completed(DownloadModel model) throws IOException {
        model.setStatus(DownloadStatus.completed);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onComplete();
            }
        });
        daoManager.updateModel(model);
        FileUtils.renameTempFile(model.getPath(), model.getTempFilePath());
    }

    public void error(final Throwable e, DownloadModel model) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFailure(e);
            }
        });
        daoManager.updateStatue(model.getId(), DownloadStatus.error);
    }

    public void paused(final DownloadModel model) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onPause(model.getSofar(), model.getTotal());
            }
        });
        daoManager.updateModel(model);
    }

    public void syncProgressFromCache(final DownloadModel model) {
        daoManager.updateModel(model);
    }


    public void retry(final DownloadModel model) {
        model.setStatus(DownloadStatus.retry);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onRetry(model.getSofar(), model.getTotal());
            }
        });
        daoManager.updateModel(model);
    }
}
