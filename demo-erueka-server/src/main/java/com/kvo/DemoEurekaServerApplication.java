package com.kvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by Administrator on 2018/11/12.
 */
@SpringBootApplication
@EnableEurekaServer
public class DemoEurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoEurekaServerApplication.class,args);
    }
}
