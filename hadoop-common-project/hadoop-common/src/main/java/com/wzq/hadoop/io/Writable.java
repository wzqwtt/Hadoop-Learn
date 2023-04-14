package com.wzq.hadoop.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 基于{@link DataInput}和{@link DataOutput}实现的一个简单\高效的序列化协议
 * <p>
 * 在Hadoop Map-Reduce框架中所有具有key或value类型的类都实现了这个接口
 *
 * <b>在实现这个接口的时候,通常还会再实现一个静态{@code read(DataInput)}方法,
 * 它构造一个新的实例,调用{@code readFields(DataInput)}方法并且返回这个实例</b>
 * <p>
 * 例子:
 * <p><blockquote><pre>
 * public class MyWriteable implements Writable {
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
 * }
 * </p></blockquote></pre>
 *
 * @author wzq
 * @create 2023-04-12 20:36
 */
public interface Writable {

    /**
     * 输出(序列化)对象到流中
     *
     * @param out {@code DataOutput}流,序列化的结果保存在流中
     * @throws IOException
     */
    void write(DataOutput out) throws IOException;

    /**
     * 从流中读取(反序列化)对象
     * 为了效率,请尽可能复用现有的对象
     *
     * @param in {@code DataInput}流,从该流中读取数据
     * @throws IOException
     */
    void readFields(DataInput in) throws IOException;
}
