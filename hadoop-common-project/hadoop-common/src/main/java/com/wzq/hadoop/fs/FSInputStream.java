package com.wzq.hadoop.fs;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public abstract class FSInputStream extends InputStream
        implements Seekable, PositionedReadable {

    // #######################################################################################################
    // PositionedReadable

    @Override
    public int read(long position, byte[] buffer, int offset, int length) throws IOException {
        synchronized (this) {
            // 保存原来的position
            long oldPos = getPos();
            int nread = -1;
            try {
                // 定位position
                seek(position);
                nread = read(buffer, offset, length);
            } finally {
                // 回退到原来的位置
                seek(oldPos);
            }
            return nread;
        }
    }

    @Override
    public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
        int nread = 0;
        while (nread < length) {
            int nbytes = read(position + nread, buffer, offset + nread, length - nread);
            if (nbytes < 0) {
                throw new EOFException("End of file reached before reading fully.");
            }
            nread += nbytes;
        }
    }

    @Override
    public void readFully(long position, byte[] buffer) throws IOException {
        readFully(position, buffer, 0, buffer.length);
    }

    // #######################################################################################################
    // Seekable - 抽象方法

    @Override
    public abstract void seek(long pos) throws IOException;

    @Override
    public abstract long getPos() throws IOException;

    @Override
    public abstract boolean seekToNewSource(long targetPos) throws IOException;
}
