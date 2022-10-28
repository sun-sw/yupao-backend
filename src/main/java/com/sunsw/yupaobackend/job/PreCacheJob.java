package com.sunsw.yupaobackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunsw.yupaobackend.model.domain.User;
import com.sunsw.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    //每天执行，预热推荐用户
    @Scheduled(cron = "0 5/5 12 * * ?")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:preCacheJob:doCache:lock");
        //只有一个线程执行
        try {
            if ( lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                System.out.println("getLock: "+ Thread.currentThread().getId());
                for (Long id : mainUserList) {
                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), userQueryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s",id);
                    ValueOperations<String,Object> redisOperations = redisTemplate.opsForValue();
                    //写入缓存
                    try {
                        log.info("预热推荐用户,写入缓存");
                        redisOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //只会释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock: "+ Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
