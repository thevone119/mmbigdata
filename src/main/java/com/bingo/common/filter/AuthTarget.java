package com.bingo.common.filter;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/1/12.
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(AuthTargets.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthTarget {
    AuthType value() default AuthType.NONE;
}
