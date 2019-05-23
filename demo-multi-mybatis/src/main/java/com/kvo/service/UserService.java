package com.kvo.service;

import com.kvo.mapper.UserMapper;
import com.kvo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-23 16:28
 **/
@Service("userService")
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional("transactionManager")
    public void insert(User user) throws Exception {
       Integer result = userMapper.insert(user);
        System.out.println(result);
//        throw new Exception("aaaaa");

    }

    public User select(Integer id){
        return userMapper.selectById(id);
    }
}
