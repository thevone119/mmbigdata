package com.bingo.common.filter;

/**
 * Created by Administrator on 2017/1/12.
 */
public enum AuthType {
    /**
     * 不用权限
     */
    NONE,
    /**
     * 系统登录用户
     */
    USER,
    /**
     * 接口授权用户
     */
    API,
    /**
     * 管理员
     */
    ADMIN
}
