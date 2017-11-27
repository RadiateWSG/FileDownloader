package me.spirittalk.library.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import me.spirittalk.library.DownloadStatus;
import me.spirittalk.library.FileDownloader;
import me.spirittalk.library.model.DaoMaster;
import me.spirittalk.library.model.DaoSession;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.model.DownloadModelDao;

/**
 * Created by spirit on 2017/11/27.
 */

public class DaoManager {
    private DaoSession daoSession;
    private DownloadModelDao modelDao;

    private final static class HolderClass {
        private final static DaoManager INSTANCE = new DaoManager();
    }

    public static DaoManager getInstance() {
        return HolderClass.INSTANCE;
    }

    private DaoManager() {
        SQLiteDatabase db = new DbHelper(FileDownloader.getAppContext()).getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
        modelDao = daoSession.getDownloadModelDao();
    }

    public boolean insertOrReplace(DownloadModel model) {
        return modelDao.insertOrReplace(model) > 0;
    }

    public void updateStatue(int id, DownloadStatus status) {
        DownloadModel model = modelDao.queryBuilder().where(DownloadModelDao.Properties.Id.eq(id)).build().unique();
        if (model.getStatus() != status) {
            model.setStatus(status);
            modelDao.update(model);
        }
    }

    public void updateModel(DownloadModel model) {
        modelDao.update(model);
    }

    public List<DownloadModel> getAll() {
        return modelDao.queryBuilder().build().list();
    }

    public void deleteModel(DownloadModel model) {
        modelDao.delete(model);
    }

    public void clear() {
        daoSession.clear();
    }
}
