package com.wzq.hadoop.io.compress;

import java.io.IOException;

/**
 * Specification of a stream-bases of 'de-compressor' which can be plugged into a CompressionInputStream
 * to compress data. This is modelled after {@link java.util.zip.Inflater}
 */
public interface Decompressor {

    /**
     * Sets input data for decompression. This should be called if and only if needsInput()
     * returns true indicating that more input data is required. (Both native and non-native
     * versions of various Decompressors require that )
     *
     * @param b   Input data
     * @param off Start offset
     * @param len Length
     */
    public void setInput(byte[] b, int off, int len);

    /**
     * @return true if the input data buffer is empty and {@link #setInput(byte[], int, int)} should be called
     * to provide more input
     */
    public boolean needsInput();

    /**
     * Sets preset dictionary for compression. A preset dictionary is used when the history buffer can
     * be predetermined.
     *
     * @param b   Dicitionary data bytes
     * @param off Start offset
     * @param len Length
     */
    public void setDictionary(byte[] b, int off, int len);

    /**
     * @return true if the end of the decompressed data output stream has been reached.
     */
    public boolean finished();

    /**
     * Fills specified buffer with uncompressed data. Return actual number of bytes of uncompressed data.
     * A return value of 0 indicates that {@link #needsInput()} should be called in order to determine if
     * more input data is required.
     *
     * @param b   Buffer for the compressed data
     * @param off Start offset of the data
     * @param len Size of the buffer
     * @return The actual number of bytes of compressed data.
     * @throws IOException
     */
    public int decompress(byte[] b, int off, int len) throws IOException;

    /**
     * @return return the number of bytes remaining in the compressed-data buffer; typically called after
     * the decompressor has finished decompressing the current  gzip stream (a.k.a. "member").
     */
    public int getRemaining();

    /**
     * Resets decompressor and input and output buffers so that a new set of input data can be processed.
     */
    public void reset();

    /**
     * Closes the decompressor and discards any unprocessed input.
     */
    public void end();
}
