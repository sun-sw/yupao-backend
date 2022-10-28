package com.sunsw.yupaobackend.service;

import com.sunsw.yupaobackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        User woke = new User();
        woke.setId(1L);
        woke.setUsername("woke");
        valueOperations.set("woke",woke);
        System.out.println(valueOperations.get("woke"));
    }
}
