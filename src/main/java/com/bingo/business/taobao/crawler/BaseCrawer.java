package com.bingo.business.taobao.crawler;

import com.bingo.business.taobao.util.TBJsessionId;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础类，封装一些公共的方法，方便调用
 */
public class BaseCrawer {
    private boolean isProxy = false;


    protected String httpGet(String url,int cookieType,String cooike) throws IOException {
        Connection conn = Jsoup.connect(url);
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("accept-language","zh-CN,zh;q=0.9");

        if(cooike!=null){
            headers.put("cookie",cooike);
        }else{
            if(cookieType==1){
                headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; _tb_token_=75e51765b736a; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+ TBJsessionId.getJsessionId());
            }
            if(cookieType==2){
                headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; _tb_token_=75e51765b736a; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+ TBJsessionId.getCurrIeCooike());
            //headers.put("cookie",TBJsessionId.getCurrIeCooike());
            }
            if(cookieType==3){
                headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+ TBJsessionId.getNextIeCooike("d:/mycooike"));
                //headers.put("cookie",TBJsessionId.getCurrIeCooike());
            }
        }
        conn.headers(headers);
        conn.timeout(1000*20);
        conn.followRedirects(true);
        Connection.Response response = conn.execute();
        String body = response.body();
        return body;
    }
    /**
     *
     * @param url
     * @param cookieType 0不要cookie  1:随机cookie  2.动态IE cookie
     * @return
     * @throws IOException
     */
    protected String httpGet(String url,int cookieType) throws IOException {
        return httpGet(url,cookieType,null);
    }

    protected String httpGet(String url) throws IOException {
        return httpGet(url,0);
    }

    protected Integer parseInt(String str){
        if(str==null){
            return null;
        }
        try{
            return new Integer(str);
        }catch (Exception e){

        }
        return null;
    }

    protected Long parseLong(String str){
        if(str==null){
            return null;
        }
        try{
            return new Long(str);
        }catch (Exception e){

        }
        return null;
    }

    protected Float parseFloat(String str){
        if(str==null){
            return null;
        }
        try{
            return new Float(str);
        }catch (Exception e){

        }
        return null;
    }

    protected String trim(String src){
        if(src==null){
            return null;
        }
        src = src.trim();
        if(src.indexOf("&nbsp;")!=-1){
            src = src.replace("&nbsp;","");
        }
        if(src.equals("")){
            return null;
        }
        return src;
    }

    public static void main(String args[]) throws Exception {
        String url = "http://d2.sku117.info/htm_data/5/1806/1192753.html";
        Connection conn = Jsoup.connect(url);
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        //headers.put("cookie","enc=QeWum2aF4OWAX4gB5weFh4LIMdtJIwRZ1NHTeuJeIs3LBcHYfBiDP5dfGDGRZF%2BCApiv223njyGQ%2BUZPKnVKhw%3D%3D; t=828187c2c4955dbdb78727eaeab21ae8; cookie2=3c39286e9ca8f5c5594fd40479f597ba; v=0; _tb_token_=3f98613e731e3; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; hng=CN%7Czh-CN%7CCNY%7C156; thw=cn; mt=ci%3D-1_1; isg=BGlpQX_4F1eXjivhHJ0qhvkoeBUDnlwPaIdmhQte8dCP0onkU4ZtOFfDkHZkzPWg");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        //headers.put("referer","https://item.taobao.com/item.htm?id=38956758535");

        conn.headers(headers);
        conn.timeout(1000*20);
        conn.validateTLSCertificates(false);
        conn.followRedirects(true);
        Connection.Response response = conn.execute();
        String body = response.body();
        System.out.println(body);

    }




}
