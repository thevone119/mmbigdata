package com.bingo.business.taobao.crawler;

import com.bingo.common.http.MyRequests;
import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.XJsonInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-04-13.
 */
public class TestCrawer {
    private static  PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private static long currTime = System.currentTimeMillis();
    public static void main(String[] args) throws Exception {
        List<String> list = org.apache.commons.io.FileUtils.readLines(new File("d:/prod_sib.txt"));
        MyThreadPool pool = new MyThreadPool(1,10);
        cm.setMaxTotal(300);// 整个连接池最大连接数
        cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        currTime = System.currentTimeMillis();
        int i=0;
        for (String url:list) {
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String now=dtf2.format(LocalDateTime.now());
            i=i+1;
            System.out.println(i+","+(System.currentTimeMillis()-currTime)+","+now);
            testJsoup(url);
            Thread.sleep(2000);
        }
        pool.shutdown();

    }

    private static void testJsoup( String url) throws IOException {
        //url = "http://47.106.70.111:9080/api/map/mapcfgarea/getHeadersInfo";
        //url = "https://item.taobao.com/item.htm?id=38956758536";
        //url = "https://item.taobao.com/item.htm?id=1115858210";
        //url = "https://item.taobao.com/item.htm?spm=a230r.1.14.146.7b6b32a08VPsXz&id=563803585152&ns=1&abbucket=14#detail";
        //url = "https://detailskip.taobao.com/service/getData/1/p1/item/detail/sib.htm?itemId=40561489243&sellerId=1663559802&modules=dynStock,viewer,price,duty,xmpPromotion,delivery,activity,fqg,zjys,soldQuantity,originalPrice";
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("referer","https://item.taobao.com/item.htm?id=38956758536");


        MyRequests req = new MyRequests();
        //req.proxyIp = "114.235.23.157";
        req.proxyPort = 9000;
        req.proxyType = "HTTP";
        req.hreads = headers;
        System.out.println(req.testProxy());
        XJsonInfo ret = req.get(url);
        String body = ret.getData().toString();
        System.out.println(body);
        //System.out.println("userTime =" + (System.currentTimeMillis() - currTime));
    }

    private static void testHttpPool() throws IOException {
        CloseableHttpResponse response = null;
        //HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //CloseableHttpClient httpClient = httpClientBuilder.build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm) // 目前暂时不使用连接池技术
                //.setConnectionManagerShared(true) // 如果启用连接池技术，请开启这个
                //.setRetryHandler(retryHandler)
                .build();
        HttpGet httpGet = new HttpGet("http://localhost/");
        httpGet.setHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("accept-encoding", "gzip, deflate, br");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("cache-control", "max-age=0");
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpGet.setHeader("referer", "https://item.taobao.com/item.htm?id=38956758535");
        try {
            //执行get请求
            response = httpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = response.getEntity();
            //响应状态
            //判断响应实体是否为空
            if (entity != null) {
                String body = EntityUtils.toString(entity, "utf-8");

                System.out.println("userTime =" + (System.currentTimeMillis() - currTime));
            }
            response.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
