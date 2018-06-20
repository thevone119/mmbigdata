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
import com.bingo.common.utility.WebClass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝的商家列表爬虫
 * 爬取淘宝的所有商家列表信息
 * 1.目前只爬取茂名一个地市的商家列表
 *
 https://shopsearch.taobao.com/search?app=shopsearch&q=%E5%A5%B3%E8%A3%85&imgfile=&js=1&stats_click=search_radio_all%3A1&initiative_id=staobaoz_20180405&ie=utf8&loc=%E8%8C%82%E5%90%8D&s=540
 *
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBShopSearchCrawer {

    //爬取的基础URL，分页爬取,写死
    private static String cBaseUrl = "https://shopsearch.taobao.com/search?app=shopsearch&q=${q}&imgfile=&js=1&stats_click=search_radio_all%3A1&initiative_id=staobaoz_${now}&ie=utf8&loc=${loc}&s=${s}";

    private static String cBaseUrl2 = "https://shopsearch.taobao.com/search?app=shopsearch&q=${q}&js=1&initiative_id=staobaoz_${now}&ie=utf8";




    private static String sussidx = "g_page_config =";//返回成功应该包含的字符串

    //商家ID记录，对于重复的商家不处理
    private static Map<String,Boolean> shopidMap = new HashMap<>();

    //爬取的地市
    public  String city = "茂名";
    //爬取搜索的关键字
    public String qkey = "";

    //搜索的商家的ID，针对单个商家进行搜索,这时city为空
    public String searchShopId = "";

    public int pageNo=1;// 当前页
    public int pageSize=20;// 每页记录数

    public int maxPage = 200;//最多只爬前100页
    public long sleep = 1000*1;//每次执行完等待时间
    public long errorSleep = 1000*1;//每次错误执行等待时间
    public int reTryTime = 5;//失败重爬次数
    public boolean isSuccess = false;//抓取是否成功

    //因为开启多线程进行处理，数据库对象无法注入，通过springUtil获取
    private static TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);
    private static TBUserService tbuserService = SpringUtil.getBean(TBUserService.class);
    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);


    public TBShopSearchCrawer(String qkey){
        this.qkey =qkey;
    }

    public TBShopSearchCrawer(String city,String qkey,String searchShopId){
        this.city = city;
        this.qkey =qkey;
        if(searchShopId!=null && searchShopId.length()>0){
            this.city=null;
            maxPage = 2;
        }
        this.searchShopId = searchShopId;
    }

    //开始抓取数据
    public void start() throws Exception {
        //url参数
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now=dtf2.format(LocalDate.now());
        String q = java.net.URLEncoder.encode(qkey,"utf-8");
        String loc = "";
        if(this.city!=null && this.city.length()>0){
            loc = java.net.URLEncoder.encode(this.city,"utf-8");
        }

        //分页抓取
        for(int p=pageNo;p<maxPage;p++){
            int s = p*pageSize - pageSize;
            StringReplace surl = new StringReplace(cBaseUrl);
            if(this.city!=null && this.city.length()>0){
                surl = new StringReplace(cBaseUrl2);
            }

            surl.put("now",now);
            surl.put("q",q);
            surl.put("loc",loc);
            surl.put("s",s+"");
            String url =surl.toMarkString();
            int ret = 0;
            //尝试X次，如果都失败，则退出
            for(int t=0;t<reTryTime;t++){
                try{
                    ret = crawer(url);
                }catch (Exception e){
                    e.printStackTrace();
                }

                System.out.println("数据抓包处理：q="+qkey+",page="+p+",ret="+ret);
                if(ret==0 || ret==2){
                    break;
                }
                Thread.sleep(errorSleep);
            }
            //
            if(ret==1){
                isSuccess = false;
                break;
            }else{
                isSuccess = true;
            }
            if(ret==2){
                break;
            }
        }
    }

    /**
     * 数据抓取处理
     * @param url
     *
     * @return 0:成功  1：失败 2：已结束
     */
    private int crawer(String url) throws Exception {
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
        //headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+ TBJsessionId.getJsessionId());
        headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+TBJsessionId.getNextIeCooike("D:/mycooike"));
        conn.headers(headers);
        conn.timeout(1000*20);

        Connection.Response response = conn.execute();
        //response.cookies()
        String body = response.body();
        if(body.indexOf(sussidx)==-1){
            System.out.println( response.statusCode());
            System.out.println( response.statusMessage());
            System.out.println( response.headers());
            //Map<String, String> hmap = response.headers();

            return 1;
        }
        //抽取数据
        StringExtract sr = new StringExtract(body);
        String g_pagestr = sr.extractLine("g_page_config","pageName");
        sr = new StringExtract(g_pagestr);
        g_pagestr = g_pagestr.substring(g_pagestr.indexOf("g_page_config = ")+"g_page_config = ".length());
        //没有shopItems，则分页已经结束了
        if(g_pagestr.indexOf("shopItems")==-1){
            return 2;
        }
        try{
            crawerPage(g_pagestr);
        }catch (Exception e){
            e.printStackTrace();
        }
        Thread.sleep(sleep);
        return 0;
    }

    //处理分页数据
    private int crawerPage(String pagejson) throws Exception {
        JSONObject json = new JSONObject(pagejson);
        json = json.getJSONObject("mods");
        json = json.getJSONObject("shoplist");
        json = json.getJSONObject("data");
        JSONArray ja = json.getJSONArray("shopItems");
        for(int i=0;i<ja.length();i++){
            JSONObject item  = (JSONObject)ja.get(i);
            String shopUrl = item.getString("shopUrl");
            String shopid = shopUrl.substring(shopUrl.indexOf("shop")+4,shopUrl.indexOf(".taobao"));
            if(searchShopId!=null && searchShopId.length()>0 && !searchShopId.equals(shopid)){
                continue;
            }
            if(shopidMap.containsKey(shopid)){
                continue;
            }
            shopidMap.put(shopid,null);
            crawerItem(item);
        }
        return 0;
    }

    private void crawerItem( JSONObject item ) throws Exception {
        //店铺
        TBShop shop = new TBShop();
        Long uid= item.getLong("uid");
        String nick = item.getString("nick");
        String userRateUrl = item.getString("userRateUrl");
        shop.setNick(nick);
        shop.setUid(uid);
        shop.setTitle(item.getString("title"));
        String shopUrl = item.getString("shopUrl");
        shop.setMainpage(shopUrl);
        String shopid = shopUrl.substring(shopUrl.indexOf("shop")+4,shopUrl.indexOf(".taobao"));
        shop.setShopid(new Long(shopid));
        if(item.has("goodratePercent")){
            String goodratePercent = item.getString("goodratePercent");
            if(goodratePercent!=null){
                goodratePercent = goodratePercent.replace("%","");
                shop.setGoodratePercent(new Float(goodratePercent));
            }
        }
        shop.setUserRateUrl(userRateUrl);
        shop.setSalesCount(item.getInt("totalsold"));
        shop.setProdCount(item.getInt("procnt"));
        shop.setShopArea(item.getString("provcity"));
        if(item.getBoolean("isTmall")){
            shop.setShopType("TM");
        }else{
            shop.setShopType("TB");
        }

        //用户
        TBUser user = new TBUser();
        user.setUid(uid);
        user.setNick(nick);
        user.setUserRateUrl(userRateUrl);

        //商品
        if(item.has("auctionsInshop")){
            JSONArray ja = item.getJSONArray("auctionsInshop");
            if(ja!=null && ja.length()>0){
                for(int i=0;i<ja.length();i++){
                    JSONObject p  = (JSONObject)ja.get(i);
                    TBShopProd prod = new TBShopProd();
                    prod.setProductId(p.getLong("nid"));
                    prod.setUid(p.getLong("uid"));
                    prod.setName( p.getString("title"));
                    prod.setShopPrice(new Double((p.getDouble("price") * 100)).longValue());
                    tbshopProdService.UpdateNoNull(prod);
                }
            }
        }
        System.out.println("地市:"+shop.getShopArea());
        tbshopService.UpdateNoNull(shop);
        tbuserService.UpdateNoNull(user);

    }



    public static void main(String args[]) throws Exception {
        SpringApplication.run(Mmbigdata2Application.class, args);
        tbshopService = SpringUtil.getBean(TBShopService.class);
        tbuserService = SpringUtil.getBean(TBUserService.class);
        tbshopProdService = SpringUtil.getBean(TBShopProdService.class);
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now=dtf2.format(LocalDateTime.now());
        //开启3个等待线程去执行
        MyThreadPool pool = new MyThreadPool(5,100);
        List<TBShop> list = tbshopService.queryByNull("shopArea",10000,"updateTime");
        for(TBShop s:list){
            if(s.getTitle()==null || s.getTitle().indexOf("[")!=-1){
                continue;
            }
            pool.execute(()->{
                try{
                    TBShopSearchCrawer ts = new TBShopSearchCrawer(null,s.getTitle(),s.getShopid()+"");
                    ts.start();;
                    if(!shopidMap.containsKey(s.getShopid()+"")){
                        ts.qkey = s.getNick();
                        ts.start();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            });
        }
    }


}
