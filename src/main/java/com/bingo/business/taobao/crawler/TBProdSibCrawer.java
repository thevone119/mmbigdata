package com.bingo.business.taobao.crawler;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShopProd;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.business.taobao.util.TBJsessionId;
import com.bingo.common.String.StringExtract;
import com.bingo.common.http.MyRequests;
import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.XJsonInfo;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;

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
public class TBProdSibCrawer extends BaseCrawer{

    private static Map<String,String> cookie = null;
    //商家ID记录，对于重复的商家不处理
    private static Map<Long,Boolean> keyMap = new HashMap<>();

    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);


    //处理userRateUrl页面解析
    public void doAction(TBShopProd prod) throws Exception {
        java.util.Random r  = new java.util.Random();
        //处理页面数据
        String prodUrl = "https://item.taobao.com/item.htm?spm=a219r.lm874.14.21.46a970e411pyHg&id="+r.nextInt();

        //地区提取
        String loc = null;

        String sibUrl = "https://detailskip.taobao.com/service/getData/1/p1/item/detail/sib.htm?itemId="+prod.getProductId()+"&sellerId="+prod.getUid()+"&modules=dynStock,viewer,price,duty,xmpPromotion,delivery,activity,fqg,zjys,soldQuantity,originalPrice";

        Map<String,String> headers = new HashMap<>();
        //headers.put("referer","https://item.taobao.com/item.htm?id=38956758536");
        headers.put("referer",prodUrl);

        MyRequests req = new MyRequests();
        //req.proxyIp = "114.235.23.157";
        req.proxyPort = 9000;
        req.proxyType = "HTTP";
        req.hreads = headers;
        XJsonInfo ret = req.get(sibUrl);


        String sibbody = ret.getData().toString();
        if(sibbody.indexOf("SUCCESS")!=-1){
            JSONObject json = new JSONObject(sibbody);
            json = json.getJSONObject("data");
            loc = json.getJSONObject("deliveryFee").getJSONObject("data").getString("sendCity");
            String price = json.getString("price");
            if(price!=null && price.indexOf("-")!=-1){
                price = price.split("-")[0];
                price = price.trim();
            }
            JSONObject promoData = json.getJSONObject("promotion").getJSONObject("promoData");
            String shop_price =price;
            if(promoData.has("def")){
                JSONObject spObj =(JSONObject)promoData.getJSONArray("def").get(0);
                if(spObj.has("price")){
                    shop_price = spObj.getString("price");
                }
            }
            Long saleNum = null;
            if(json.has("soldQuantity")){
                saleNum = json.getJSONObject("soldQuantity").getLong("soldTotalCount");
            }
            prod.setSaleNum(saleNum);
            if(shop_price!=null){
                prod.setShopPrice(new Float(shop_price).longValue());
            }
            if(price!=null){
                prod.setPrice(new Float(price).longValue());
            }

        }else{
            System.out.println("sib error~!"+loc);
            return;
        }

        if(loc!=null){
            prod.setLoc(loc);
        }
        System.out.println(prod);
        tbshopProdService.UpdateNoNull(prod);
        //tbshopProdService.UpdateNoNull(prod);
        //Thread.sleep(200*1);
    }





    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopProdService = SpringUtil.getBean(TBShopProdService.class);
        MyThreadPool pool = new MyThreadPool(1,300);
        int i=0;
        while(true){
            List<TBShopProd> shoplist =  tbshopProdService.queryByHql("from TBShopProd where loc is null and uid is not null",300);
            if(shoplist==null || shoplist.size()==0){
                break;
            }
            for(TBShopProd prod:shoplist){
                try{
                    new TBProdSibCrawer().doAction(prod);
                    System.out.println(i);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                //Thread.sleep(1000*2);
            }
        }
        pool.shutdown();
        System.out.println("end");
    }


}
