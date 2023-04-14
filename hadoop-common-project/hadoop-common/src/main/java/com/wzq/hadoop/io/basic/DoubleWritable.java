package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for Double
 */
public class DoubleWritable implements WritableComparable {

    private double value;

    public DoubleWritable() {
    }

    public DoubleWritable(double value) {
        set(value);
    }

    public void set(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }

    public int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DoubleWritable)) {
            return false;
        }
        DoubleWritable other = (DoubleWritable) obj;
        return this.value == other.value;
    }

    public String toString() {
        return Double.toString(value);
    }

    public void write(DataOutput out) throws IOException {
        out.writeDouble(value);
    }

    public void readFields(DataInput in) throws IOException {
        value = in.readDouble();
    }

    public int compareTo(Object o) {
        DoubleWritable other = (DoubleWritable) o;
        return value < other.value ? -1 : (value == other.value ? 0 : 1);
    }

    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(DoubleWritable.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            double thisValue = readDouble(b1, s1);
            double thatValue = readDouble(b2, s2);
            return thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1);
        }
    }

    static {
        WritableComparator.define(DoubleWritable.class, new Comparator());
    }
}
