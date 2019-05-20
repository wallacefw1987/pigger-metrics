package com.kvo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-17 16:15
 **/
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
