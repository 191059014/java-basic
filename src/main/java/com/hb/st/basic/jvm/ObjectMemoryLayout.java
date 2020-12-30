package com.hb.st.basic.jvm;

import lombok.Data;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

/**
 * <p>
 * 对象内存布局：markword + 类指针信息 + 实例数据 + 对其填充
 * </p>
 * <p>
 * 其中，markword + 类指针 = 对象头
 * </p>
 * <p>
 * 开启指针压缩，类指针占4字节，-XX:-XX:+UseCompressedOops； 关闭指针压缩，类指针占8字节，-XX:-UseCompressedOops
 * </p>
 * <p>
 * 64位的JVM支持 -XX:+UseCompressedOops 来开启指针压缩功能 1.6 后默认开启
 * </p>
 *
 * @version v0.1, 2020/12/30 15:57, create by huangbiao.
 */
public class ObjectMemoryLayout {

    /**
     * 打印一个空对象（没有属性）在内存中的大小（16 bytes）
     */
    @Test
    public void test1() {
        EmptyModel emptyModel = new EmptyModel();
        System.out.println(ClassLayout.parseInstance(emptyModel).toPrintable());
    }

    /**
     * 打印包含各种数据类型的对象在内存中的大小
     */
    @Test
    public void test2() {
        Model model = new Model();
        System.out.println(ClassLayout.parseInstance(model).toPrintable());
    }

    /**
     * 打印包含各种数据类型的对象在内存中的大小
     */
    @Test
    public void test3() {
        Model model = new Model();
        model.setAge(21);
        model.setName("zhangsan");
        System.out.println(ClassLayout.parseInstance(model).toPrintable());
    }

    private class EmptyModel {

    }

    @Data
    private class Model {
        private boolean boo; // 1字节
        private byte byt; // 1字节
        private char cha; // 2字节
        private short sho; // 2字节
        private int age; // 4字节
        private float flo; // 4字节
        private double dou; // 8字节
        private long lon; // 8字节
        private String name; // 4字节
    }

}
