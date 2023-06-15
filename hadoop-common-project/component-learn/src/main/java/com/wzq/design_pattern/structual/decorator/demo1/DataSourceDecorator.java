package com.wzq.design_pattern.structual.decorator.demo1;

/**
 * 抽象基础装饰
 *
 * @author wzq
 * @create 2023-06-14 21:40
 */
public class DataSourceDecorator implements DataSource {

    private DataSource wrapper;

    public DataSourceDecorator(DataSource wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void writeData(String data) {
        wrapper.writeData(data);
    }

    @Override
    public String readData() {
        return wrapper.readData();
    }
}
