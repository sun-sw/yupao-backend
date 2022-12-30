package com.sunsw.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 加入队伍request
 */

@Data
public class TeamJoinRequest implements Serializable {
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;



    private static final long serialVersionUID = 1L;
}