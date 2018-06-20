package com.bingo.business.ip;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.ip.bean.IpProxyModel;
import com.bingo.business.ip.service.IpProxyService;
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
public class test2 {

    public static boolean checkIpProxy(IpProxyModel ipm) throws IOException {
        //System.out.println("start proxy:"+ipm.ip);
        String url = "https://ip.cn/";
        Connection conn = Jsoup.connect(url);
        //伪装http头
        Map<String,String> headers = new HashMap<>();

        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        //headers.put("HTTP_X_FORWARDED_FOR","123.4.196.31,123.4.196.32");

        //headers.put("referer","https://item.taobao.com/item.htm?id=38956758535");
        if(ipm!=null){
            conn.proxy(ipm.ip,ipm.prot);
        }
        conn.headers(headers);
        conn.timeout(1000*5);
        conn.followRedirects(true);
        //conn.postDataCharset("gbk");
        Connection.Response response = conn.execute();
        //response.charset("gbk");
        String body = response.body();
        System.out.println(body);



        return false;
    }




    public static void main(String args[]) throws Exception {
        IpProxyModel imp  =new IpProxyModel();
        imp.ip = "109.197.184.49";
        imp.prot = 8080;
        checkIpProxy(imp);


        System.out.println("end");
    }
}
