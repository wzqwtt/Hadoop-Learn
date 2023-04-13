package com.wzq.hadoop.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author wzq
 * @create 2023-04-13 22:42
 */
public class TestStringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TestStringUtils.class);

    private static String testString;

    static {
        testString = "com.wzq.hadoop,com.wtt.hadoop";
    }

    @Test
    public void testGetStrings() {
        String[] strings = StringUtils.getStrings(testString);
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s + "\n");
        }
        LOG.info("test getStrings method : [{}]", sb.toString());
    }

    @Test
    public void testGetStringCollection() {
        Collection<String> collection = StringUtils.getStringCollection(testString);
        Iterator<String> iterator = collection.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next() + "\n");
        }
        LOG.info("test getStringCollection method : [{}]", sb.toString());
    }
}
