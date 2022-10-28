package com.sunsw.yupaobackend.service;

import com.sunsw.yupaobackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;


    @Test
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
    }

    @Test
    void searchUsersByTags() {
        List<String> tagList = Arrays.asList("Java");
        List<User> users = userService.searchUsersByTags(tagList);
        Assertions.assertNotNull(users);
    }
}