package com.yunduan.common.utils;


import lombok.Data;

import java.io.Serializable;

/**
 * 前后端交互数据标准
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 消息
     */
    private String msg;

    /**
     * 返回代码
     */
    private Integer status;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    private T result;

    /**
     * 加密串
     */
    private String data;

    /**
     * 是否加密
     */
    private int isEncrypt = 0;
}
