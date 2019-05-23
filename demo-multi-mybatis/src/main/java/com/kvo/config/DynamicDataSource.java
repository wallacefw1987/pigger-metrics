package com.kvo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 18:59
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {

    static final Map<DataBaseType, List<String>> METHOD_TYPE_MAP = new HashMap<>();

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        DataBaseType type = DataBaseContextHolder.getDataBaseType();
        logger.info("====================dataSource ==========" + type);
        return type;
    }

    void setMethodType(DataBaseType type,String content){
        List<String> list = Arrays.asList(content.split(","));
        METHOD_TYPE_MAP.put(type,list);
    }
}
