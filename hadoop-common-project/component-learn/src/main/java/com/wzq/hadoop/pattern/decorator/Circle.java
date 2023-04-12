package com.wzq.hadoop.pattern.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wzq
 * @create 2023-04-12 15:21
 */
public class Circle implements Shape {

    private static final Logger LOG = LoggerFactory.getLogger(Circle.class);

    @Override
    public void draw() {
        LOG.info("Shape : Circle");
    }
}
