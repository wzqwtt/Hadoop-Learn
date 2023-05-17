package com.wzq.hadoop.fs;

import com.wzq.hadoop.conf.Configured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hadoop Abstract FileSystem
 */
public abstract class FileSystem extends Configured implements Closeable {

    /**
     * Statistics类是FileSystem的静态内部类，用于跟踪文件系统操作的统计数据，如读取次数、
     * 写入次数、读取字节数、写入字节数等。它提供了一组方法来获取和重置这些统计信息。
     * <p>
     * 请注意，Statistics类只提供了对单个FileSystem实例的统计信息。如果你有多个FileSystem实例，
     * 你需要分别获取每个实例的Statistics并进行相应的操作。
     */
    public static final class Statistics {
        private final String scheme;    // 用于跟踪文件系统操作的统计信息，并区分不同文件系统的统计数据
        private AtomicLong bytesRead = new AtomicLong();    // 表示已读取的字节数
        private AtomicLong bytesWritten = new AtomicLong(); // 表示已写入的字节数
        private AtomicInteger readOps = new AtomicInteger();    // 表示读取操作的次数
        private AtomicInteger largeReadOps = new AtomicInteger();   // 表示大型读取操作（读取字节数大于`io.file.buffer.size`）的次数
        private AtomicInteger writeOps = new AtomicInteger();   // 表示写入操作的次数

        public Statistics(String scheme) {
            this.scheme = scheme;
        }

        /**
         * Increment the bytes read in the statistics
         *
         * @param newBytes the additional bytes read
         */
        public void incrementBytesRead(long newBytes) {
            bytesRead.getAndAdd(newBytes);
        }

        /**
         * Increment the bytes written in the statistics
         *
         * @param newBytes the additional bytes written
         */
        public void incrementBytesWritten(long newBytes) {
            bytesWritten.getAndAdd(newBytes);
        }

        /**
         * Increment the number of read operations
         *
         * @param count number of read operations
         */
        public void incrementReadOps(int count) {
            readOps.getAndAdd(count);
        }

        /**
         * Increment the number of large read operations
         *
         * @param count number of large read operations
         */
        public void incrementLargeReadOps(int count) {
            largeReadOps.getAndAdd(count);
        }

        /**
         * Increment the number of write operations
         *
         * @param count number of write operations
         */
        public void incrementWriteOps(int count) {
            writeOps.getAndAdd(count);
        }

        /**
         * Get the total number of bytes read
         *
         * @return the number of bytes
         */
        public long getBytesRead() {
            return bytesRead.get();
        }

        /**
         * Get the total number of bytes written
         *
         * @return the number of bytes
         */
        public long getBytesWritten() {
            return bytesWritten.get();
        }

        /**
         * Get the number of file system read operations such as list files
         *
         * @return number of read operations
         */
        public int getReadOps() {
            return readOps.get() + largeReadOps.get();
        }

        /**
         * Get the number of large file system read operations such as list files
         * under a large directory
         *
         * @return number of large read operations
         */
        public int getLargeReadOps() {
            return largeReadOps.get();
        }

        /**
         * Get the number of file system write operations such as create, append
         * rename etc.
         *
         * @return number of write operations
         */
        public int getWriteOps() {
            return writeOps.get();
        }

        public String toString() {
            return bytesRead + " bytes read, " + bytesWritten + " bytes written, "
                    + readOps + " read ops, " + largeReadOps + " large read ops, "
                    + writeOps + " write ops";
        }

        /**
         * Reset the counts of bytes to 0.
         */
        public void reset() {
            bytesWritten.set(0);
            bytesRead.set(0);
        }

        /**
         * Get the uri scheme associated with this statistics object.
         *
         * @return the schema associated with this set of statistics
         */
        public String getScheme() {
            return scheme;
        }

    }
}
