package com.wzq.hadoop.io.serializer;

import com.wzq.hadoop.conf.Configuration;
import com.wzq.hadoop.conf.Configured;
import com.wzq.hadoop.io.Writable;
import com.wzq.hadoop.util.ReflectionUtils;

import java.io.*;

/**
 *
 */
public class WritableSerialization extends Configured
        implements Serialization<Writable> {

    /**
     * 序列化器
     */
    static class WritableSerializer implements Serializer<Writable> {

        private DataOutputStream dataOut;

        @Override
        public void open(OutputStream out) throws IOException {
            if (out instanceof DataOutputStream) {
                this.dataOut = (DataOutputStream) out;
            } else {
                dataOut = new DataOutputStream(out);
            }
        }

        @Override
        public void serialize(Writable writable) throws IOException {
            writable.write(dataOut);
        }

        @Override
        public void close() throws IOException {
            dataOut.close();
        }
    }

    /**
     * 反序列化器
     */
    static class WritableDeserializer extends Configured implements Deserializer<Writable> {

        private DataInputStream dataIn;
        private Class<?> writableClass;

        public WritableDeserializer(Configuration conf, Class<?> c) {
            super(conf);
            this.writableClass = c;
        }

        @Override
        public void open(InputStream in) throws IOException {
            if (in instanceof DataInputStream) {
                this.dataIn = (DataInputStream) in;
            } else {
                this.dataIn = new DataInputStream(in);
            }
        }

        @Override
        public Writable deserialize(Writable w) throws IOException {
            Writable writable;
            if (w == null) {
                // 通过反射创建Writable
                writable = (Writable) ReflectionUtils.newInstance(writableClass, getConf());
            } else {
                writable = w;
            }
            writable.readFields(dataIn);
            return writable;
        }

        @Override
        public void close() throws IOException {
            dataIn.close();
        }
    }

    @Override
    public boolean accept(Class<?> c) {
        return Writable.class.isAssignableFrom(c);
    }

    @Override
    public Serializer<Writable> getSerializer(Class<Writable> c) {
        return new WritableSerializer();
    }

    @Override
    public Deserializer<Writable> getDeserializer(Class<Writable> c) {
        return new WritableDeserializer(getConf(), c);
    }
}
