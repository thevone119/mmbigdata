package com.bingo.business.taobao.crawler;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.model.TBShopProd;
import com.bingo.business.taobao.model.TBUser;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.business.taobao.service.TBShopService;
import com.bingo.business.taobao.service.TBUserService;
import com.bingo.business.taobao.util.TBJsessionId;
import com.bingo.common.String.StringExtract;
import com.bingo.common.String.StringReplace;
import com.bingo.common.thread.MyThreadPool;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 淘宝的商家主页爬虫
 * 1.先查找主页
 * 2.查找
 *
 * https:////shop114816300.taobao.com
 *
 * https://store.taobao.com/shop/view_shop.htm?user_number_id=673126895
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBShopMainCrawer extends BaseCrawer{

    //爬取的基础URL，分页爬取,写死
    private static String cBaseUrl01 = "https://store.taobao.com/shop/view_shop.htm?user_number_id=${uid}";
    private static String cBaseUrl = "https://shop${shopid}.taobao.com";

    private static String sussidx = "shop_config.isView";//返回成功应该包含的字符串

    //商家ID记录，对于重复的商家不处理
    private static Map<Long,Boolean> keyMap = new HashMap<>();

    public String url  = "";
    public long errorSleep = 100*1;//每次错误执行等待时间
    public int reTryTime = 2;//失败重爬次数
    public boolean isSuccess = false;//抓取是否成功

    //保存当前店铺信息，各个方法对这个对象进行更新处理
    private TBShop shop = new TBShop();

    //因为开启多线程进行处理，数据库对象无法注入，通过springUtil获取
    private static TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);
    private static TBUserService tbuserService = SpringUtil.getBean(TBUserService.class);
    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);



    public TBShopMainCrawer(){
    }

    //开始抓取数据
    public void crawerByUid(Long uid) throws Exception {
        //url参数
        StringReplace surl = new StringReplace(cBaseUrl01);
        surl.put("uid",uid+"");
        url = surl.toMarkString();
        //尝试X次，如果都失败，则退出
        for(int t=0;t<reTryTime;t++){
            int ret = crawerShopMain(url);
            if(ret==0 || ret==2){
                break;
            }
            Thread.sleep(errorSleep);
        }
    }

    public void crawerByShopid(Long shopid) throws Exception {

        StringReplace surl = new StringReplace(cBaseUrl);
        surl.put("shopid",shopid+"");
        url = surl.toMarkString();
        //尝试X次，如果都失败，则退出
        for(int t=0;t<reTryTime;t++){
            int ret = crawerShopMain(url);
            if(ret==0 || ret==2){
                break;
            }
            //Thread.sleep(errorSleep);
        }
    }



    //处理主页数据
    private int crawerShopMain(String url) throws Exception {
        String shop_body = httpGet(url);
        if(shop_body.indexOf("window.shop_config")==-1){
            return 1;
        }
        StringExtract sr = new StringExtract(shop_body);
        //<a class="shop-name" href="//chengyueyd.tmall.com"  rel="nofollow" ><span>成跃运动专营店</span></a>
        String shop_name_str = sr.extractLine("shop-name","<a class=");
        String shopname = new StringExtract(shop_name_str).extractString("<span>","</span>");
        String shopulr = new StringExtract(shop_name_str).extractString("href=\"","\"");
        //<span class="shop-name-title" title="沛百小精灵">沛百小精灵</span>
        if(shopname==null){
            String shop_name2 = sr.extractLine("shop-name-title","<span");
            shopname = new StringExtract(shop_name2).extractString("\">","</span>");
        }
        //
        String shop_config = sr.extractString("window.shop_config =","};") + "}";
        JSONObject json = new JSONObject(shop_config);
        Long userId = json.getLong("userId");
        Long shopId = json.getLong("shopId");
        String user_nick = java.net.URLDecoder.decode(json.getString("user_nick"),"utf-8");

        String userRateUrlstr = sr.extractLine("<a ","//rate.taobao.com/");
        String userRateUrl = new StringExtract(userRateUrlstr).extractString("href=\"","\"");


        //是否天猫
        String shopType = "TB";
        if(sr.indexNumber("tmall.com")>5){
            shopType = "TM";
        }
        //描述分数 1 <span class="dsr-num red">4.9</span>
        String itemScore = sr.extractLine(shop_body.indexOf("描述"),"dsr-num red" ,"</span>");
        itemScore = new StringExtract(itemScore).extractString("red\">","</span>");

        //服务分数 1 <span class="dsr-num red">4.9</span>
        String serviceScore = sr.extractLine(shop_body.indexOf("服务"),"dsr-num red" ,"</span>");
        serviceScore = new StringExtract(serviceScore).extractString("red\">","</span>");

        //物流分数 1 <span class="dsr-num red">4.9</span>
        String deliveryScore = sr.extractLine(shop_body.indexOf("物流"),"dsr-num red" ,"</span>");
        deliveryScore = new StringExtract(deliveryScore).extractString("red\">","</span>");


        //店铺
        shop.setShopid(shopId);
        shop.setTitle(shopname);
        shop.setMainpage(shopulr);

        shop.setUid(userId);
        shop.setNick(user_nick);
        shop.setShopType(shopType);
        shop.setUserRateUrl(userRateUrl);

        shop.setItemScore(this.parseFloat(itemScore));
        shop.setServiceScore(this.parseFloat(serviceScore));
        shop.setDeliveryScore(this.parseFloat(deliveryScore));
        System.out.println(shopId);
        //抽取热门商品
        String divs[] = sr.extractStringList("<div class=\"detail\">","</div>");
        if(divs!=null){
            for(String div :divs){
                TBShopProd tprod = new TBShopProd();
                StringExtract sdiv = new StringExtract(div);
                String title = sdiv.extractString("<a  title=\"","\"");
                String prodid = sdiv.extractString("item.htm?id=","\"");
                String SaleNum = sdiv.extractString("class=\"sale-count\">","</span>");
                String _shopPrice = sdiv.extractString("<span>","</span>");
                if(prodid==null||_shopPrice==null){
                    continue;
                }
                tprod.setName(title);
                tprod.setProductId(new Long(prodid));
                Float shopPrice = this.parseFloat(_shopPrice);
                //剔除运费
                if(shopPrice==1 && tprod.getName()!=null && tprod.getName().indexOf("费")!=-1){
                    continue;
                }
                tprod.setShopPrice(new Float(shopPrice*100).longValue());
                tprod.setSaleNum(this.parseLong(SaleNum));
                tbshopProdService.UpdateNoNull(tprod);
            }
        }
        return 0;
    }


    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopProdService = SpringUtil.getBean(TBShopProdService.class);
        tbshopService = SpringUtil.getBean(TBShopService.class);
        //开启3个等待线程去执行
        MyThreadPool pool = new MyThreadPool(5,10);
        while(true){
            List<TBShop> shoplist =  tbshopService.queryByNull("shopArea",100);
            if(shoplist.size()<100){
                break;
            }
            for(TBShop shop:shoplist){
                shop.setShopArea("null");
                tbshopService.saveOrUpdate(shop);
                pool.execute(()-> {
                    try {
                        new TBShopMainCrawer().crawerByShopid(shop.getShopid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        System.out.println("end");






    }


}
