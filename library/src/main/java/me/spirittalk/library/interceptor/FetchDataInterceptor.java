package me.spirittalk.library.interceptor;

import android.os.SystemClock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.spirittalk.library.DownloadStatusCallback;
import me.spirittalk.library.connection.FileDownloadUrlConnection;
import me.spirittalk.library.model.ConnectionProfile;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.stream.FileDownloadOutputStream;
import me.spirittalk.library.stream.FileDownloadRandomAccessFile;
import me.spirittalk.library.util.FileDownloadLog;
import me.spirittalk.library.util.FileDownloadUtils;
import me.spirittalk.library.util.GiveUpRetryException;

/**
 * Created by spirit on 2017/11/26.
 */

public class FetchDataInterceptor implements Interceptor {
    static final int BUFFER_SIZE = 4 * 1024;
    private DownloadStatusCallback callback;
    FileDownloadOutputStream outputStream;

    @Override
    public void intercept(Chain chain) throws IOException {
        // @spirit 暂停
        InputStream inputStream = null;
        DownloadModel model = chain.getDownloadModel();
        FileDownloadUrlConnection connection = (FileDownloadUrlConnection) chain.connection();

        final long contentLength = check(connection);

        ConnectionProfile profile = connection.getProfile();
        final long fetchBeginOffset = profile.getCurrentOffset();

        callback = ((DownloadInterceptorChain) chain).getStatusCallback();
        outputStream = new FileDownloadRandomAccessFile(new File(model.getTempFilePath()));
        try {
            outputStream.seek(fetchBeginOffset);
            FileDownloadLog.d(this, "start fetch: range [%d, %d), seek to[%d]",
                    profile.getStartOffset(), profile.getEndOffset(), profile.getCurrentOffset());
            inputStream = connection.getInputStream();
            byte[] buff = new byte[BUFFER_SIZE];

            // @spirit 暂停

            do {
                int byteCount = inputStream.read(buff);
                if (byteCount == -1) {
                    break;
                }
                outputStream.write(buff, 0, byteCount);
                model.sofar += byteCount;
                // callback progress
                callback.progressing(model);
                checkAndSync(model);
                // @spirit 暂停
            } while (true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                inputStream.close();
            try {
                if (outputStream != null)
                    sync(model);
            } finally {
                if (outputStream != null)
                    outputStream.close();
            }
        }

        final long fetchedLength = model.sofar - fetchBeginOffset;
        if (contentLength > 0 && contentLength != fetchedLength) {
            throw new GiveUpRetryException(String.format("fetched length[%d] != content length[%d]",
                    fetchedLength, contentLength));
        }
        // callback completed
        callback.completed(model);
    }

    private void sync(DownloadModel model) {
        boolean bufferPersistToDevice;
        try {
            outputStream.flushAndSync();
            bufferPersistToDevice = true;
        } catch (IOException e) {
            bufferPersistToDevice = false;
            FileDownloadLog.d(this, "Because of the system cannot guarantee that all " +
                    "the buffers have been synchronized with physical media, or write to file " +
                    "failed, we just not flushAndSync process to database too %s", e);
        }

        if (bufferPersistToDevice) {
            // only need update the filedownloader table.
            callback.syncProgressFromCache(model);
        }
    }

    private volatile long lastSyncBytes = 0;
    private volatile long lastSyncTimestamp = 0;

    private void checkAndSync(DownloadModel model) {
        final long now = SystemClock.elapsedRealtime();
        final long bytesDelta = model.sofar - lastSyncBytes;
        final long timestampDelta = now - lastSyncTimestamp;

        if (FileDownloadUtils.isNeedSync(bytesDelta, timestampDelta)) {
            sync(model);
            lastSyncBytes = model.sofar;
            lastSyncTimestamp = now;
        }
    }

    private long check(FileDownloadUrlConnection connection) throws IOException {
        long contentLength = FileDownloadUtils.convertContentLengthString(connection.getResponseHeaderField("Content-Length"));
        if (contentLength <= 0) {
            FileDownloadLog.w(this, "response contentLength is " + contentLength);
            throw new GiveUpRetryException("there isn't any content need to download");
        }

        ConnectionProfile profile = connection.getProfile();
        if (profile.getContentLength() > 0 && profile.getContentLength() != contentLength) {
            String msg = String.format("require contentLength(%d), but the response contentLength is %d",
                    profile.getContentLength(), contentLength);
            FileDownloadLog.w(this, msg);
            throw new GiveUpRetryException(msg);
        }
        return contentLength;
    }
}
