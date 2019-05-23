package com.kvo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-22 11:18
 **/
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class DataSourceAspect {

    @Pointcut("execution(* com.kvo.mapper.*.* (..))")
    public void aspect(){
    }

    @Before("aspect()")
    public void before(JoinPoint point){
        String className = point.getTarget().getClass().getName();
        String method = point.getSignature().getName();
        String args = StringUtils.join(point.getArgs(),",");
        log.info("className:{}, method:{}, args:{} ", className, method, args);

        try {
            for (DataBaseType type:DataBaseType.values()){
                List<String> values = DynamicDataSource.METHOD_TYPE_MAP.get(type);
                for (String key : values){
                  if (method.startsWith(key)){
                      log.info(">>{} 方法使用的数据源为:{}<<", method, key);
                      DataBaseContextHolder.setDataBaseType(type);
                      DataBaseType types = DataBaseContextHolder.getDataBaseType();
                      log.info(">>{}方法使用的数据源为:{}<<", method, types);
                  }
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
