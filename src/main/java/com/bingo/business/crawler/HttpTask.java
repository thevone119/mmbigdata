package com.bingo.business.crawler;

import java.util.Map;

/**
 * http的数据爬取任务
 * Created by huangtw on 2018-04-17.
 */
public class HttpTask {
    public String name;//任务名称，同样名称的，做相关的限制处理
    public String url;//数据抓取的url
    public  int ipLimitTime = 0;//限制IP时间，0为不限制 单位为毫秒
    //请求的hread头(包含cookie)
    public Map<String,String> hreads;

    public long lastRunTime = 3;//最后运行时间，用于计算间隔

}
