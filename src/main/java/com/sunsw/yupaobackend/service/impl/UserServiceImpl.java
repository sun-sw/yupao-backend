package com.sunsw.yupaobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunsw.yupaobackend.common.ErrorCode;
import com.sunsw.yupaobackend.exception.BusinessException;
import com.sunsw.yupaobackend.model.domain.User;
import com.sunsw.yupaobackend.model.domain.request.UserRegisterRequest;
import com.sunsw.yupaobackend.service.UserService;
import com.sunsw.yupaobackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sunsw.yupaobackend.constant.UserConstant.ADMIN_ROLE;
import static com.sunsw.yupaobackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    /**
     * 盐值，密码加密混淆
     */
    private static final String SALT = "woke";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        //1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求数据为空");
        }
        //账户不能小于4位
        if(userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        //密码不小于8位
        if(userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账号不包含特殊字符
        String unvalidPattern = "[`~!@#$%^&*()_+|{}';:',\\\\[\\\\].<>/?~！@#￥%......&*（）——+|{}【】‘：；“”’，。、？]";
        Matcher matcher = Pattern.compile(unvalidPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        //账户不能重复
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("userAccount", userAccount));
        if(count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        //密码大于5位
        if(planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }
        //星球编号不能重复
        count = userMapper.selectCount(new QueryWrapper<User>().eq("planetCode", planetCode));
        if(count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }

        //2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean result = this.save(user);
        if(!result){

            throw new BusinessException(ErrorCode.NULL_ERROR,"注册用户插入数据库失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        //账户不能小于4位
        if(userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户过短");
        }
        //密码不小于8位
        if(userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账号不包含特殊字符
        String unvalidPattern = "[`~!@#$%^&*()_+|{}';:',\\\\[\\\\].<>/?~！@#￥%......&*（）——+|{}【】‘：；“”’，。、？]";
        Matcher matcher = Pattern.compile(unvalidPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户包含特殊字符");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        userQueryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        //用户不存在
        if(user == null){
            log.info("user login failed,userAccount cannot match userPassword!");
            throw new BusinessException(ErrorCode.NULL_ERROR,"登录用户不存在");
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登陆状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    public User getSafetyUser(User originUser){
        if(originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> result = searchByCache(tagNameList);
        return result;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        //管理员和自己可以修改,管理员可以修改任意用户信息
        long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果不是管理员或者本人，没有权限修改
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) o;
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    public boolean isAdmin(HttpServletRequest request){
        Object o = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) o;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 通过数据库
     * @param tagNameList
     * @return
     */
    private List<User> searchBySql(List<String> tagNameList) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        for (String tag:tagNameList) {
            queryWrapper = queryWrapper.like("tags",tag);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 在内存中筛选
     * @param tagNameList
     * @return
     */
    private List<User> searchByCache(List<String> tagNameList) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //先查询所有用户
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //判断每一个用户的标签是否包含要求的标签，不包含去除该用户

        return  userList.stream().filter(user -> {
            if(StringUtils.isBlank(user.getTags())){
                return false;
            }
            Set<String> tempTagSet = gson.fromJson(user.getTags(), new TypeToken<Set<String>>(){}.getType());
            tempTagSet = Optional.ofNullable(tempTagSet).orElse(new HashSet<>());
            for (String tagName: tagNameList) {
                if(!tempTagSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map( this::getSafetyUser).collect(Collectors.toList());

    }
}




