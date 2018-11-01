package com.kvo.service.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;


@Component
@Slf4j
public class DruidDBConfig {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(env.getProperty("mysql.dirver"));
        dataSource.setPassword(env.getProperty("mysql.password"));
        dataSource.setUrl(env.getProperty("mysql.url"));
        dataSource.setUsername(env.getProperty("mysql.username"));
        return dataSource;
    }
}
