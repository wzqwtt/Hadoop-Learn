package com.wzq.hadoop.fs;

import java.io.IOException;

/**
 * 用于在流中进行定位读
 * <p>
 * Stream that permits positional reading.
 */
public interface PositionedReadable {

    /**
     * 从指定位置开始，读最多指定长度的数据到buffer中offset开始的缓冲区中
     * 注意，该函数不改变读流的当前位置，同事它是线程安全的
     * <p>
     * Read upto the specified number of bytes, from a given position within a file, and return
     * the number of bytes read. This does not change the current offset of a file, and is thread-safe.
     */
    public int read(long position, byte[] buffer, int offset, int length)
            throws IOException;

    /**
     * 从指定位置开始读指定长度的数据到buffer中offset开始的缓冲区中
     * <p>
     * Read the specified number of bytes, from a given position within a file. This does not change
     * the current offset of a file, and is thread-safe.
     */
    public void readFully(long position, byte[] buffer, int offset, int length)
            throws IOException;

    /**
     * Read number of bytes equal to the length of the buffer, from a given position within a file.
     * This does not change the current offset of a file, and is thread-safe.
     */
    public void readFully(long position, byte[] buffer) throws IOException;
}
