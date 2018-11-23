package com.kvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DemoEurekaClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoEurekaClientApplication.class,args);
    }
}
