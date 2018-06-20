package com.bingo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程配置类
 * Created by huangtw on 2018-04-06.
 */
@EnableAsync
@Configuration
public class AsyncTaskConfig {

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);//最小线程数
        executor.setMaxPoolSize(20);//最大线程数
        executor.setQueueCapacity(200);//队列的容量
        executor.setKeepAliveSeconds(60);//保存活动的时间，秒
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 用于特定的异步线程
     * 这里用于http抓包开启的线程数，开启1个线程进行数据抓包
     * @return
     */
    @Bean("taskHttpCrawer01")
    public Executor taskHttpCrawer01() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);//最小线程数
        executor.setMaxPoolSize(10);//最大线程数
        executor.setQueueCapacity(1000);//队列的容量
        executor.setKeepAliveSeconds(60);//保存活动的时间，秒
        executor.setThreadNamePrefix("taskHttpCrawer01-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 用于特定的异步线程
     * 这里用于http抓包开启的线程数，开启10个线程进行数据抓包
     * @return
     */
    @Bean("taskHttpCrawer10")
    public Executor taskHttpCrawer10() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);//最小线程数
        executor.setMaxPoolSize(20);//最大线程数
        executor.setQueueCapacity(1000);//队列的容量
        executor.setKeepAliveSeconds(60);//保存活动的时间，秒
        executor.setThreadNamePrefix("taskHttpCrawer10-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
