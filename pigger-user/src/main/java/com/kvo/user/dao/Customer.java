package com.kvo.user.dao;

import com.kvo.utils.SnowFlakeIdGenerator;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "t_user")
public class Customer implements Serializable {

    private Long id;

    private String name;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-mm-dd HH:MM:SS")
    private Date createdTime;

    public Customer() {
        id = SnowFlakeIdGenerator.getDefaultNextId();
    }
}
