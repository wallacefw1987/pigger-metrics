package com.kvo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-23 21:13
 **/
@Configuration
@EnableTransactionManagement
@Slf4j
@AutoConfigureAfter({SpringJDBCDataSource.class})
public class TransactionConfiguration extends DataSourceTransactionManagerAutoConfiguration {

    @Bean("transactionManager")
    @Autowired
    public DataSourceTransactionManager transactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource){
        log.info("事物配置");
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
