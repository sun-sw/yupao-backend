package com.sunsw.yupaobackend.model.vo;


import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息封装类
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 2351857324211723135L;
    /**
     * id
     */
    private long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;

    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 星球编号
     */
    private String planetCode;
    /**
     * 标签列表 json
     */
    private String tags;
    /**
     * 个人简介
     */
    private String profile;
}
