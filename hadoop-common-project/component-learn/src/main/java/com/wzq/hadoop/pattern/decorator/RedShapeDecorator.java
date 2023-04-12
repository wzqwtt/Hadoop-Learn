package com.wzq.hadoop.pattern.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建扩展了ShapeDecorator类的实体装饰类
 *
 * @author wzq
 * @create 2023-04-12 15:24
 */
public class RedShapeDecorator extends ShapeDecorator {

    private static Logger LOG = LoggerFactory.getLogger(RedShapeDecorator.class);

    public RedShapeDecorator(Shape decoratedShape) {
        super(decoratedShape);
    }

    @Override
    public void draw() {
        decoratedShape.draw();
        setRedBorder(decoratedShape);
    }

    private void setRedBorder(Shape decoratedShape) {
        LOG.info("Broder Color: Red");
    }
}
