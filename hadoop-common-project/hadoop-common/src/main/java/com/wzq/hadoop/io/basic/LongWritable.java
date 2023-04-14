package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for Long
 */
public class LongWritable implements WritableComparable {

    private long value;

    public LongWritable() {
    }

    public LongWritable(long value) {
        set(value);
    }

    public void set(long value) {
        this.value = value;
    }

    public long get() {
        return value;
    }


    public void write(DataOutput out) throws IOException {
        out.writeLong(value);
    }

    public void readFields(DataInput in) throws IOException {
        value = in.readLong();
    }

    public boolean equals(Object o) {
        if (!(o instanceof LongWritable)) {
            return false;
        }
        LongWritable other = (LongWritable) o;
        return this.value == other.value;
    }

    public int hashCode() {
        return (int) value;
    }

    public int compareTo(Object o) {
        long thisValue = this.value;
        long thatValue = ((LongWritable) o).value;
        return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
    }

    public String toString() {
        return Long.toString(value);
    }

    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(LongWritable.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            long thisValue = readLong(b1, s1);
            long thatValue = readLong(b2, s2);
            return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
        }
    }

    static {
        WritableComparator.define(LongWritable.class, new Comparator());
    }

    /**
     * TODO
     * A decreasing Comparator optimized for LongWritable.
     */
    public static class DecreasingComparator extends Comparator {
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }

        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }
}
