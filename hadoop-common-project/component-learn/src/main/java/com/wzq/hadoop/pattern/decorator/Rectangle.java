package com.wzq.hadoop.pattern.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wzq
 * @create 2023-04-12 15:20
 */
public class Rectangle implements Shape {

    private static Logger LOG = LoggerFactory.getLogger(Rectangle.class);

    @Override
    public void draw() {
        LOG.info("Shape : Rectangle");
    }
}
