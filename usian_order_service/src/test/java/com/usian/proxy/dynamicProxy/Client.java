package com.usian.proxy.dynamicProxy;

import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args) {
        Star realStar = new RealStar();
        ProxyStar handler = new ProxyStar(realStar);
        //生成代理类的对象
        Star proxy = (Star)Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Star.class}, handler);
        proxy.sing();
    }
}
