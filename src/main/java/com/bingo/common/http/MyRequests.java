package com.bingo.common.http;

import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.XJsonInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Administrator on 2018-04-15.
 */
public class MyRequests {
    //请求的代理
    public String proxyIp;
    public int proxyPort;
    public String proxyType = "http";

    //请求的hread头
    public Map<String,String> hreads;

    //字符编码
    public String defaultCharset = "utf-8";




    private static PoolingHttpClientConnectionManager cm;


    private static synchronized void init(){
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(300);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        }
    }

    /**
     *
     * @param url
     * @return 200成功
     */
    public XJsonInfo get(String url) {
        url=url+"&_"+System.currentTimeMillis();
        if(proxyIp!=null){
            return getByProxy(url);
        }
        XJsonInfo ret = new XJsonInfo();
        //伪装http头
        Map<String,String> hs = new HashMap<>();
        hs.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        hs.put("accept-encoding","gzip, deflate, br");
        hs.put("cache-control","max-age=0");
        hs.put("upgrade-insecure-requests","1");
        hs.put("user-agent",UserAgent.getRandomUserAgent());
        hs.put("accept-language","zh-CN,zh;q=0.9");
        if(hreads!=null){
            hs.putAll(hreads);
        }
        Connection conn = Jsoup.connect(url);
        conn.headers(hs);
        conn.timeout(1000*20);
        conn.followRedirects(true);
        try {
            Connection.Response response = conn.execute();
            if(response.statusCode()==200){
                ret.setData(response.body());
            }else{
                ret.setCode(response.statusCode());
            }
        } catch (IOException e) {
            ret.setCode(-1);
            e.printStackTrace();
        }
        return ret;
    }

    private XJsonInfo getByProxy(String url){
        XJsonInfo ret = new XJsonInfo();
        CloseableHttpResponse resp = null;
        try{
            //设置代理
            HttpGet httpGet = new HttpGet(url);
            HttpHost proxyHost = new HttpHost(proxyIp, proxyPort, proxyType);
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).setConnectTimeout(3000) // 创建连接的最长时间
                    .setConnectionRequestTimeout(1000)  // 从连接池中获取到连接的最长时间
                    .setSocketTimeout(4000)  // 数据传输的最长时间
                    .build();

            httpGet.setConfig(config);
            //伪装http头
            Map<String,String> hs = new HashMap<>();
            hs.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            hs.put("accept-encoding","gzip, deflate, br");
            hs.put("cache-control","max-age=0");
            hs.put("upgrade-insecure-requests","1");
            hs.put("user-agent",UserAgent.getUserAgent(0));
            hs.put("accept-language","zh-CN,zh;q=0.9");
            if(hreads!=null){
                hs.putAll(hreads);
            }
            for (Map.Entry<String, String> param : hs.entrySet()) {
                httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm) // 目前暂时不使用连接池技术
                    //.setConnectionManagerShared(true) // 如果启用连接池技术，请开启这个
                    //.setRetryHandler(retryHandler)
                    .build();
            resp = httpClient.execute(httpGet);

            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                ret.setCode(200);
                ret.setData(EntityUtils.toString(entity, defaultCharset));
            }
            resp.close();
        }catch (Exception e){
            ret.setCode(-1);
            e.printStackTrace();
        }finally {
            try {
                if(resp!=null){
                    resp.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 发送httppost请求
     * @param url
     * @param postData
     * @return
     * @throws IOException
     */
    public String httpPost2(String url,Map<String,String> postData) throws IOException {
        String retstr = null;
        HttpPost httpPost = new HttpPost(url);

        if(postData!=null){
            ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : postData.entrySet()) {
                pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, defaultCharset));
        }

        CloseableHttpResponse resp=null;
        try{
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm) // 目前暂时不使用连接池技术
                    //.setConnectionManagerShared(true) // 如果启用连接池技术，请开启这个
                    //.setRetryHandler(retryHandler)
                    .build();
            resp = httpClient.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            if (resp.getStatusLine().getStatusCode() == 200) {
                if (entity != null) {
                    retstr = EntityUtils.toString(entity, defaultCharset);
                }
            }
            resp.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(resp!=null){
                resp.close();
            }
        }

        return retstr;
    }

    /**
     * 通过telnet测试代理IP和端口
     * @return
     */
    public boolean testProxy(){
        Socket server = null;
        try {
            server = new Socket();
            InetSocketAddress address = new InetSocketAddress(proxyIp,proxyPort);
            server.connect(address, 2000);
            return true;
        }  catch (Exception e) {
            System.out.println("telnet失败");
        }finally{
            if(server!=null)
                try {
                    server.close();
                } catch (IOException e) {
                }
        }
        return false;
    }

    public static void main(String args[]) throws IOException {
        MyRequests req = new MyRequests();
        //req.proxyIp = "107.183.211.173";
        //req.proxyPort = 1080;
        //req.proxyType = "HTTP";
        System.out.println(req.httpPost2("http://localhost:8090/payapi/create2",null));


    }




}
