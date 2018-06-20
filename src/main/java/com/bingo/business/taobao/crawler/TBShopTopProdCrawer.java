package com.bingo.business.taobao.crawler;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShopProd;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.common.String.StringExtract;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝的热门商品页面抓取
 *
 https://rate.taobao.com/user-rate-UvCIbOF8WMCgGMNTT.htm
 *
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBShopTopProdCrawer extends BaseCrawer{

    private static Map<String,String> cookie = null;
    //商家ID记录，对于重复的商家不处理
    private static Map<Long,Boolean> keyMap = new HashMap<>();

    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);


    //处理userRateUrl页面解析
    public void doAction(TBShopProd prod) throws Exception {
        //处理userRateUrl页面数据
        String prodUrl = "https://item.taobao.com/item.htm?id="+prod.getProductId();
       // Connection conn = Jsoup.connect(prodUrl);
        String body = this.httpGet(prodUrl);
        if(body!=null & body.indexOf("name=\"region\"")==-1){
            System.out.println("数据读取失败");
            return;
        }

        StringExtract bodyExt = new StringExtract(body);
        //地区提取
        String loc = bodyExt.extractLine("name=\"region\" value=\"");
        loc = trim(new StringExtract(loc).extractString("name=\"region\" value=\"","\" />"));
        if(loc!=null){
            prod.setLoc(loc);
        }
        //销量


        //市场价 <input type="hidden" name="current_price" value= "198.00"/>


        //淘宝价


        //<meta name="microscope-data" content="pageId=457739493;prototypeId=2;siteId=12;shopId=107067463;userid=1855022748;">
        String microscope = bodyExt.extractLine("microscope-data","meta");
        //shopid,userid
        String shopId = trim(new StringExtract(microscope).extractString("shopId=",";"));
        String userid = trim(new StringExtract(microscope).extractString("userid=",";"));
        prod.setUid(this.parseLong(userid));
        prod.setShopid(this.parseLong(shopId));
        tbshopProdService.UpdateNoNull(prod);
        //Thread.sleep(200*1);
    }





    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopProdService = SpringUtil.getBean(TBShopProdService.class);
        while(true){
            List<TBShopProd> shoplist =  tbshopProdService.queryByNull("loc",1,1000,null);
            if(shoplist==null || shoplist.size()==0){
                break;
            }
            for(TBShopProd prod:shoplist){
                try{
                    new TBShopTopProdCrawer().doAction(prod);
                }catch(Exception e){
                    e.printStackTrace();;
                }
            }
        }
        System.out.println("end");
    }


}
