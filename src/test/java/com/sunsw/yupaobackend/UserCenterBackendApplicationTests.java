package com.sunsw.yupaobackend;

import com.sunsw.yupaobackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserCenterBackendApplicationTests {
    @Resource
    UserService userService;
    @Test
    void contextLoads() {
    }
/*    @Test
    void userRegister() {
        String userAccount = "woke?";
        String password = "12345678";
        String checkPassword = "12345678";
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();

        long result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1,result);
    }*/
}
