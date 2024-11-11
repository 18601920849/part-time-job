package com.entnews;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.entnews.dao")
public class EntnewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntnewsApplication.class, args);
    }

}
