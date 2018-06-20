package com.bingo.business.ip.crawler;

/**
 * Created by Administrator on 2018-04-15.
 */
public class BaseIpCrawer {
    public String cUrl;//抓取的URL;
    public int intervalTime = 20;//多久抓取一次，秒
    public long lastCrawerTime = 0;//最后抓取时间，上次抓取的时间

    public int reTryTime = 1;//失败重抓次数

    public int crawerTime = 1;//重复抓取次数

    public boolean isProxyPool = false;//是否采用IP代理池进行抓取

    public short cookieType = 0;// 采用的cookie类型  0：不用cookie  1:isg   2:cookie池

}
