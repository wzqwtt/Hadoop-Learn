package com.wzq.hadoop.pattern.decorator;

/**
 * 实现Shape接口的抽象装饰类
 *
 * @author wzq
 * @create 2023-04-12 15:22
 */
public abstract class ShapeDecorator implements Shape {

    protected Shape decoratedShape;

    public ShapeDecorator(Shape decoratedShape) {
        this.decoratedShape = decoratedShape;
    }

    public void draw() {
        decoratedShape.draw();
    }
}
