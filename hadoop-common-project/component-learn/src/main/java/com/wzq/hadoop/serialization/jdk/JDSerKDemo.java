package com.wzq.hadoop.serialization.jdk;

import com.wzq.hadoop.util.PrintUtil;

import java.io.*;

/**
 * @author wzq
 * @create 2023-04-12 14:41
 */
public class JDSerKDemo {

    public static void printHex() {

    }

    public static void main(String[] args) {
        Block block = new Block(7806259420524417791L, 39447755L, 56736651L);

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileOutputStream fos = new FileOutputStream("demo.txt");
                ObjectOutputStream objOut = new ObjectOutputStream(baos)
        ) {
            objOut.writeObject(block);
            PrintUtil.printHex(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try (
//                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("demo.txt"))
//        ) {
//            Block b = (Block)ois.readObject();
//            System.out.println(b.toString());
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
