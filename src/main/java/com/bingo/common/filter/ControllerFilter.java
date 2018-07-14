package com.bingo.common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring mvc控制层的过滤器，注解
 * Created by Administrator on 2017/1/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ControllerFilter {
    //角色编码，如果有配置，则登录用户的角色编码必须是此编码才可以访问
    public String[] roles() default {};

    //登录类型，0：所有，1：已登录
    public int LoginType() default 0;

    //用户类型：0：所有  1：管理员
    public int UserType() default 0;

}
