package com.bingo.common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring mvc控制层的过滤器，注解
 * Created by Administrator on 2017/1/12.
 */
public abstract class ControllerType {
    public final static int LOGIN_TYPE_NONE=0;//不需要登录
    public final static int LOGIN_TYPE_LOGIN=1;//登陆过滤

    public final static int USER_TYPE_ALL=0;//所有用户
    public final static int USER_TYPE_ADMIN=1;//管理员

}
