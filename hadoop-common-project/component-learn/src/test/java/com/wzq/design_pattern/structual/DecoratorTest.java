package com.wzq.design_pattern.structual;

import com.wzq.design_pattern.structual.decorator.demo1.CompressionDecorator;
import com.wzq.design_pattern.structual.decorator.demo1.DataSourceDecorator;
import com.wzq.design_pattern.structual.decorator.demo1.EncryptionDecorator;
import com.wzq.design_pattern.structual.decorator.demo1.FileDataSource;
import com.wzq.design_pattern.structual.decorator.demo2.Circle;
import com.wzq.design_pattern.structual.decorator.demo2.Rectangle;
import com.wzq.design_pattern.structual.decorator.demo2.RedShapeDecorator;
import com.wzq.design_pattern.structual.decorator.demo2.Shape;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 装饰器测试类
 *
 * @author wzq
 * @create 2023-06-14 21:59
 */
public class DecoratorTest {

    private static final Logger LOG = LoggerFactory.getLogger(DecoratorTest.class);

    @Test
    public void testDemo1() {
        String salaryRecords = "Name,Salary\tJohn Smith,100000\tSteven Jobs,912000";
        DataSourceDecorator decorator =
                new CompressionDecorator(
                        new EncryptionDecorator(
                                new FileDataSource("OutputDemo.txt")));

        decorator.writeData(salaryRecords);

        FileDataSource plain = new FileDataSource("OutputDemo.txt");

        LOG.info("- Input ------------------");
        LOG.info(salaryRecords);

        LOG.info("- Encoded ------------------");
        LOG.info(plain.readData());

        LOG.info("- Decoded ------------------");
        LOG.info(decorator.readData());
    }

    @Test
    public void testDemo2() {
        Shape circle = new Circle();
        Shape redCircle = new RedShapeDecorator(new Circle());
        Shape redRectangle = new RedShapeDecorator(new Rectangle());

        LOG.info("Circle with normal border");
        circle.draw();

        LOG.info("Circle of red border");
        redCircle.draw();

        LOG.info("Rectangle of red border");
        redRectangle.draw();
    }

}
