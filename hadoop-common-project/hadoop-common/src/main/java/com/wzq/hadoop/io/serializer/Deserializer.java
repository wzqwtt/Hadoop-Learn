package com.wzq.hadoop.io.serializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * 提供反序列化的功能
 * <p>
 * Provides a facility for deserializing objects of type from an {@link InputStream}
 */
public interface Deserializer<T> {

    /**
     * Prepare the deserializer for reading
     */
    void open(InputStream in) throws IOException;

    /**
     * 反序列化到T
     */
    T deserialize(T t) throws IOException;

    /**
     * Close the underlying input stream and clear up any resources.
     */
    void close() throws IOException;
}
