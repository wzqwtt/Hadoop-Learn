package com.wzq.hadoop.io;

import com.wzq.hadoop.io.basic.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 测试基础数据类型的Writable
 */
public class TestPrimitiveWritable {

    private static final Logger LOG = LoggerFactory.getLogger(TestPrimitiveWritable.class);

    private static final int intValue = 100;
    private static final long longValue = 10000L;
    private static final boolean booleanValue = true;
    private static final byte byteValue = 57;
    private static final float floatValue = 222.222F;
    private static final double doubleValue = 11.1111111111111111111111;

    private static final IntWritable intWritable = new IntWritable(intValue);
    private static final LongWritable longWritable = new LongWritable(longValue);
    private static final BooleanWritable booleanWritable = new BooleanWritable(booleanValue);
    private static final ByteWritable byteWritable = new ByteWritable(byteValue);
    private static final FloatWritable floatWritable = new FloatWritable(floatValue);
    private static final DoubleWritable doubleWritable = new DoubleWritable(doubleValue);

    private static FileOutputStream fos = null;
    private static DataOutputStream dos = null;

    private static FileInputStream fis = null;
    private static DataInputStream dis = null;

    private static final String tmpFileName = "TestPrimitiveWritable.txt";

    @Before
    public void init() {
        try {
            fos = new FileOutputStream(tmpFileName);
            dos = new DataOutputStream(fos);

            fis = new FileInputStream(tmpFileName);
            dis = new DataInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrimitiveWritable() {
        try {
            // 序列化
            intWritable.write(dos);
            // 反序列化
            intWritable.readFields(dis);
            Assert.assertEquals(intValue, intWritable.get());

            longWritable.write(dos);
            longWritable.readFields(dis);
            Assert.assertEquals(longValue, longWritable.get());

            floatWritable.write(dos);
            floatWritable.readFields(dis);
            Assert.assertEquals(floatValue, floatWritable.get(), 0);

            booleanWritable.write(dos);
            booleanWritable.readFields(dis);
            Assert.assertEquals(booleanValue, booleanWritable.get());

            byteWritable.write(dos);
            byteWritable.readFields(dis);
            Assert.assertEquals(byteValue, byteWritable.get());

            doubleWritable.write(dos);
            doubleWritable.readFields(dis);
            Assert.assertEquals(doubleValue, doubleWritable.get(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void close() {
        try {
            fos.close();
            dos.close();
            fis.close();
            dis.close();

            // 删除文件
            if (new File(tmpFileName).delete()) {
                LOG.info("[{}] 文件删除成功！");
            } else {
                LOG.info("[{}] 文件删除失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
