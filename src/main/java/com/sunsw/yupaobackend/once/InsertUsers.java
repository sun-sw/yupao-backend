package com.sunsw.yupaobackend.once;

import com.sunsw.yupaobackend.mapper.UserMapper;
import com.sunsw.yupaobackend.model.domain.User;
import com.sunsw.yupaobackend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    //@Scheduled(cron = "0 59 16 * * ?")
    public void doInsert(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("章北海");
            user.setUserAccount("zhangbeihai");
            user.setAvatarUrl("https://img.aliyundrive.com/avatar/a19c0b2f8d32476c9ec17ce12605b56b.jpeg");
            user.setGender((byte) 0);
            user.setUserPassword("12345678");
            user.setPhone("123123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111");
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println("总时间：" + stopWatch.getTotalTimeMillis());
    }
}
