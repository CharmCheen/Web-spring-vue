package com.example.exp2.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String birthdate;
    private String address;
    private String postcode;
    private String avatar;
    private LocalDateTime createdAt; // 映射为 LocalDateTime
    
    // 省市区（使用字段存储省市区名称）
    private String province;
    private String city;
    private String district;
}
