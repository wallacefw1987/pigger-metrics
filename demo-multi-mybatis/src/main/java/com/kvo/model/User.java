package com.kvo.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @program: pigger-metrics
 * @description
 * @author: fanwei
 * @create: 2019-05-19 19:28
 **/
@Data
@Table(name = "tmp_user")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
}
