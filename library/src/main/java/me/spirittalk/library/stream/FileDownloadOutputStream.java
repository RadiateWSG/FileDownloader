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

package me.spirittalk.library.stream;

import java.io.IOException;

/**
 * Created by spirit on 2017/11/26.
 *
 * @see FileDownloadRandomAccessFile
 */

public interface FileDownloadOutputStream {

    void write(byte b[], int off, int len) throws IOException;

    void flushAndSync() throws IOException;

    void close() throws IOException;

    void seek(long offset) throws IOException, IllegalAccessException;

    void setLength(final long newLength) throws IOException, IllegalAccessException;
}
