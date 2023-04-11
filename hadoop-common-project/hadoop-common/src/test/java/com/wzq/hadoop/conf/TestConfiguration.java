package com.wzq.hadoop.conf;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Configuration测试类
 *
 * @author wzq
 * @create 2023-04-10 23:35
 */
public class TestConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TestConfiguration.class);
    private static final String fileName = "core-default.xml";

    private static Configuration configuration;

    // 测试配置的属性
    private static String rawProp;
    private static String prop;
    private static String intProp;
    private static String longProp;
    private static String floatProp;
    private static String booleanProp;

    @Before
    public void create() {
        configuration = new Configuration();
        // 添加默认资源，可写可不写，因为在Configuration中已经默认添加core-default.xml了
        // 这里可以添加任何的xml资源，但需要遵守hadoop配置文件规范
        configuration.addResource(fileName);

        rawProp = "hadoop.security.group.mapping";
        prop = "hadoop.tmp.dir";
        intProp = "hadoop.logfile.count";
        longProp = "local.cache.size";
        floatProp = "io.mapfile.bloom.error.rate";
        booleanProp = "fs.har.impl.disable.cache";
    }

    @Test
    public void testConfigurationGet() {
        String rawPropValue = configuration.getRaw(rawProp);
        String propValue = configuration.get(prop);
        int intPropValue = configuration.getInt(intProp, 1);
        long longPropValue = configuration.getLong(longProp, 1l);
        float floatPropValue = configuration.getFloat(floatProp, 1.0f);
        boolean booleanPropValue = configuration.getBoolean(booleanProp, true);

        LOG.info("rawProp : [{}], rawPropValue : [{}]", rawProp, rawPropValue);
        LOG.info("prop : [{}], propValue : [{}]", prop, propValue);
        LOG.info("intProp : [{}], intPropValue : [{}]", intProp, intPropValue);
        LOG.info("longProp : [{}], longPropValue : [{}]", longProp, longPropValue);
        LOG.info("floatProp : [{}], floatPropValue : [{}]", floatProp, floatPropValue);
        LOG.info("booleanProp : [{}], booleanPropValue : [{}]", booleanProp, booleanPropValue);
    }

    @Test
    public void testGetResource() {
        ClassLoader classLoader = TestConfiguration.class.getClassLoader();
        URL url = classLoader.getResource("core-default.xml");
        LOG.debug("resource => [{}]", url);
    }

    @Test
    public void testHexDigits() {
        String hexDigits = configuration.getHexDigits("0x7b");
        LOG.debug("hexDigits : [{}]", hexDigits);
    }

}
