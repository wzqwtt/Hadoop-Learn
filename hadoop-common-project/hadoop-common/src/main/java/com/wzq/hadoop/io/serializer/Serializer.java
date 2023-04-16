package com.wzq.hadoop.io.serializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 用于将一个对象转换为字节流，提供序列化的功能
 * <p>
 * Provides a facility for serializing objects of type to an {@link OutputStream}
 * <p>
 * Serializers are stateful, but most no buffer the output since other producers may write to
 * the output between calls to {@code serialize(Object)}
 */
public interface Serializer<T> {

    /**
     * Prepare the serializer for writing.
     */
    void open(OutputStream out) throws IOException;

    /**
     * Serialize t to the underlying output stream.
     */
    void serialize(T t) throws IOException;

    /**
     * Close the underlying output stream and clear up any resources.
     */
    void close() throws IOException;
}
