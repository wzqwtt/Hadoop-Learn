package com.wzq.hadoop.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * 从内存缓存区读取的可复用的DataInputBuffer实现
 * <p>
 * 这比每次创建{@link java.io.DataInputStream}和{@link java.io.ByteArrayInputStream}读取数据节省了内存
 * <p>
 * Example:
 *
 * <pre>
 *     DataInputBuffer buffer = new DataInputBuffer();
 *     while (... loop condition ...) {
 *         byte[] data = ... get data ...;
 *         int dataLength = ... get data length ...;
 *         buffer.reset(data,dataLength);
 *         ... read buffer using DataInput methods ...
 *     }
 * </pre>
 */
public class DataInputBuffer extends DataInputStream {

    private static class Buffer extends ByteArrayInputStream {
        public Buffer() {
            super(new byte[]{});
        }

        public void reset(byte[] input, int start, int length) {
            this.buf = input;
            this.count = start + length;
            this.mark = start;
            this.pos = start;
        }

        public byte[] getData() {
            return buf;
        }

        public int getPosition() {
            return pos;
        }

        public int getLength() {
            return count;
        }
    }

    private Buffer buffer;

    public DataInputBuffer() {
        super(new Buffer());
    }

    public DataInputBuffer(Buffer buffer) {
        super(buffer);  // buffer extends ByteArrayInputStream
        this.buffer = buffer;
    }

    /**
     * Resets the data that the buffer reads
     *
     * @param input  byte[]
     * @param length byte.length
     */
    public void reset(byte[] input, int length) {
        buffer.reset(input, 0, length);
    }

    /**
     * Resets the data that the buffer reads
     *
     * @param input  byte[]
     * @param start  起始位置
     * @param length byte.length
     */
    public void reset(byte[] input, int start, int length) {
        buffer.reset(input, start, length);
    }

    public byte[] getData() {
        return buffer.getData();
    }

    public int getPosition() {
        return buffer.getPosition();
    }

    public int getLength() {
        return buffer.getLength();
    }
}
