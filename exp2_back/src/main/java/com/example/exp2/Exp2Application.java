package com.example.exp2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.exp2.mapper") // 一定要加上这一行
public class Exp2Application {
    public static void main(String[] args) {
        SpringApplication.run(Exp2Application.class, args);
    }
}