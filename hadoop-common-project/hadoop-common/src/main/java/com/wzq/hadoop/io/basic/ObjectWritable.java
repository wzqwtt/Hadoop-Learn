package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.conf.Configurable;
import com.wzq.hadoop.conf.Configuration;
import com.wzq.hadoop.conf.Configured;
import com.wzq.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * A polymorphic Writable that writes an instance with it's class name.
 * Handlers arrays, strings and primitive types without a Writable wrapper.
 */
public class ObjectWritable implements Writable, Configurable {

    private Class declaredClass;    // 该对象运行时类的Class
    private Object instance;    // 对象实例
    private Configuration conf; // 该对象的配置类

    public ObjectWritable() {
    }

    public ObjectWritable(Object instance) {
        set(instance);
    }

    public ObjectWritable(Class declaredClass, Object instance) {
        this.declaredClass = declaredClass;
        this.instance = instance;
    }

    public void set(Object instance) {
        this.declaredClass = instance.getClass();
        this.instance = instance;
    }

    public Object get() {
        return instance;
    }

    public Class getDeclaredClass() {
        return declaredClass;
    }

    public String toString() {
        return "OW[class=" + declaredClass + ",value=" + instance + "]";
    }

    /**
     * 序列化
     *
     * @param out {@code DataOutput}流,序列化的结果保存在流中
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException {
        writeObject(out, instance, declaredClass, conf);
    }

    /**
     * 反序列化
     *
     * @param in {@code DataInput}流,从该流中读取数据
     * @throws IOException
     */
    public void readFields(DataInput in) throws IOException {
        readObject(in, this, this.conf);
    }

    /**
     * k-基本数据类型，v-基本数据类型.class
     */
    private static final Map<String, Class<?>> PRIMITIVE_NAMES = new HashMap<>();

    static {
        PRIMITIVE_NAMES.put("boolean", Boolean.TYPE);
        PRIMITIVE_NAMES.put("byte", Byte.TYPE);
        PRIMITIVE_NAMES.put("char", Character.TYPE);
        PRIMITIVE_NAMES.put("short", Short.TYPE);
        PRIMITIVE_NAMES.put("int", Integer.TYPE);
        PRIMITIVE_NAMES.put("long", Long.TYPE);
        PRIMITIVE_NAMES.put("float", Float.TYPE);
        PRIMITIVE_NAMES.put("double", Double.TYPE);
        PRIMITIVE_NAMES.put("void", Void.TYPE);
    }

    private static class NullInstance extends Configured implements Writable {
        private Class<?> declaredClass;

        public NullInstance() {
            super(null);
        }

        public NullInstance(Class declaredClass, Configuration conf) {
            super(conf);
            this.declaredClass = declaredClass;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            UTF8.writeString(out, declaredClass.getName());
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            String className = UTF8.readString(in);
            declaredClass = PRIMITIVE_NAMES.get(className);
            if (declaredClass == null) {
                try {
                    declaredClass = getConf().getClassByName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.toString());
                }
            }
        }
    }

    /**
     * Write a Writable, String, primitive type, or an array of the preceding
     */
    public static void writeObject(DataOutput out, Object instance,
                                   Class declaredClass,
                                   Configuration conf) throws IOException {
        if (instance == null) {
            instance = new NullInstance(declaredClass, conf);
            declaredClass = Writable.class;
        }

        // always write declared
        UTF8.writeString(out, declaredClass.getName());

        // array
        if (declaredClass.isArray()) {
            int length = Array.getLength(instance);
            out.writeInt(length);
            for (int i = 0; i < length; i++) {
                writeObject(out, Array.get(instance, i),
                        declaredClass.getComponentType(), conf);
            }
        } else if (declaredClass == String.class) { // String
            UTF8.writeString(out, (String) instance);
        } else if (declaredClass.isPrimitive()) {
            // Primitive type 基础数据类型
            if (declaredClass == Boolean.TYPE) {        // boolean
                out.writeBoolean(((Boolean) instance).booleanValue());
            } else if (declaredClass == Character.TYPE) { // char
                out.writeChar(((Character) instance).charValue());
            } else if (declaredClass == Byte.TYPE) {    // byte
                out.writeByte(((Byte) instance).byteValue());
            } else if (declaredClass == Short.TYPE) {   // short
                out.writeShort(((Short) instance).shortValue());
            } else if (declaredClass == Integer.TYPE) { // int
                out.writeInt(((Integer) instance).intValue());
            } else if (declaredClass == Long.TYPE) {    // long
                out.writeLong(((Long) instance).longValue());
            } else if (declaredClass == Float.TYPE) {   // float
                out.writeFloat(((Float) instance).floatValue());
            } else if (declaredClass == Double.TYPE) {  // double
                out.writeDouble(((Double) instance).doubleValue());
            } else if (declaredClass == Void.TYPE) {    // void
            } else {
                throw new IllegalArgumentException("Not a primitive: " + declaredClass);
            }
        } else if (declaredClass.isEnum()) { // enum
            UTF8.writeString(out, ((Enum) instance).name());
        } else if (Writable.class.isAssignableFrom(declaredClass)) { // Writable
            UTF8.writeString(out, instance.getClass().getName());
            ((Writable) instance).write(out);
        } else {
            throw new IOException("Can't write: " + instance + " as " + declaredClass);
        }
    }


    /**
     * Read a {@link Writable}, {@link String}, primitive type, or an array of
     * the preceding.
     */
    public static Object readObject(DataInput in, Configuration conf)
            throws IOException {
        return readObject(in, null, conf);
    }

    /**
     * Read a {@link Writable}, {@link String}, primitive type, or an array of
     * the preceding.
     */
    @SuppressWarnings("unchecked")
    public static Object readObject(DataInput in, ObjectWritable objectWritable, Configuration conf)
            throws IOException {
        String className = UTF8.readString(in);
        Class<?> declaredClass = PRIMITIVE_NAMES.get(className);
        if (declaredClass == null) {
            try {
                declaredClass = conf.getClassByName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("readObject can't find class " + className, e);
            }
        }

        Object instance;

        if (declaredClass.isPrimitive()) {            // primitive types

            if (declaredClass == Boolean.TYPE) {             // boolean
                instance = Boolean.valueOf(in.readBoolean());
            } else if (declaredClass == Character.TYPE) {    // char
                instance = Character.valueOf(in.readChar());
            } else if (declaredClass == Byte.TYPE) {         // byte
                instance = Byte.valueOf(in.readByte());
            } else if (declaredClass == Short.TYPE) {        // short
                instance = Short.valueOf(in.readShort());
            } else if (declaredClass == Integer.TYPE) {      // int
                instance = Integer.valueOf(in.readInt());
            } else if (declaredClass == Long.TYPE) {         // long
                instance = Long.valueOf(in.readLong());
            } else if (declaredClass == Float.TYPE) {        // float
                instance = Float.valueOf(in.readFloat());
            } else if (declaredClass == Double.TYPE) {       // double
                instance = Double.valueOf(in.readDouble());
            } else if (declaredClass == Void.TYPE) {         // void
                instance = null;
            } else {
                throw new IllegalArgumentException("Not a primitive: " + declaredClass);
            }

        } else if (declaredClass.isArray()) {              // array
            int length = in.readInt();
            instance = Array.newInstance(declaredClass.getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(instance, i, readObject(in, conf));
            }

        } else if (declaredClass == String.class) {        // String
            instance = UTF8.readString(in);
        } else if (declaredClass.isEnum()) {         // enum
            instance = Enum.valueOf((Class<? extends Enum>) declaredClass, UTF8.readString(in));
        } else {                                      // Writable
            Class instanceClass = null;
            String str = "";
            try {
                str = UTF8.readString(in);
                instanceClass = conf.getClassByName(str);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("readObject can't find class " + str, e);
            }
            // TODO Writable Factory create new Instance
            Writable writable = null;
            writable.readFields(in);
            instance = writable;

            if (instanceClass == NullInstance.class) {  // null
                declaredClass = ((NullInstance) instance).declaredClass;
                instance = null;
            }
        }
        if (objectWritable != null) {                 // store values
            objectWritable.declaredClass = declaredClass;
            objectWritable.instance = instance;
        }
        return instance;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return this.conf;
    }

}
