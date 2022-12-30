package com.sunsw.yupaobackend.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -2267914929214482157L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;
    /**
     * 当前第几页
     */
    protected int pageNum = 1;
}
