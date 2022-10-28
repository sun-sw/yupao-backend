package com.sunsw.yupaobackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.sunsw.yupaobackend.mapper")
@EnableScheduling
public class YupaoBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(YupaoBackEndApplication.class, args);
    }

}
