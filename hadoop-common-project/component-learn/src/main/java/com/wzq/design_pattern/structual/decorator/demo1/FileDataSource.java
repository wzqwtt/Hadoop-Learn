package com.wzq.design_pattern.structual.decorator.demo1;

import java.io.*;

/**
 * 简单数据读写器
 *
 * @author wzq
 * @create 2023-06-14 21:36
 */
public class FileDataSource implements DataSource {

    private String name;

    public FileDataSource(String name) {
        this.name = name;
    }

    @Override
    public void writeData(String data) {
        File file = new File(name);
        try (
                OutputStream fos = new FileOutputStream(file)
        ) {
            fos.write(data.getBytes(), 0, data.length());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String readData() {
        char[] buffer = null;
        File file = new File(name);

        try (FileReader reader = new FileReader(file)) {
            buffer = new char[(int) file.length()];
            reader.read(buffer);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return new String(buffer);
    }
}
