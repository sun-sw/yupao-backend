package com.sunsw.usercenterbackend.service;

import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
 /*   @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("woke");
        user.setUserAccount("woke");
        user.setAvatarUrl("123");
        user.setUserPassword("123");
        user.setPhone("156");
        user.setEmail("123");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }*/

}