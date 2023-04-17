package com.wzq.hadoop.io.compress;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 流抽象了任何有能力产出的数据的数据源。
 * <p>
 * CompressionOutputStream提供了基于流的数据压缩能力
 */
public abstract class CompressionOutputStream extends OutputStream {

    /**
     * The output stream to be compressed.
     */
    protected final OutputStream out;

    /**
     * Create a compression output stream that writes the compressed bytes to the given stream.
     *
     * @param out
     */
    protected CompressionOutputStream(OutputStream out) {
        this.out = out;
    }

    public void close() throws IOException {
        finish();
        out.close();
    }

    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Write compressed bytes to the stream. Made abstract to prevent leakage to underlying stream.
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public abstract void write(byte[] b, int off, int len) throws IOException;

    /**
     * Finishes writing compressed data to the output stream without closing the underlying stream.
     * @throws IOException
     */
    public abstract void finish() throws IOException;

    /**
     * Reset the compression to the initial state. Does not reset the underlying stream.
     * @throws IOException
     */
    public abstract void resetState() throws IOException;
}
