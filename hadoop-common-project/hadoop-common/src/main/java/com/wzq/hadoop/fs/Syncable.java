package com.wzq.hadoop.fs;

import java.io.IOException;

/**
 * This interface declare the sync() operation.
 */
public interface Syncable {

    /**
     * Synchronize all buffer with the underling devices.
     *
     * @throws IOException
     */
    public void sync() throws IOException;

}
