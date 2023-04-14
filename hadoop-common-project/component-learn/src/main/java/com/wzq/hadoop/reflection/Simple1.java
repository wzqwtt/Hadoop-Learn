package com.wzq.hadoop.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author wzq
 * @create 2023-04-14 14:21
 */
public class Simple1 {

    public static void main(String[] args) throws Exception {
        Class<?> clz = Class.forName("com.wzq.hadoop.reflection.A");
        Constructor<?> constructor = clz.getConstructor();
        A a = (A) constructor.newInstance();

        Method add = clz.getMethod("add", int.class, int.class);
        Object invoke = add.invoke(a, 1, 2);
        System.out.println((Integer) invoke);
    }

    private static void demo5() {
        Class<A> clz = A.class;
        Field[] fields = clz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            System.out.println("name = " + field.getName());
            System.out.println("type = " + field.getType());

            System.out.println("Modifiers = " + Modifier.toString(field.getModifiers()));
        }
    }

    private static void demo4() throws ClassNotFoundException {
        Class<?> clz = Class.forName("com.wzq.hadoop.reflection.A");
        Constructor<?>[] constructors = clz.getConstructors();

        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> con = constructors[i];
            System.out.println("name = " + con.getName());
            System.out.println("decl class = " + con.getDeclaringClass());

            Class<?>[] parameterTypes = con.getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                System.out.println("\tparam #" + j + " = " + parameterTypes[j]);
            }

            Class<?>[] exceptionTypes = con.getExceptionTypes();
            for (int j = 0; j < exceptionTypes.length; j++) {
                System.out.println("\tparam #" + j + " = " + exceptionTypes[j]);
            }
            System.out.println("-------");
        }
    }

    private static void demo3() throws ClassNotFoundException {
        Class<?> clz = Class.forName("com.wzq.hadoop.reflection.Simple1");
        Method[] listMethods = clz.getDeclaredMethods();

        for (int i = 0; i < listMethods.length; i++) {
            Method method = listMethods[i];
            System.out.println("name = " + method.getName());
            System.out.println("decl class = " + method.getDeclaringClass());

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                System.out.println("param #" + j + " = " + parameterTypes[j]);
            }

            Class<?>[] exceptionTypes = method.getExceptionTypes();
            for (int j = 0; j < exceptionTypes.length; j++) {
                System.out.println("Exception #" + j + " = " + exceptionTypes[j]);
            }

            System.out.println("Returen type = " + method.getReturnType());

            System.out.println("-----");
        }
    }

    private static void demo2() throws ClassNotFoundException {
        Class<?> c = Class.forName("com.wzq.hadoop.reflection.A");
        boolean b1 = c.isInstance(new Integer(3));
        System.out.println(b1);
        boolean b2 = c.isInstance(new A());
        System.out.println(b2);
    }

    private static void demo1() throws ClassNotFoundException {
        Class<?> c = Class.forName("java.util.Stack");
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method);
        }
    }
}

class A {

    private int a;
    public int b;
    protected int c;

    public A() {

    }

    public A(int a, int b) {

    }

    public int add(int a, int b) {
        return a + b;
    }
}