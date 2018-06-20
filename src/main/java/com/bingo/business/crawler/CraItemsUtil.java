package com.bingo.business.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 爬虫的核心引起，把其他的模块统一通过此引擎进行调度处理
 *
 * Created by huangtw on 2018-04-17.
 */

public class CraItemsUtil {

    //存储数据对象队列
    private static String ITEM_LIST_KEY = "CRA_ITEM_LIST_KEY";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 把一个存储对象压入队列末尾，等待处理
     */
    public void AddItem(Object item){
        redisTemplate.opsForList().rightPush(ITEM_LIST_KEY, item);
    }


    /**
     * 把一个任务压入队列头部，等待处理
     * @param task
     */
    public void AddTaskLeft(HttpTask task){
        redisTemplate.opsForList().leftPush(ITEM_LIST_KEY, task);
    }



    /**
     * 队列中取出一个任务
     * @return
     */
    public Object PopTask(){
        return (Object)redisTemplate.opsForList().leftPop(ITEM_LIST_KEY);
    }

    /**
     * 返回当前的任务数
     * @return
     */
    public long getTaskSize(){
        return redisTemplate.opsForList().size(ITEM_LIST_KEY);
    }












}
