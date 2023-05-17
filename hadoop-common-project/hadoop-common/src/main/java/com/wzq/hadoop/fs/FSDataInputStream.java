package com.wzq.hadoop.fs;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility that wraps a FSInputStream in a DataInputStream and buffers input through a BufferedInputStream.
 */
public class FSDataInputStream extends DataInputStream
        implements Seekable, PositionedReadable, Closeable {

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public FSDataInputStream(InputStream in) {
        super(in);
        if (!(in instanceof Seekable) || !(in instanceof PositionedReadable)) {
            throw new IllegalArgumentException("In is not an instance of Seekable or PositionedReadable");
        }
    }

    // #######################################################################################################
    // PositionedReadable

    @Override
    public int read(long position, byte[] buffer, int offset, int length) throws IOException {
        return ((PositionedReadable) in).read(position, buffer, offset, length);
    }

    @Override
    public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
        ((PositionedReadable) in).readFully(position, buffer, offset, length);
    }

    @Override
    public void readFully(long position, byte[] buffer) throws IOException {
        ((PositionedReadable) in).readFully(position, buffer);
    }

    // #######################################################################################################
    // Seekable

    @Override
    public void seek(long pos) throws IOException {
        ((Seekable) in).seek(pos);
    }

    @Override
    public long getPos() throws IOException {
        return ((Seekable) in).getPos();
    }

    @Override
    public boolean seekToNewSource(long targetPos) throws IOException {
        return ((Seekable) in).seekToNewSource(targetPos);
    }
}
