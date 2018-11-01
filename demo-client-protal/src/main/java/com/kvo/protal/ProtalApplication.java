package com.kvo.protal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author fanwei
 * @desc 参考 https://blog.csdn.net/zhongzunfa/article/details/79481011
 */
@EnableEurekaClient
@SpringBootApplication
public class ProtalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtalApplication.class,args);
    }
}
