package com.bingo.common.utility;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018-09-06.
 * 随机数函数
 * 1.生成定长的随机字符串
 * 2.生成定长的随机数字
 */
public class RandomUtils {
    private  static AtomicInteger currIdx =  new AtomicInteger(0);


    /**
     * 去指定长度的随机字符串
     * 采用了UUID+随机函数+内存自增3重保障绝对的随机
     * @param size
     * @return
     */
    public static String getRandomString(int size){
        String uuid = UUID.randomUUID().toString().replace("-", "")+UUID.randomUUID().toString().replace("-", "");
        Random random = new Random();
        int r = random.nextInt(uuid.length());
        int n = currIdx.getAndIncrement();
        if(n>100000){
            currIdx.set(0);
        }
        int start = (r+n)%(uuid.length()-size);
        return uuid.substring(start,start+size);
    }



    public static void main(String args[]){
        for(int i=0;i<10000;i++){
            System.out.println(getRandomString(16));
        }
    }
}
