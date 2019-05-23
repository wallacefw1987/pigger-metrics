package com.kvo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 13:10
 **/
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {


    /**
     * 通过Spring JDBC 快速创建 DataSource
     * 参数格式
     * spring.datasource.master.jdbcurl=jdbc:mysql://localhost:3306/charles_blog
     * spring.datasource.master.username=root
     * spring.datasource.master.password=root
     * spring.datasource.master.driver-class-name=com.mysql.jdbc.Driver
     *
     * @return DataSource
     */
    @Bean
    @Qualifier("masterDataSource")
    public DataSource masterDataSource(){
        return new SpringJDBCDataSource().master();
    }
}
