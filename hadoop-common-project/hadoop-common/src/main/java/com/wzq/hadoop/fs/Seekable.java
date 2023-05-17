package com.wzq.hadoop.fs;

import java.io.IOException;

/**
 * 用于支持在流中定位
 * <p>
 * Stream that permits seeking
 */
public interface Seekable {

    /**
     * 将当前偏移量设置到参数位置，下次读取数据从该位置开始
     * <p>
     * Seek to the given offset from the start of the file. The next read() will be
     * from that location. Can't seek past the end of the file.
     */
    void seek(long pos) throws IOException;

    /**
     * 返回当前偏移量的值
     *
     * @return the current offset from the start of the file.
     */
    long getPos() throws IOException;

    /**
     * 重新选择一个副本
     * <p>
     * Seeks a different copy of the data.
     *
     * @return true if found a new source, false otherwise.
     */
    boolean seekToNewSource(long targetPos) throws IOException;
}
