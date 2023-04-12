package com.wzq.hadoop.pattern.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wzq
 * @create 2023-04-12 15:27
 */
public class DecoratorPatternDemo {

    private static final Logger LOG = LoggerFactory.getLogger(DecoratorPatternDemo.class);

    public static void main(String[] args) {
        Shape circle = new Circle();

        ShapeDecorator redCircle = new RedShapeDecorator(new Circle());
        ShapeDecorator redRectangle = new RedShapeDecorator(new Rectangle());

        LOG.info("Circle with normal border");
        circle.draw();

        LOG.info("Circle of red border");
        redCircle.draw();

        LOG.info("Rectangle of red border");
        redRectangle.draw();
    }

}
