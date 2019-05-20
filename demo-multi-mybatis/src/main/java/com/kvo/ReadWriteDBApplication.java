package com.kvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.kvo.mapper")
public class ReadWriteDBApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ReadWriteDBApplication.class,args);
    }
}
