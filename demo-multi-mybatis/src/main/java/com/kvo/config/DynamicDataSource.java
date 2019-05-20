package com.kvo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 18:59
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        DataBaseType type = DataBaseContextHolder.getDataBaseType();
        logger.info("====================dataSource ==========" + type);
        return type;
    }
}
