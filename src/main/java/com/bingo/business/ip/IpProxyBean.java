package com.bingo.business.ip;

import com.bingo.common.http.MyRequests;
import com.bingo.common.utility.XJsonInfo;
import org.jsoup.Jsoup;

/**
 *
 * http://www.xicidaili.com/
 * https://www.kuaidaili.com/ops/proxylist/1/
 * http://www.ip3366.net/fetch/
 * http://www.89ip.cn/tiqv.php?sxb=&tqsl=30&ports=&ktip=&xl=on&submit=%CC%E1++%C8%A1
 * http://www.ip3366.net/free/
 * http://www.89ip.cn/
 * https://proxy.mimvp.com/help.php
 *
 * 验证代理IP是否匿名，是否高匿
 * http://web.chacuo.net/netproxycheck
 * Created by Administrator on 2018-04-14.
        */
public class IpProxyBean {
    public String ip;
    public int port;
    public long lastCheckTime = 0;//最后校验可用的时间


    public boolean checkEnable(){
        lastCheckTime = System.currentTimeMillis();
        MyRequests req = new MyRequests();
        req.proxyIp = this.ip;
        req.proxyPort = port;
        XJsonInfo ret = req.get("https://www.baidu.com/");
        if(ret.getCode()==200){
            return true;
        }
        return false;
    }
}
