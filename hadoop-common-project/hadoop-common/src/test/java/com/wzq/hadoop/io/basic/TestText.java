package com.wzq.hadoop.io.basic;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author wzq
 * @create 2023-05-16 14:45
 */
public class TestText {

    private static final Logger LOG = LoggerFactory.getLogger(TestText.class);

    /**
     * {@link java.nio.charset.Charset}
     */
    @Test
    public void testCharsetClass() {
        // 1、获取字符集的方式
        // 1.1 返回指定的字符集CharSet
        Charset charset = Charset.forName("UTF-8");
        // 1.2 返回虚拟机默认的字符集Charset
        // Charset.defaultCharset() - UTF-8

        LOG.info("{} -- {}", charset.name(), charset.canEncode());
        Iterator<String> iterator = charset.aliases().iterator();
        while (iterator.hasNext()) {
            LOG.info(iterator.next());
        }

        // 2、编码
        ByteBuffer buffer = charset.encode("sdf");
        LOG.info("encode buffer : {}", buffer);
        while (buffer.hasRemaining()) {
            LOG.info("{}", (char) buffer.get());
        }

        // 3、解码
        buffer.flip();  // 切换读
        CharBuffer decode = charset.decode(buffer);
        LOG.info("decode : {}", decode.toString());
    }


}
