package com.wzq.design_pattern.structual.decorator.demo1;

/**
 * 定义读取和写入操作的通用数据接口
 *
 * @author wzq
 * @create 2023-06-14 21:26
 */
public interface DataSource {

    void writeData(String data);

    String readData();

}
