package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparable;
import com.wzq.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A {@link com.wzq.hadoop.io.WritableComparable} for Float
 */
public class FloatWritable implements WritableComparable {

    private float value;

    public FloatWritable() {
    }

    public FloatWritable(float value) {
        set(value);
    }

    public void set(float value) {
        this.value = value;
    }

    public float get() {
        return value;
    }

    public void write(DataOutput out) throws IOException {
        out.writeFloat(value);
    }

    public void readFields(DataInput in) throws IOException {
        value = in.readFloat();
    }

    public int hashCode() {
        return Float.floatToIntBits(value);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FloatWritable)) {
            return false;
        }
        FloatWritable other = (FloatWritable) obj;
        return this.value == other.value;
    }

    public String toString() {
        return Float.toString(value);
    }

    public int compareTo(Object o) {
        float thisValue = this.value;
        float thatValue = ((FloatWritable) o).value;
        return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
    }

    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(FloatWritable.class);
        }

        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            float thisValue = readFloat(b1, s1);
            float thatValue = readFloat(b2, s2);
            return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
        }
    }

    static {
        WritableComparator.define(FloatWritable.class, new Comparator());
    }
}
