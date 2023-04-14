package com.wzq.hadoop.io;

/**
 * 提供序列化与比较的能力
 * <p>
 * {@code WritableComparable}可以进行对比. 所有MapReduce中的key都应该实现此接口
 * <p>
 * 实现Comparable的接口意味着可以排序
 * <p>
 * 例子:
 * <p><blockquote><pre>
 * public class MyWriteableComparable implements Writable {
 *      private int counter;
 *      private long timestamp;
 *
 *      public void write(DataOutput out) throws IOException {
 *          out.writeInt(counter);
 *          out.writeLong(timestamp);
 *      }
 *
 *      public void readFields(DataInput in) throws IOException {
 *          counter = in.readInt();
 *          timestamp = in.readLong();
 *      }
 *
 *      public static MyWriteable read(DataInput in) throws IOException {
 *          MyWriteable w = new MyWriteable();
 *          w.readFields(in);
 *          return w;
 *      }
 *
 *      public int compareTo(MyWriteableComparable w) {
 *          int thisValue = this.value;
 *          int thatValue = ((IntWritable)o).value;
 *          return (thisValue < thatValue ? -1 : (thisValue==thatValue ? 0 : 1));
 *      }
 * }
 * </p></blockquote></pre>
 */
public interface WritableComparable<T> extends Writable, Comparable<T> {
}
