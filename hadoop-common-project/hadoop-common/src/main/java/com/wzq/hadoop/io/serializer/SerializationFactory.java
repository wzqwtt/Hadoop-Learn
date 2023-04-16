package com.wzq.hadoop.io.serializer;

import com.wzq.hadoop.conf.Configuration;
import com.wzq.hadoop.conf.Configured;
import com.wzq.hadoop.util.ReflectionUtils;
import com.wzq.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory for Serializations
 */
public class SerializationFactory extends Configured {

    private static final Logger LOG = LoggerFactory.getLogger(SerializationFactory.class);

    private List<Serialization<?>> serializations = new ArrayList<Serialization<?>>();

    public SerializationFactory(Configuration conf) {
        super(conf);
        for (String serializerName : conf.getStrings("io.serializations",
                new String[]{"com.wzq.hadoop.io.serializer.WritableSerialization"})) {
            add(conf, serializerName);
        }
    }

    @SuppressWarnings("unchecked")
    private void add(Configuration conf, String serializerName) {
        try {
            Class<? extends Serialization> serializationClass =
                    (Class<? extends Serialization>) conf.getClassByName(serializerName);
            LOG.debug("serializationClass: [{}]",serializationClass.getName());
            serializations.add(ReflectionUtils.newInstance(serializationClass, getConf()));
        } catch (ClassNotFoundException e) {
            LOG.warn("Serilization class not found: " + StringUtils.stringifyException(e));
        }
    }

    /**
     * 获取Class的序列化器
     */
    public <T> Serializer<T> getSerializer(Class<T> c) {
        return getSerialization(c).getSerializer(c);
    }

    /**
     * 获取Class的反序列化器
     */
    public <T> Deserializer<T> getDeserializer(Class<T> c) {
        return getSerialization(c).getDeserializer(c);
    }

    @SuppressWarnings("unchecked")
    public <T> Serialization<T> getSerialization(Class<T> c) {
        for (Serialization serialization : serializations) {
            if (serialization.accept(c)) {
                return (Serialization<T>) serialization;
            }
        }
        return null;
    }
}
