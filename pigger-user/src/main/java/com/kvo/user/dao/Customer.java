package com.kvo.user.dao;

import com.kvo.utils.SnowFlakeIdGenerator;
import lombok.Data;
import java.io.Serializable;

@Data
public class Customer implements Serializable {

    private Long id;

    private String name;

    private String phone;

    public Customer() {
        id = SnowFlakeIdGenerator.getDefaultNextId();
    }
}
