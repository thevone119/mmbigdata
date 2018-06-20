package com.bingo.common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/1/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AuthPower {
    String[] powers() default {};
    String[] roles() default {};
    boolean powersIsOR() default true; // powers里面的所有值是否是or关系
    boolean rolesIsOR() default true; // roles里面的所有值是否是or关系
    boolean powersAndRolesIsOR() default true; // powers和roles的关系是否是or关系
}
