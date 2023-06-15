package com.wzq.design_pattern.structual.decorator.demo2;

/**
 * @author wzq
 * @create 2023-06-15 18:46
 */
public class RedShapeDecorator extends ShapeDecorator{
    public RedShapeDecorator(Shape wrapper) {
        super(wrapper);
    }

    @Override
    public void draw() {
        wrapper.draw();
        setRedBorder(wrapper);
    }

    private void setRedBorder(Shape decoratedShape) {
        System.out.println("Border color: Red");
    }
}
