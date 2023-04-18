package com.wzq.hadoop.io.compress;

import com.wzq.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * 基于流的compressor，它可以插入 CompressionOutputStream压缩数据。这等价于{@link java.util.zip.Deflater}
 */
public interface Compressor {

    /**
     * 设置输入数据压缩。当调用needsInput()方法返回true时，表明可以设置更多的数据
     *
     * @param b   input data
     * @param off start offset
     * @param len length
     */
    public void setInput(byte[] b, int off, int len);

    /**
     * @return 如果数据缓冲区是空的并且调用setInput()方法为了获取更多数据时返回true
     */
    public boolean needsInput();

    /**
     * 设置用于压缩的预备字典。当历史缓冲区可以被提前确认使用预备字典
     *
     * @param b   Dictionary data bytes
     * @param off Start offset
     * @param len length
     */
    public void setDictionary(byte[] b, int off, int len);

    /**
     * @return 迄今为止没有被解压缩的输入bytes
     */
    public long getBytesRead();

    /**
     * When called, indicates that compression should end with the current contents of the input buffer.
     */
    public void finish();

    /**
     * @return true if the end of the compressed data output stream has been reached.
     */
    public boolean finished();

    /**
     * Fills specified buffer with compressed data. return the actual number of bytes of compressed data.
     * A return value of 0 indicates that needsInput() should be called in order to determine if more input
     * data is required.
     *
     * @param b   Buffer for the compressed data
     * @param off Start offset of the data
     * @param len size of the buffer
     * @return the actual number of bytes of compressed data.
     */
    public int compress(byte[] b, int off, int len) throws IOException;

    /**
     * Resets compressor so that a new set of input data can be processed.
     */
    public void reset();

    /**
     * Closes the compressor and discards any unprocessed input.
     */
    public void end();

    /**
     * Prepare the compressor to be used in a new stream with settings defined in the given Configuration.
     *
     * @param conf Configuration from which new setting are fetched.
     */
    public void reinit(Configuration conf);
}
