package com.kvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-04-11 22:28
 **/
@SpringBootApplication
@EnableScheduling
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class,args);
    }
}
