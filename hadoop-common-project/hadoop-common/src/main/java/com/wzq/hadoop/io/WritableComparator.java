package com.wzq.hadoop.io;

import com.wzq.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class WritableComparator implements RawComparator {

    // ########################################################################################
    // 工厂
    // registry
    private static HashMap<Class, WritableComparator> comparators = new HashMap<>();

    /**
     * 获取一个实现了WritableComparable的比较器
     *
     * @param c
     * @return
     */
    public static synchronized WritableComparator get(Class<? extends WritableComparable> c) {
        WritableComparator comparator = comparators.get(c);
        if (comparator == null) {
            comparator = new WritableComparator(c, true);
        }
        return comparator;
    }

    /**
     * 对WritableComparable的实现注册一个最优比较器
     *
     * @param c
     * @param comparator
     */
    public static synchronized void define(Class c, WritableComparator comparator) {
        comparators.put(c, comparator);
    }

    // ########################################################################################
    // 属性
    private final Class<? extends WritableComparable> keyClass;
    private final WritableComparable key1;
    private final WritableComparable key2;
    private final DataInputBuffer buffer;

    // 构造方法
    protected WritableComparator(Class<? extends WritableComparable> keyClass) {
        this(keyClass, false);
    }

    protected WritableComparator(Class<? extends WritableComparable> keyClass, boolean createInstance) {
        this.keyClass = keyClass;
        if (createInstance) {
            key1 = newKey();
            key2 = newKey();
            buffer = new DataInputBuffer();
        } else {
            key1 = key2 = null;
            buffer = null;
        }
    }

    /**
     * Returns the {@link WritableComparable} implementation class
     *
     * @return
     */
    public Class<? extends WritableComparable> getKeyClass() {
        return keyClass;
    }

    /**
     * Construct a new {@link WritableComparable} instance
     *
     * @return
     */
    public WritableComparable newKey() {
        return ReflectionUtils.newInstance(keyClass, null);
    }

    // ########################################################################################

    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
        try {
            buffer.reset(b1, s1, l1);
            // 从流中反序列化数据
            key1.readFields(buffer);

            buffer.reset(b2, s2, l2);
            // 从流中反序列化数据
            key2.readFields(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return compare(key1, key2);
    }

    @SuppressWarnings("unchecked")
    public int compare(WritableComparable a, WritableComparable b) {
        return a.compareTo(b);
    }

    public int compare(Object a, Object b) {
        return compare((WritableComparable) a, (WritableComparable) b);
    }

    public static int compareBytes(byte[] b1, int s1, int l1,
                                   byte[] b2, int s2, int l2) {
        int end1 = s1 + l1;
        int end2 = s2 + l2;

        for (int i = s1, j = s2; i < end1 && j < end2; i++, j++) {
            int a = (b1[i] & 0xff);
            int b = (b2[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }

        return l1 - l2;
    }

    /**
     * Compute hash for binary data
     *
     * @param bytes
     * @param length
     * @return
     */
    public static int hashBytes(byte[] bytes, int length) {
        int hash = 1;
        for (int i = 0; i < length; i++) {
            hash = (31 * hash) + (int) bytes[i];
        }
        return hash;
    }

    /**
     * Parse an unsigned short from a byte array
     *
     * @param bytes
     * @param start
     * @return
     */
    public static int readUnsignedShort(byte[] bytes, int start) {
        return (((bytes[start] & 0xff) << 8) + ((bytes[start + 1] & 0xff)));
    }

    /**
     * Parse an integer from a byte array
     *
     * @param bytes
     * @param start
     * @return
     */
    public static int readInt(byte[] bytes, int start) {
        return (((bytes[start] & 0xff) << 24) +
                ((bytes[start + 1] & 0xff) << 16) +
                ((bytes[start + 2] & 0xff) << 8) +
                ((bytes[start + 3] & 0xff)));

    }

    /**
     * Parse a float from a byte array.
     */
    public static float readFloat(byte[] bytes, int start) {
        return Float.intBitsToFloat(readInt(bytes, start));
    }

    /**
     * Parse a long from a byte array.
     */
    public static long readLong(byte[] bytes, int start) {
        return ((long) (readInt(bytes, start)) << 32) +
                (readInt(bytes, start + 4) & 0xFFFFFFFFL);
    }

    /**
     * Parse a double from a byte array.
     */
    public static double readDouble(byte[] bytes, int start) {
        return Double.longBitsToDouble(readLong(bytes, start));
    }

    /**
     * Reads a zero-compressed encoded long from a byte array and returns it.
     *
     * @param bytes byte array with decode long
     * @param start starting index
     * @return deserialized long
     * @throws java.io.IOException
     */
    public static long readVLong(byte[] bytes, int start) throws IOException {
        int len = bytes[start];
        if (len >= -112) {
            return len;
        }
        boolean isNegative = (len < -120);
        len = isNegative ? -(len + 120) : -(len + 112);
        if (start + 1 + len > bytes.length)
            throw new IOException(
                    "Not enough number of bytes for a zero-compressed integer");
        long i = 0;
        for (int idx = 0; idx < len; idx++) {
            i = i << 8;
            i = i | (bytes[start + 1 + idx] & 0xFF);
        }
        return (isNegative ? (i ^ -1L) : i);
    }

    /**
     * Reads a zero-compressed encoded integer from a byte array and returns it.
     *
     * @param bytes byte array with the encoded integer
     * @param start start index
     * @return deserialized integer
     * @throws java.io.IOException
     */
    public static int readVInt(byte[] bytes, int start) throws IOException {
        return (int) readVLong(bytes, start);
    }
}
