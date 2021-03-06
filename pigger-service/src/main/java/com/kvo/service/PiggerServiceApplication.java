package com.kvo.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 这里主要系统操作类
 */
@EnableEurekaClient
@SpringBootApplication
public class PiggerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiggerServiceApplication.class,args);
    }
}
