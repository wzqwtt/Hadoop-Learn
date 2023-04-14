package com.wzq.hadoop.io;

import java.util.Comparator;

/**
 * RawComparator接口允许执行者比较流中读取的未被反序列化为对象的记录,从而省去了创建对象的所有开销
 * <p>
 * Comparator => 外部比较器(定制排序); Comparable => 内部比较器(自然排序)
 */
public interface RawComparator<T> extends Comparator<T> {

    /**
     *
     * @param b1 字节数组b1
     * @param s1 b1数组指定开始位置
     * @param l1 b1数组长度
     * @param b2 字节数组b2
     * @param s2 b2数组指定开始位置
     * @param l2 b2数组长度
     * @return
     */
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2);

}
