package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for Boolean
 */
public class BooleanWritable implements WritableComparable {

    private boolean value;

    public BooleanWritable() {
    }

    public BooleanWritable(boolean value) {
        set(value);
    }

    public void set(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public int hashCode() {
        return value ? 0 : 1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BooleanWritable)) {
            return false;
        }
        BooleanWritable other = (BooleanWritable) obj;
        return this.value == other.value;
    }

    public String toString() {
        return Boolean.toString(get());
    }

    public void write(DataOutput out) throws IOException {
        out.writeBoolean(value);
    }

    public void readFields(DataInput in) throws IOException {
        value = in.readBoolean();
    }

    public int compareTo(Object o) {
        BooleanWritable other = (BooleanWritable) o;
        return value == other.value ? 0 : (value == false ? -1 : 1);
    }

    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(BooleanWritable.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            boolean a = (readInt(b1, s1) == 1) ? true : false;
            boolean b = (readInt(b2, s2) == 1) ? true : false;
            return ((a == b) ? 0 : (a == false) ? -1 : 1);
        }
    }

    static {
        WritableComparator.define(BooleanWritable.class, new Comparator());
    }

}
