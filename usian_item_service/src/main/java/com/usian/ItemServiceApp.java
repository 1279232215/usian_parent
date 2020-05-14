package com.usian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication//springboot的启动器
@EnableDiscoveryClient //标识这个类是eureka客户端的启动类
@MapperScan("com.usian.mapper")
public class ItemServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApp.class,args);
    }
}
