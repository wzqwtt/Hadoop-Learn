package com.wzq.hadoop.io.serializer;

/**
 * 封装{@link Serializer}和{@link Deserializer}
 */
public interface Serialization<T> {

    /**
     * Allows clients to test whether this {@link Serialization} supports the given class.
     */
    boolean accept(Class<?> c);

    /**
     * @return a Serializer for the given class.
     */
    Serializer<T> getSerializer(Class<T> c);

    /**
     * @return a Deserializer for given class.
     */
    Deserializer<T> getDeserializer(Class<T> c);
}
