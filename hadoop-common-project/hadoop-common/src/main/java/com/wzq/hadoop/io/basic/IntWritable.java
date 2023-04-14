package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for ints
 */
public class IntWritable implements WritableComparable {

    private int value;

    public IntWritable() {
    }

    public IntWritable(int value) {
        set(value);
    }

    /**
     * Set the value of this IntWritable
     *
     * @param value
     */
    public void set(int value) {
        this.value = value;
    }

    /**
     * Return the value of this IntWritable
     *
     * @return
     */
    public int get() {
        return value;
    }

    /**
     * 从流中反序列化对象
     *
     * @param in {@code DataInput}流,从该流中读取数据
     * @throws IOException
     */
    public void readFields(DataInput in) throws IOException {
        value = in.readInt();
    }

    /**
     * 序列化Int到流中
     *
     * @param out {@code DataOutput}流,序列化的结果保存在流中
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException {
        out.writeInt(value);
    }

    /**
     * Return true if Object o is a IntWritable with the same value.
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (!(o instanceof IntWritable)) {
            return false;
        }
        IntWritable other = (IntWritable) o;
        return this.value == other.value;
    }

    public int hashCode() {
        return value;
    }

    /**
     * Compares two IntWritable
     *
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        int thisValue = this.value;
        int thatValue = ((IntWritable) o).value;
        return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
    }

    public String toString() {
        return Integer.toString(value);
    }

    /**
     * A Comparator optimized for IntWritable
     */
    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(IntWritable.class);
        }

        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            int thisValue = readInt(b1, s1);
            int thatValue = readInt(b2, s2);
            return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
        }
    }

    // register this comparator
    static {
        WritableComparator.define(IntWritable.class, new Comparator());
    }
}
