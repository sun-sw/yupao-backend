package com.sunsw.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sunsw.usercenterbackend.common.BaseResponse;
import com.sunsw.usercenterbackend.common.ErrorCode;
import com.sunsw.usercenterbackend.common.ResultUtil;
import com.sunsw.usercenterbackend.exception.BusinessException;
import com.sunsw.usercenterbackend.model.domain.User;
import com.sunsw.usercenterbackend.model.domain.request.UserLoginRequest;
import com.sunsw.usercenterbackend.model.domain.request.UserRegisterRequest;
import com.sunsw.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.sunsw.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.sunsw.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            //return ResultUtil.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码或确认密码或星球编号为空");
        }
        long id = userService.userRegister(userRegisterRequest);
        return ResultUtil.success(id);
    }

    /**
     * 用户登录
     * @param userLoginRequest 登录请求体
     * @param request 请求
     * @return 用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin( @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户或密码为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }

    /**
     * 用户注销
     * @param request 请求
     * @return 用户信息
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        int result = userService.userLogout(request);
        return ResultUtil.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户没有登陆");
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        return ResultUtil.success(userService.getSafetyUser(user));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //仅管理员可查询
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            userQueryWrapper.like("username",username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        if(userList == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());

        return ResultUtil.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        //仅管理员可删除
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }

    /**
     * 判断是否为管理员
     * @param request 请求
     * @return 是否
     */
    public boolean isAdmin(HttpServletRequest request){
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) o;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
