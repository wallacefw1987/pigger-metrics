package com.kvo.service;

import com.kvo.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-23 16:31
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void insert() throws Exception {
        User user = new User();
        user.setName("A小米");
        userService.insert(user);
    }

    @Test
    public void select() {
      User user = userService.select(1);
        System.out.println(user.toString());
    }
}