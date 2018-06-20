package com.bingo.business.taobao.crawler;

import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.business.taobao.service.TBShopService;
import com.bingo.business.taobao.service.TBUserService;
import com.bingo.common.String.StringExtract;
import com.bingo.common.String.StringReplace;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
public class TBShopCreateTimeCrawer extends BaseCrawer{



    //因为开启多线程进行处理，数据库对象无法注入，通过springUtil获取
    private static TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);




    public TBShopCreateTimeCrawer(){
    }


    public void crawerByShop(TBShop shop) throws Exception {
        //获取开店时间 https://shop.taobao.com/getShopInfo.htm?shopId=68302154
        String ShopInfoBody = httpGet("https://shop.taobao.com/getShopInfo.htm?shopId="+shop.getShopid(),2);
        String shopCreatetime = new StringExtract(ShopInfoBody).extractString("starts\":\"","\"}");
        if(shopCreatetime==null){
            System.out.println("shopCreatetime error---");
        } else{
            System.out.println("shopCreatetime suss!");
            shop.setShopCreatetime(shopCreatetime);
            tbshopService.saveOrUpdate(shop);
        }
        Thread.sleep(500);
    }



    public static void main(String args[]) throws Exception {
        TBShop shop = new TBShop();
        shop.setShopid(254341635L);
        shop.setUserRateUrl("//rate.taobao.com/user-rate-UMCcyvFcu.htm");
        new TBShopCreateTimeCrawer().crawerByShop(shop);
    }



}
