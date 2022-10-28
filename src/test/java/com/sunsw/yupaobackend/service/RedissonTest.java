package com.sunsw.yupaobackend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {

    @Resource
    RedissonClient redissonClient;
    @Test
    public void test(){
        RMap<Object, Object> map = redissonClient.getMap("test-map");
        map.put("1","woke");
        System.out.println(map.get("1"));
    }
}
