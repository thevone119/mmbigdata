package com.bingo.business.taobao.crawler;

import com.bingo.Mmbigdata2Application;
import com.bingo.SpringUtil;
import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.model.TBShopProd;
import com.bingo.business.taobao.service.TBShopProdService;
import com.bingo.business.taobao.service.TBShopService;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 淘宝的卖家主页爬虫
 *
 https://rate.taobao.com/user-rate-UvCIbOF8WMCgGMNTT.htm
 *
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBProdCrawer extends BaseCrawer{

    private static Map<String,String> cookie = null;
    //商家ID记录，对于重复的商家不处理
    private static Map<Long,Boolean> keyMap = new HashMap<>();

    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);

    private static AtomicInteger thisIdx = new AtomicInteger(0);


    public void doAction(TBShopProd prod){
        try{
            doActionProxy(prod);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        //自减
        thisIdx.getAndDecrement();
    }

    //处理userRateUrl页面解析
    public void doActionProxy(TBShopProd prod) throws Exception {
        //处理页面数据
        String prodUrl = "https://item.taobao.com/item.htm?id="+prod.getProductId();
        MyRequests req = new MyRequests();
        //req.proxyIp="cmproxy.gmcc.net";
        //req.proxyPort = 8081;
        XJsonInfo ret =req.get(prodUrl);
        String body = ret.getData().toString();

        //String body = this.httpGet(prodUrl,2);
        StringExtract bodyExt = new StringExtract(body);
        if(body!=null & body.indexOf("microscope-data")==-1){
            if(body.indexOf("您查看的宝贝不存在")!=-1){
                tbshopProdService.delete(prod.getProductId());
                return;
            }
            System.out.println("数据抓取失败"+prodUrl);
            return;
        }
        //<meta name="microscope-data" content="pageId=457739493;prototypeId=2;siteId=12;shopId=107067463;userid=1855022748;">
        String microscope = bodyExt.extractLine("microscope-data","meta");
        //shopid,userid
        String shopId = trim(new StringExtract(microscope).extractString("shopId=",";"));
        String userid = trim(new StringExtract(microscope).extractString("userid=",";"));
        //地区提取
        String loc = bodyExt.extractLine("name=\"region\" value=\"");
        loc = trim(new StringExtract(loc).extractString("name=\"region\" value=\"","\" />"));


        prod.setUid(this.parseLong(userid));
        prod.setShopid(this.parseLong(shopId));
        if(loc!=null){
            prod.setLoc(loc);
        }
        tbshopProdService.UpdateNoNull(prod);
        System.out.println(shopId+","+userid);
        //tbshopProdService.UpdateNoNull(prod);
        //Thread.sleep(200*1);
    }





    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopProdService = SpringUtil.getBean(TBShopProdService.class);
        MyThreadPool pool = new MyThreadPool(1,5);
        while(true){

            List<TBShopProd> shoplist =  tbshopProdService.queryByNull("uid",2,200,null);
            if(shoplist==null || shoplist.size()<5){
                break;
            }
            for(TBShopProd prod:shoplist){
                pool.execute(()->{
                    try{
                        //自增
                        thisIdx.getAndIncrement();
                        new TBProdCrawer().doAction(prod);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                });
            }
        }
        System.out.println("end");
    }


}
