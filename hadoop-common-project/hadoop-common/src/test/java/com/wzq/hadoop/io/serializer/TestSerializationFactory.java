package com.wzq.hadoop.io.serializer;

import com.wzq.hadoop.conf.Configuration;
import com.wzq.hadoop.io.basic.IntWritable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author wzq
 * @create 2023-04-16 21:00
 */
public class TestSerializationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TestSerializationFactory.class);

    private static Configuration conf;
    private static SerializationFactory factory;
    private static Serializer<IntWritable> serializer;
    private static Deserializer<IntWritable> deserializer;

    private static final String tmpFileName = "test.txt";

    @Before
    public void init() {
        conf = new Configuration();
        factory = new SerializationFactory(conf);
        serializer = factory.getSerializer(IntWritable.class);
        deserializer = factory.getDeserializer(IntWritable.class);

        // open
        try {
            serializer.open(new FileOutputStream(tmpFileName));
            deserializer.open(new FileInputStream(tmpFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        try {
            IntWritable intWritable = new IntWritable(20);
            serializer.serialize(intWritable);

            IntWritable deserialize = deserializer.deserialize(intWritable);
            Assert.assertEquals(intWritable.get(), deserialize.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void close() {
        try {
            serializer.close();
            deserializer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 删除文件
        if (new File(tmpFileName).delete()) {
            LOG.info("[{}] 文件删除成功！");
        } else {
            LOG.info("[{}] 文件删除失败！");
        }
    }


}
