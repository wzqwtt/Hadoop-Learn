package com.wzq.hadoop.io.compress;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link CompressorStream}使用压缩器实现了一个通用的压缩流
 */
public class CompressorStream extends CompressionOutputStream {

    protected Compressor compressor;
    protected byte[] buffer;
    protected boolean closed = false;

    public CompressorStream(OutputStream out, Compressor compressor, int bufferSize) {
        super(out);

        if (out == null || compressor == null) {
            throw new NullPointerException();
        } else if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal bufferSize");
        }

        this.compressor = compressor;
        this.buffer = new byte[bufferSize];
    }

    public CompressorStream(OutputStream out, Compressor compressor) {
        this(out, compressor, 512);
    }

    /**
     * Allow derived classes to directly set the underlying stream.
     *
     * @param out
     */
    protected CompressorStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        byte[] oneByte = new byte[1];
        oneByte[0] = (byte) (b & 0xff);
        write(oneByte, 0, oneByte.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Sanity checks （果然聪明）
        if (compressor.finished()) {
            throw new IOException("write beyond end of stream");
        }
        /**
         * 1. off | len：将off和len进行按位或运算，如果结果小于0，则说明off或len中有一个小于0
         * 2. off + len：将off和len相加，如果结果小于0，则说明两个数相加溢出了
         * 3. b.length - (off + len)：将数组b的长度建议off和len的和，如果结果小于0，则说明
         *    off+len大于了数组b的长度
         * 最终，将以上三个结果再进行按位或运算，如果结果小于0，则说明有一个或多个条件不成立，即数组操作越界
         */
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        // 执行压缩操作
        compressor.compress(b, off, len);
        while (!compressor.needsInput()) {
            compress();
        }
    }

    protected void compress() throws IOException {
        int len = compressor.compress(buffer, 0, buffer.length);
        if (len > 0) {
            out.write(buffer, 0, len);
        }
    }

    @Override
    public void finish() throws IOException {
        if (!compressor.finished()) {
            compressor.finish();
            while (!compressor.finished()) {
                compress();
            }
        }
    }

    @Override
    public void resetState() throws IOException {
        compressor.reset();
    }

    public void close() throws IOException {
        if (!closed) {
            finish();
            out.close();
            closed = true;
        }
    }
}
