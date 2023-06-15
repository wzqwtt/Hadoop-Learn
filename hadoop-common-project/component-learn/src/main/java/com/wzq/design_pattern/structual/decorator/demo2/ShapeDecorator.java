package com.wzq.design_pattern.structual.decorator.demo2;

/**
 * @author wzq
 * @create 2023-06-15 18:46
 */
public abstract class ShapeDecorator implements Shape{

    protected Shape wrapper;

    public ShapeDecorator(Shape wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void draw() {
        wrapper.draw();
    }
}
