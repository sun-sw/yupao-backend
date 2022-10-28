package com.sunsw.yupaobackend.service;

import com.sunsw.yupaobackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunsw.yupaobackend.model.domain.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 通过标签查找用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);
    /**
     * 更改用户
     * @param user
     * @param request
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);
}
