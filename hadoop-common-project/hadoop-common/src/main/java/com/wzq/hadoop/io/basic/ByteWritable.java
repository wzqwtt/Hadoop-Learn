package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for single Byte
 */
public class ByteWritable implements WritableComparable {

    private byte value;

    public ByteWritable() {
    }

    public ByteWritable(byte value) {
        set(value);
    }

    public void set(byte value) {
        this.value = value;
    }

    public byte get() {
        return value;
    }

    public int hashCode() {
        return (int)value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ByteWritable)) {
            return false;
        }
        ByteWritable other = (ByteWritable) obj;
        return this.value == other.value;
    }

    public String toString() {
        return Byte.toString(value);
    }

    public void write(DataOutput out) throws IOException {
        out.writeByte(value);
    }

    public void readFields(DataInput in) throws IOException {
        value = in.readByte();
    }

    public int compareTo(Object o) {
        ByteWritable other = (ByteWritable) o;
        return value < other.value ? -1 : (value == other.value ? 0 : 1);
    }

    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(ByteWritable.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            byte thisValue = b1[s1];
            byte thatValue = b2[s2];
            return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
        }
    }

    static {
        WritableComparator.define(ByteWritable.class, new Comparator());
    }

}
