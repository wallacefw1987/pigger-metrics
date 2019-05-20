package com.kvo.config;

import lombok.Data;

/**
 * @program: pigger-metrics
 * @description 该类主要用于记录当前线程使用的数据源，使用 ThreadLocal 进行记录数据
 * @author: fanwei
 * @create: 2019-05-19 12:25
 **/
public class DataBaseContextHolder {

    private static final ThreadLocal<DataBaseType> contextHolder = new ThreadLocal<>();

    public static DataBaseType getDataBaseType(){
        return contextHolder.get();
    }

    public static void setDataBaseType(DataBaseType dataBaseType){
        contextHolder.set(dataBaseType);
    }
}
