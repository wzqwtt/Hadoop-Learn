package com.wzq.hadoop.io.basic;

import com.wzq.hadoop.io.WritableComparator;

/**
 * Interface supported by {@link com.wzq.hadoop.io.WritableComparable} types supporting
 * ordering/permutation by a representative set of bytes.
 */
public abstract class BinaryComparable implements Comparable<BinaryComparable> {

    /**
     * Return n st bytes 0...n-1 from {@code getBytes()} are valid
     *
     * @return
     */
    public abstract int getLength();

    /**
     * Return representative byte array for this instance.
     *
     * @return
     */
    public abstract byte[] getBytes();

    /**
     * Compare bytes from {@code getBytes()}
     *
     * @param other
     * @return
     * @see {@link com.wzq.hadoop.io.WritableComparator(...)}
     */
    public int compareTo(BinaryComparable other) {
        if (this == other) {
            return 0;
        }
        return WritableComparator.compareBytes(
                getBytes(), 0, getLength(),
                other.getBytes(), 0, other.getLength()
        );
    }

    public int compareTo(byte[] other, int off, int len) {
        return WritableComparator.compareBytes(
                getBytes(), 0, getLength(),
                other, off, len
        );
    }

    /**
     * Return true if bytes from getBytes() match.
     *
     * @param other
     * @return
     */
    public boolean equals(Object other) {
        if (!(other instanceof BinaryComparable)) {
            return false;
        }
        BinaryComparable that = (BinaryComparable) other;
        if (this.getLength() != that.getLength()) {
            return false;
        }
        return this.compareTo(that) == 0;
    }

    /**
     * Return a hash of the bytes returned from getBytes()
     *
     * @return
     */
    public int hashCode() {
        return WritableComparator.hashBytes(getBytes(), getLength());
    }
}
