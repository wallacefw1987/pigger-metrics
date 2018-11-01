package com.kvo.registy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author fanwei
 * @desc eureka - server 配置
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistyApplication.class,args);
    }
}
