package com.sunsw.usercenterbackend.service;

import com.sunsw.usercenterbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunsw.usercenterbackend.model.domain.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 * @author woke
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @return 新用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @return User 脱敏后的用户
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
