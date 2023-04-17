package com.wzq.hadoop.io.compress;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link CompressionInputStream}提供了基于流的解压缩的能力
 * <p>
 * Implementations are assumed to be buffered. This permits clients to reposition the underlying
 * input stream then call {@link #resetState()}, without having to also synchronize client buffer.
 */
public abstract class CompressionInputStream extends InputStream {

    /**
     * The input stream to be compressed
     */
    protected final InputStream in;

    /**
     * Create a compression input stream that reads the decompessed bytes from given stream.
     *
     * @param in The input stream to be compressed.
     */
    protected CompressionInputStream(InputStream in) {
        this.in = in;
    }

    public void close() throws IOException {
        in.close();
    }

    /**
     * Read bytes from stream. Made abstract to prevent leakage to underlying stream.
     *
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public abstract int read(byte[] b, int off, int len) throws IOException;

    /**
     * Reset the decompressor to its initial state and discard any buffered data, as the underlying
     * stream may have been repositioned.
     *
     * @throws IOException
     */
    public abstract void resetState() throws IOException;

}
