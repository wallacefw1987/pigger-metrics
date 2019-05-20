package com.kvo.mapper;

import com.kvo.ReadWriteDBApplication;
import com.kvo.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 21:14
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReadWriteDBApplication.class)
@WebAppConfiguration
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect(){
        User user = userMapper.selectById(1);
        System.out.println(user.toString());
    }
}