package com.wzq.hadoop.io;

import com.wzq.hadoop.io.entity.Block;
import com.wzq.hadoop.util.PrintUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author wzq
 * @create 2023-04-12 21:12
 */
public class TestWritable {

    private static final Logger LOG = LoggerFactory.getLogger(TestWritable.class);

    @Test
    public void testWritable() {
        Block block = new Block(7806259420524417791L, 39447755L, 56736651L);

        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos)
        ) {
            block.write(dos);
            PrintUtil.printHex(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWritableToFile() {
        Block block = new Block(7806259420524417791L, 39447755L, 56736651L);

        try (
                FileOutputStream bos = new FileOutputStream("demo.txt");
                DataOutputStream dos = new DataOutputStream(bos)
        ) {
            block.write(dos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFromFileWritable() {
        try (
                DataInputStream dis = new DataInputStream(new FileInputStream("demo.txt"))
        ) {
            Block block = Block.read(dis);
            LOG.debug("反序列化后block => [{}]",block.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
