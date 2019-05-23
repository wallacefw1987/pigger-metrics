package com.kvo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-18 23:10
 **/
@Configuration
@MapperScan(value = "com.kvo.mapper",sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class SpringJDBCDataSource {

    @Autowired
    private DataSourceProperties properties;
    @Autowired
    private Environment env;

    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    public DataSource master(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.master.jdbcurl"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.master.driver-class-name"));
        dataSource.setUsername(env.getProperty("spring.datasource.master.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.master.password"));
        return dataSource;
    }


    /**
     * 手动创建DruidDataSource,通过DataSourceProperties 读取配置
     * 参数格式
     * spring.datasource.url=jdbc:mysql://localhost:3306/charles_blog
     * spring.datasource.username=root
     * spring.datasource.password=root
     * spring.datasource.driver-class-name=com.mysql.jdbc.Driver
     *
     * @return DataSource
     * @throws SQLException
     */
    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    public DataSource slave() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(100);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setFilters("stat,wall");
        return dataSource;
    }

    /**
     *  构造多数据源连接池
     *  Master 数据源连接池采用 HikariDataSource
     *  Slave  数据源连接池采用 DruidDataSource
     * @param master
     * @param slave
     * @return
     */
    @Bean(name = "dynamicDataSource")
    @Qualifier("dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                        @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataBaseType.MASTER, masterDataSource);
        targetDataSources.put(DataBaseType.SLAVE, slaveDataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        // 该方法是AbstractRoutingDataSource的方法
        dataSource.setTargetDataSources(targetDataSources);
        // 默认的datasource设置为myTestDbDataSourcereturn dataSource;
        dataSource.setDefaultTargetDataSource(masterDataSource);

        String read = env.getProperty("spring.datasource.read");
        dataSource.setMethodType(DataBaseType.SLAVE, read);

        String write = env.getProperty("spring.datasource.write");
        dataSource.setMethodType(DataBaseType.MASTER, write);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource myTestDbDataSource,
                                               @Qualifier("slaveDataSource") DataSource myTestDb2DataSource) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dynamicDataSource(master(),slave()));
        fb.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapper-locations")));
        return fb.getObject();
    }

//    //添加事务
//    @Bean("dynamTransactionManager")
//    public DataSourceTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dynamicDataSource){
//        return new DataSourceTransactionManager(dynamicDataSource);
//    }
}
