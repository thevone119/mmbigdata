package com.bingo.business.taobao.crawler;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.model.TBUser;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.business.taobao.service.TBShopService;
import com.bingo.business.taobao.service.TBUserService;
import com.bingo.business.taobao.util.TBJsessionId;
import com.bingo.common.String.StringExtract;
import com.bingo.common.String.StringReplace;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝的卖家主页爬虫
 *
 https://rate.taobao.com/user-rate-UvCIbOF8WMCgGMNTT.htm
 *
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBUserRateCrawer extends BaseCrawer{

    private static Map<String,String> cookie = null;
    //商家ID记录，对于重复的商家不处理
    private static Map<Long,Boolean> shopidMap = new HashMap<>();



    //因为开启多线程进行处理，数据库对象无法注入，通过springUtil获取
    private static TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);


    //处理userRateUrl页面解析
    public void userRateUrlAction(TBShop shop) throws Exception {
        //处理userRateUrl页面数据
        String userRateUrl = "https:"+shop.getUserRateUrl();
        Connection conn = Jsoup.connect(userRateUrl);
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        conn.timeout(1000*20);
        conn.followRedirects(true);
        if(cookie!=null){
            conn.cookies(cookie);
        }

        Connection.Response response = conn.execute();
        String userRateBody = response.body();
        cookie = response.cookies();
        if(userRateBody!=null & userRateBody.indexOf("卖家信用")==-1){
            System.out.println("数据抓取失败等待1秒---");

            return;
        }

        StringExtract usersr = new StringExtract(userRateBody);
        //地区提取
        String shopArea = usersr.extractLine("<li>所在地区");
        shopArea = trim(new StringExtract(shopArea).extractString("所在地区：","</li>"));
        shop.setShopArea(shopArea);

        //主营类目提取方式1 <span id="chart-name" class="data">食品/保健</span>
        String mainCname = usersr.extractLine("<span id=\"chart-name\" class=\"data\">");
        mainCname =  new StringExtract(mainCname).extractString("class=\"data\">","</span>");
        //主营类目提取方式2 <li>当前主营：<a href="//service.taobao.com/support/knowledge-1119280.htm" target="_blank">食品/保健</a></li>
        if(mainCname==null){
            mainCname = usersr.extractLine("<li>当前主营");
            mainCname =  new StringExtract(mainCname).extractString("target=\"_blank\">","</a>");
        }
        trim(mainCname);
        shop.setMainCname(mainCname);

        //近半年评分人数 //共<span>5056965</span>人
        String commentCountStr = usersr.extractLine("共<span>","</span>人");
        commentCountStr = new StringExtract(commentCountStr).extractString("<span>","</span>");
        shop.setCommentCount(this.parseInt(commentCountStr));


        //宝贝与描述相符评分
        String itemScoreStr = usersr.extractLine(userRateBody.indexOf("宝贝与描述相符"),"<em title","class=\"count\"");
        itemScoreStr = new StringExtract(itemScoreStr).extractString("class=\"count\">","</em>");
        shop.setItemScore(this.parseFloat(itemScoreStr));

        //卖家的服务态度
        String serviceScoreStr = usersr.extractLine(userRateBody.indexOf("卖家的服务态度"),"<em title","class=\"count\"");
        serviceScoreStr = new StringExtract(serviceScoreStr).extractString("class=\"count\">","</em>");
        shop.setServiceScore(this.parseFloat(serviceScoreStr));

        //卖家的服务态度
        String deliveryScoreStr = usersr.extractLine(userRateBody.indexOf("物流服务的质量"),"<em title","class=\"count\"");
        deliveryScoreStr = new StringExtract(deliveryScoreStr).extractString("class=\"count\">","</em>");
        shop.setDeliveryScore(this.parseFloat(deliveryScoreStr));

        //卖家信用抽取方式1 <span id="chart-num" class="data">105500</span>
        String sellerCreditStr = usersr.extractLine("<span id=\"chart-num\" class=\"data\">");
        sellerCreditStr = new StringExtract(sellerCreditStr).extractString("class=\"data\">","</span>");

        //卖家信用抽取方式2 <div class="list">卖家信用：105500
        if(sellerCreditStr==null){
            sellerCreditStr = usersr.extractLine("<div class=\"list\">卖家信用");
            sellerCreditStr = new StringExtract(sellerCreditStr).extractString("卖家信用：",null);
        }
        shop.setSellerCredit(this.parseLong(sellerCreditStr));
        System.out.println(shop);
        tbshopService.UpdateNoNull(shop);
        Thread.sleep(200*1);
    }





    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopService = SpringUtil.getBean(TBShopService.class);
        while(true){
            List<TBShop> shoplist =  tbshopService.queryByNull("sellerCredit",1000);
            if(shoplist==null || shoplist.size()==0){
                break;
            }
            for(TBShop shop:shoplist){
                if(shopidMap.containsKey(shop.getShopid())){
                    continue;
                }
                shopidMap.put(shop.getShopid(),null);
                try{
                    new TBUserRateCrawer().userRateUrlAction(shop);

                }catch(Exception e){
                    e.printStackTrace();;
                }
            }
        }
        System.out.println("end");
    }


}
