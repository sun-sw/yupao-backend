package com.sunsw.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 退出队伍request
 */

@Data
public class TeamQuitRequest implements Serializable {
    /**
     * id
     */
    private Long teamId;



    private static final long serialVersionUID = 1L;
}