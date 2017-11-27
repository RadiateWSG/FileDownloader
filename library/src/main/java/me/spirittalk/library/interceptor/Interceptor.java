package me.spirittalk.library.interceptor;

import java.io.IOException;

import me.spirittalk.library.connection.DownloadConnection;
import me.spirittalk.library.model.DownloadModel;

/**
 * Created by spirit on 2017/11/26.
 */

public interface Interceptor {

    void intercept(Chain chain) throws IOException;

    interface Chain {
        DownloadModel getDownloadModel();
        void proceed(DownloadModel model) throws IOException;
        DownloadConnection connection();
    }
}
