package com.sunsw.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunsw.yupaobackend.common.BaseResponse;
import com.sunsw.yupaobackend.common.ErrorCode;
import com.sunsw.yupaobackend.common.ResultUtil;
import com.sunsw.yupaobackend.exception.BusinessException;
import com.sunsw.yupaobackend.model.domain.User;
import com.sunsw.yupaobackend.model.request.UserLoginRequest;
import com.sunsw.yupaobackend.model.request.UserRegisterRequest;
import com.sunsw.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sunsw.yupaobackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
//@CrossOrigin(origins = { "http://localhost:3000" },methods = {RequestMethod.POST,RequestMethod.GET})
//前端axios设置axios.defaults.withCredentials = true;//表示向后端发送请求时携带请求的凭证
//@CrossOrigin无效，通过拦截器中设置response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;
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
        if(!userService.isAdmin(request)){
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
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request) {
        //如果缓存中存在，直接获取
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        ValueOperations<String,Object> redisOperations = redisTemplate.opsForValue();
        Page<User> userPage= (Page<User>)redisOperations.get(redisKey);
        if (userPage != null){
            log.info("缓存存在");
            return ResultUtil.success(userPage);
        }
        //缓存中不存在，查询数据库
       /* List<User> userList = userService.list(userQueryWrapper);
        if(userList == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());*/
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
       userPage = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
       //写入缓存
        try {
            log.info("写入缓存");
            redisOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
        return ResultUtil.success(userPage);
    }
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagNameList);
        return ResultUtil.success(users);
    }
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //校验非空
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        int result = userService.updateUser(user, loginUser);
        return ResultUtil.success(result);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        //仅管理员可删除
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }


}
