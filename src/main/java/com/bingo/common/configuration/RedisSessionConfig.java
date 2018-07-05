package com.bingo.common.configuration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.lang.reflect.Method;

/**
 * 配置redisSession的超时时间(8小时),不使用这个配置，使用配置文件配置
 * Created by Administrator on 2018-06-25.
 */
@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60*60*8)
public class RedisSessionConfig  extends CachingConfigurerSupport {

    /**
     * 自定义key. 这个可以不用
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     */
    @Override
    public KeyGenerator keyGenerator() {
        // 这里的重写key的作用，是为@Cacheable(value="user-key")这种注解服务，加上这个注解就会自动把某个方法的返回值缓存起来
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                // This will generate a unique key of the class name, the method name
                //and all method parameters appended.
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                //System.out.println("keyGenerator=" + sb.toString());
                //return sb.toString();
                return "aboss-" + sb.toString();
            }
        };
    }
}
