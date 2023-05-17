package com.wzq.hadoop.fs;

import org.omg.CORBA.PUBLIC_MEMBER;

import javax.swing.plaf.PanelUI;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class FSDataOutputStream extends DataOutputStream implements Syncable {

    // 包装
    private OutputStream wrappedStream;

    // ######################################################################################
    // 构造方法

    @Deprecated
    public FSDataOutputStream(OutputStream out) throws IOException {
        this(out, null);
    }

    public FSDataOutputStream(OutputStream out, FileSystem.Statistics stats) throws IOException {
        this(out, stats, 0);
    }

    public FSDataOutputStream(OutputStream out,
                              FileSystem.Statistics stats,
                              long startPosition)
            throws IOException {
        super(new PositionCache(out, stats, startPosition));
        wrappedStream = out;
    }

    /**
     * 用于缓存写入位置的信息。主要目的是在多线程环境下提供更高效的写入操作
     */
    private static class PositionCache extends FilterOutputStream {

        private FileSystem.Statistics statistics;
        long position;

        public PositionCache(OutputStream out,
                             FileSystem.Statistics stats,
                             long pos) {
            super(out);
            statistics = stats;
            position = pos;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
            position++;
            if (statistics != null) {
                statistics.incrementBytesWritten(1);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            position += len;    // update position
            if (statistics != null) {
                statistics.incrementBytesWritten(len);
            }
        }

        public long getPos() throws IOException {
            return position;    // return cached position
        }

        public void close() throws IOException {
            out.close();
        }
    }

    public long getPos() throws IOException {
        return ((PositionCache) out).getPos();
    }

    public void close() throws IOException {
        out.close();    // This invokes PositionCache.close()
    }

    // Returns the underlying output stream. This is used by unit tests.
    public OutputStream getWrappedStream() {
        return wrappedStream;
    }

    @Override
    public void sync() throws IOException {
        if (wrappedStream instanceof Syncable) {
            ((Syncable) wrappedStream).sync();
        }
    }
}
