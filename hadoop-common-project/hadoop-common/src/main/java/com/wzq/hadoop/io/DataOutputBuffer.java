package com.wzq.hadoop.io;

import java.io.*;

/**
 * 写入内存缓冲区的一个DataOutput实现
 * <p>
 * 这比每次创建{@link java.io.DataOutputStream}和{@link java.io.ByteArrayOutputStream}写入数据节省了内存
 * <p>
 * Examples:
 *
 * <pre>
 *     DataOutputBuffer buffer = new DataOutputBuffer();
 *     while (... loop condition ...) {
 *         buffer.reset();
 *         ... write buffer using DataOutput methods ...
 *         byte[] data = buffer.getData();
 *         int dataLength = buffer.getLength();
 *         ... write data to its ultimate destination ...
 *     }
 * </pre>
 */
public class DataOutputBuffer extends DataOutputStream {

    private static class Buffer extends ByteArrayOutputStream {
        public Buffer() {
            super();
        }

        public Buffer(int size) {
            super(size);
        }

        public byte[] getData() {
            return buf;
        }

        public int getLength() {
            return count;
        }

        public void write(DataInput in, int len) throws IOException {
            int newcount = count + len;
            if (newcount > buf.length) {
                byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];
                System.arraycopy(buf, 0, newbuf, 0, count);
                buf = newbuf;
            }
            in.readFully(buf, count, len);
            count = newcount;
        }
    }

    private Buffer buffer;

    public DataOutputBuffer() {
        this(new Buffer());
    }

    public DataOutputBuffer(int size) {
        this(new Buffer(size));
    }

    private DataOutputBuffer(Buffer buffer) {
        super(buffer);
        this.buffer = buffer;
    }

    public byte[] getData() {
        return buffer.getData();
    }

    public int getLength() {
        return buffer.getLength();
    }

    public DataOutputBuffer reset() {
        this.written = 0;
        buffer.reset();
        return this;
    }

    public void write(DataInput in, int length) throws IOException {
        buffer.write(in, length);
    }

    public void writeTo(OutputStream out) throws IOException {
        buffer.writeTo(out);
    }
}
