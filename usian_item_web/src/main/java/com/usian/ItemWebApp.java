package com.usian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication//springboot的启动器
@EnableDiscoveryClient//迪斯科ve瑞，标识eureka的客户端的启动类，如果不使用eureka也支持
@EnableFeignClients   //扫描feign接口
public class ItemWebApp {
    public static void main(String[] args) {
        SpringApplication.run(ItemWebApp.class,args);
    }
}
