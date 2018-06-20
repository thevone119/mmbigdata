package com.bingo.business.taobao.mianrun;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.crawler.BaseCrawer;
import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.service.TBShopService;
import com.bingo.common.exception.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2018-04-11.
 */
public class ImpData   {

    public static void main(String args[]) throws Exception{
        initUserRate(args);

    }

    private  static void initUserRate(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);

        BufferedReader reader  = new BufferedReader(new FileReader("e:/user_json.txt"));
        String tempString = null;

        while ((tempString = reader.readLine()) != null) {
            // 显示行号

            JSONObject json = new JSONObject(tempString);
            String userRateUrl = json.getString("userRateUrl");
            userRateUrl = userRateUrl.substring("https:".length());


            Float deliveryScore = parseFloat(json.getString("deliveryScore"));
            Float serviceScore = parseFloat(json.getString("serviceScore"));
            Float itemScore = parseFloat(json.getString("itemScore"));
            Integer commentCount = parseInt(json.getString("commentCount"));
            String mainCname = json.getString("mainCname");
            String shopArea = json.getString("shopArea");
            if("null".equals(shopArea)){
                shopArea = null;
            }
            Long sellerCredit = parseLong(json.getString("sellerCredit"));

            TBShop shop = tbshopService.queryByUserRateUrl(userRateUrl);
            if(shop==null){
                continue;
            }
            shop.setDeliveryScore(deliveryScore);
            shop.setServiceScore(serviceScore);
            shop.setItemScore(itemScore);
            shop.setCommentCount(commentCount);
            shop.setMainCname(mainCname);
            shop.setShopArea(shopArea);
            shop.setSellerCredit(sellerCredit);

            tbshopService.UpdateNoNull(shop);
        }
        reader.close();
        System.out.println("end");

    }
    private  static void initShopCreateTime(String args[]) throws IOException, ServiceException {
        SpringApplication.run(Mmbigdata2Application.class, args);
        TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);
        BufferedReader reader  = new BufferedReader(new FileReader("e:/mycreatetime.txt"));
        String tempString = null;

        while ((tempString = reader.readLine()) != null) {
            // 显示行号
            System.out.println( tempString);
            String s[] = tempString.split(":");
            if(s.length!=2){
                continue;
            }
            TBShop shop = tbshopService.get(new Long(s[0]));
            shop.setShopCreatetime(s[1]);
            tbshopService.saveOrUpdate(shop);
        }
        reader.close();
        System.out.println("end");

    }

    protected static  Long  parseLong(String str){
        if(str==null){
            return null;
        }
        try{
            return new Long(str);
        }catch (Exception e){

        }
        return null;
    }

    protected static Float parseFloat(String str){
        if(str==null){
            return null;
        }
        try{
            return new Float(str);
        }catch (Exception e){

        }
        return null;
    }

    protected static Integer parseInt(String str){
        if(str==null){
            return null;
        }
        try{
            return new Integer(str);
        }catch (Exception e){

        }
        return null;
    }
}
