package com.bingo.business.ip;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.ip.bean.IpProxyModel;
import com.bingo.business.ip.repository.IpProxyRepository;
import com.bingo.business.ip.service.IpProxyService;
import com.bingo.business.taobao.crawler.TBProdCrawer;
import com.bingo.business.taobao.model.TBShopProd;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.common.String.StringExtract;
import com.bingo.common.thread.MyThreadPool;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-05-17.
 */
public class test {

    public static boolean checkIpProxy(IpProxyModel ipm) throws IOException {
        //System.out.println("start proxy:"+ipm.ip);
        String url = "http://2017.ip138.com/ic.asp";
        Connection conn = Jsoup.connect(url);
        //伪装http头
        Map<String,String> headers = new HashMap<>();

        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("HTTP_X_FORWARDED_FOR","123.4.196.31,123.4.196.32");

        //headers.put("referer","https://item.taobao.com/item.htm?id=38956758535");
        conn.proxy(ipm.ip,ipm.prot);
        conn.headers(headers);
        conn.timeout(1000*5);
        conn.followRedirects(true);
        //conn.postDataCharset("gbk");
        Connection.Response response = conn.execute();
        //response.charset("gbk");
        String body = response.body();
        System.out.println(body);
        String ip = body.substring(body.indexOf("[")+1,body.indexOf("]"));
        System.out.println("getip:"+ip+",proxyip:"+ipm.ip+",srcurl:"+ipm.src_url);


        return false;
    }

    public static boolean checkIpProxy2(IpProxyModel ipm) throws IOException {
        //System.out.println("start proxy:"+ipm.ip);
        String url = "https://ip.cn/";
        Connection conn = Jsoup.connect(url);
        //伪装http头
        Map<String,String> headers = new HashMap<>();

        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        //headers.put("HTTP_X_FORWARDED_FOR","123.4.196.31");

        //headers.put("referer","https://item.taobao.com/item.htm?id=38956758535");
        conn.proxy(ipm.ip,ipm.prot);
        conn.headers(headers);
        conn.timeout(1000*5);
        conn.followRedirects(true);
        //conn.postDataCharset("gbk");
        Connection.Response response = conn.execute();
        //response.charset("gbk");
        String body = response.body();
        StringExtract sr = new StringExtract(body);
        String ip = sr.extractString("IP：<code>","</code>");
        System.out.println("getip:"+ip+",proxyip:"+ipm.ip+":"+ipm.prot+","+ipm.protocol+",srcurl:"+ipm.src_url);


        return false;
    }




    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        IpProxyService ipProxyService = SpringUtil.getBean(IpProxyService.class);
        IpProxyModel vo = new IpProxyModel();
        vo.setPageSize(500);
        vo.setPageNo(1);
        vo.setTotalCount(10000);
        MyThreadPool pool = new MyThreadPool(10,10);
        while(true){
            List<IpProxyModel> shoplist =  ipProxyService.findPage(vo).getResult();
            if(shoplist==null || shoplist.size()<5){
                break;
            }
            for(IpProxyModel prod:shoplist){
                pool.execute(()->{
                    try{
                        checkIpProxy2(prod);
                    }catch(Exception ex){
                        //ex.printStackTrace();
                    }
                });
            }
        }
        System.out.println("end");
    }
}
