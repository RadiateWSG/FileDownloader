/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.spirittalk.library.model;


import me.spirittalk.library.util.FileUtils;

/**
 * Created by spirit on 2017/11/26.
 */

public class ConnectionProfile {
    // offset 通指与文件头(也就是0)的差值
    private long startOffset;// 开始位置的 offset
    private long currentOffset;// 当前位置的 offset
    private long endOffset;// 结束位置的 offset
    private long contentLength;

    ConnectionProfile(long startOffset, long currentOffset, long endOffset, long contentLength) {
        this.startOffset = startOffset;
        this.currentOffset = currentOffset;
        this.endOffset = endOffset;
        this.contentLength = contentLength;
    }

    public static ConnectionProfile getFirstConnectProfile(DownloadModel model) {
        // check resume available
        final long offset;
        final String tempFilePath = FileUtils.getTempFilePath(model.getPath());
        final String targetFilePath = model.getPath();

        final boolean resumeAvailable = FileUtils.isBreakpointAvailable(model);
        if (resumeAvailable) {
            offset = model.getSofar();
        } else {
            offset = 0;
        }
        model.setSofar(offset);

        if (offset <= 0) {
            // 多表的话，删除关联表
            // 删除文件
            FileUtils.deleteFiles(targetFilePath, tempFilePath);
        }

        return new ConnectionProfile(0, offset, 0, model.getTotal() - offset);
    }

    public long getStartOffset() {
        return startOffset;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public long getContentLength() {
        return contentLength;
    }

    @Override
    public String toString() {
        return "ConnectionProfile{" +
                "startOffset=" + startOffset +
                ", currentOffset=" + currentOffset +
                ", endOffset=" + endOffset +
                ", contentLength=" + contentLength +
                '}';
    }
}
