package com.usian.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/*
 * InvocationHandler：通过invoke方法调用真实角色
 * 好处：
 *      1、代理类可以代理任意的对象
 *      2、代理类没有真是类的重复代码
 * */
public class ProxyStar implements InvocationHandler {
    //真实角色
    private Object realStar;

    public ProxyStar(Object object){
        this.realStar=object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //mianTan();qianHeTong();yuFuKuan();dingJiPiao();
        Object result = method.invoke(realStar, args);//反
        // 射调用真实角色的方法
        return null;
    }

    public void mianTan(){
        System.out.println("ProxyStar.mianTa == 面谈");
    }
    public void qianHeTong(){
        System.out.println("ProxyStar.mianTa == 签合同");
    }
    public void yuFuKuan(){
        System.out.println("ProxyStar.mianTa == 预付款");
    }
    public void dingJiPiao(){
        System.out.println("ProxyStar.mianTa == 订机票");
    }
}
