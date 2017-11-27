package me.spirittalk.library.db;

import android.content.Context;

import me.spirittalk.library.model.DaoMaster;

/**
 * Created by spirit on 2017/11/27.
 */
public class DbHelper extends DaoMaster.OpenHelper {

    public DbHelper(Context context) {
        super(context, "fileDownloader");
    }
}
