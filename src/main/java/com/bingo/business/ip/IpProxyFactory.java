package com.bingo.business.ip;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018-04-14.
 */
public class IpProxyFactory {
    //所有有效IP保存在在这个队列里
    private static LinkedBlockingQueue<IpProxyBean> ipPool = new LinkedBlockingQueue<IpProxyBean>();


    /**
     * 取出一个IP进行使用
     * @return
     */
    public static IpProxyBean getNextPorxyIp(){
        IpProxyBean ip = ipPool.poll();
        ipPool.add(ip);
        return ip;
    }



    public static void main(String args[]){


    }






}
