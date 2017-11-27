package me.spirittalk.library.connection;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import me.spirittalk.library.DownloadStatusCallback;
import me.spirittalk.library.model.ConnectionProfile;
import me.spirittalk.library.model.DownloadModel;
import me.spirittalk.library.util.FileDownloadUtils;
import me.spirittalk.library.util.FileUtils;
import me.spirittalk.library.util.RetryException;

/**
 * Created by spirit on 2017/11/26.
 */

public class FileDownloadUrlConnection implements DownloadConnection {
    private DownloadModel model;
    private URLConnection connection;
    private ConnectionProfile profile;
    private DownloadStatusCallback callback;
    private boolean acceptPartial, onlyFromBeginning;

    public FileDownloadUrlConnection(DownloadModel model, DownloadStatusCallback callback) throws IOException {
        this.model = model;
        this.callback = callback;
        connection = new URL(model.getUrl()).openConnection();
    }

    @Override
    public void addTimeoutMillis(int connectionTimeout, int readTimeout) {
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
    }

    @Override
    public void addHeader(String name, String value) {
        connection.addRequestProperty(name, value);
    }

    @Override
    public void connection() throws IOException {
        profile = ConnectionProfile.getFirstConnectProfile(model);
        addHeaders();
        connection.connect();
        handleConnected();
    }

    private void handleConnected() throws IOException {
        int code = getResponseCode();
        final String oldEtag = model.getEtag();
        String newEtag = getResponseHeaderField("Etag");
        onlyFromBeginning = (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED);
        acceptPartial = (code == HttpURLConnection.HTTP_PARTIAL);
        boolean isPreconditionFailed = isFailed(code, oldEtag, newEtag);
        if (isPreconditionFailed) {
            FileUtils.deleteFiles(model.getPath(), model.getTempFilePath());
            if (oldEtag != null && oldEtag.equals(newEtag)) {
                newEtag = "";
            }
            model.setSofar(0);
            model.setTotal(0);
            model.setEtag(newEtag);
            // retry
            callback.retry(model);
            throw new RetryException();
        }
        if (acceptPartial || onlyFromBeginning) {
            final long contentLength = FileDownloadUtils.convertContentLengthString(getResponseHeaderField("Content-Length"));
            if (contentLength != -1) {
                model.setTotal(model.getSofar() + contentLength);
            } else {
                model.setTotal(contentLength);
            }
            model.setEtag(newEtag);
            // connect success
            callback.connectSuc(model);
        } else {
            throw new RetryException();
        }
    }

    /**
     * 连接是否正常可用
     *
     * @param code    response code
     * @param oldEtag old etag
     * @param newEtag new etag
     */
    private boolean isFailed(int code, String oldEtag, String newEtag) {
        if (code == HttpURLConnection.HTTP_PRECON_FAILED) {
            return true;
        }

        // etag changed
        if (oldEtag != null && !oldEtag.equals(newEtag)) {
            // 200 or 206
            if (onlyFromBeginning || acceptPartial) {
                return true;
            }
        }
        if (code == HttpURLConnection.HTTP_CREATED && profile.getCurrentOffset() > 0) {
            return true;
        }
        if (code == HTTP_REQUESTED_RANGE_NOT_SATISFIABLE && model.getSofar() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public int getResponseCode() throws IOException {
        if (connection instanceof HttpURLConnection) {
            return ((HttpURLConnection) connection).getResponseCode();
        }
        return DownloadConnection.NO_RESPONSE_CODE;
    }

    @Override
    public String getResponseHeaderField(String name) {
        return connection.getHeaderField(name);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }

    private void addHeaders() {
        //添加 etag
        if (!TextUtils.isEmpty(model.getEtag())) {
            addHeader("If-None-Match", model.getEtag());
        }
        // 添加 range
        final String range;
        if (profile.getEndOffset() == 0) {// 判断是否为分段
            range = String.format("bytes=%d-", profile.getCurrentOffset());
        } else {
            range = String.format("bytes=%d-%d", profile.getCurrentOffset(), profile.getEndOffset());
        }
        addHeader("Range", range);
    }

    public ConnectionProfile getProfile() {
        return profile;
    }
}
