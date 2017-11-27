package me.spirittalk.library.connection;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by spirit on 2017/11/26.
 *
 * @see FileDownloadUrlConnection
 */

public interface DownloadConnection {
    int NO_RESPONSE_CODE = 0;
    public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    /**
     * 设置超时时间（单位秒）
     */
    void addTimeoutMillis(int connectionTimeout, int readTimeout);

    /**
     * 设置 header
     */
    void addHeader(String name, String value);

    /**
     * execute connection
     */
    void connection() throws IOException;

    /**
     * 返回 http status code
     */
    int getResponseCode() throws IOException;

    /**
     * return response header field
     */
    String getResponseHeaderField(String name);

    /**
     * 返回连接的 input stream
     */
    InputStream getInputStream() throws IOException;
}
