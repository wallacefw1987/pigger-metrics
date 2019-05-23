package com.kvo.mapper;

import com.kvo.model.User;
import com.kvo.MyMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 20:38
 **/
public interface UserMapper extends MyMapper<User> {

    @Select("select id,name from tmp_user where id = #{id}")
    User selectById(Integer id);

    @Override
    @Insert("INSERT INTO `tmp_user`(`name`) VALUES (#{name})")
    int insert(User user);

//    Boolean addUser(User user);
}
