package com.bingo.business.taobao.crawler;

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

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 淘宝的商家列表爬虫
 * 爬取淘宝的所有商家列表信息
 * 1.目前只爬取茂名一个地市的商家列表
 * 2.根据产品搜索来爬
 * https://s.taobao.com/search?ie=utf8&initiative_id=staobaoz_20180407&stats_click=search_radio_all%3A1&js=1&imgfile=&q=17%E5%B2%81%E7%94%B7%E8%A1%A3%E6%9C%8D&_input_charset=utf-8&&loc=%E5%B9%BF%E5%B7%9E
 * Created by huangtw
 * on 2018-04-04.
 */
public class TBShopSearchByProdCrawer {

    //爬取的基础URL，分页爬取,写死
    private static String cBaseUrl = "https://s.taobao.com/search?ie=utf8&initiative_id=staobaoz_${now}&stats_click=search_radio_all%3A1&js=1&imgfile=&q=${q}&_input_charset=utf-8&&loc=${loc}&s=${s}";
    private static String sussidx = "g_page_config =";//返回成功应该包含的字符串

    //商家ID记录，对于重复的商家不处理
    private static Map<String,Boolean> keyMap = new HashMap<>();

    //爬取的地市
    public  String city = "茂名";
    //爬取搜索的关键字
    public String qkey = "";

    public int pageNo=1;// 当前页
    public int pageSize=44;// 每页记录数

    public int maxPage = 200;//最多只爬前100页
    public long sleep = 1000*0;//每次执行完等待时间
    public long errorSleep = 1000*1;//每次错误执行等待时间
    public int reTryTime = 5;//失败重爬次数
    public boolean isSuccess = false;//抓取是否成功

    //因为开启多线程进行处理，数据库对象无法注入，通过引用注入
    //因为开启多线程进行处理，数据库对象无法注入，通过springUtil获取
    private static TBShopService tbshopService = SpringUtil.getBean(TBShopService.class);
    private static TBUserService tbuserService = SpringUtil.getBean(TBUserService.class);
    private static TBShopProdService tbshopProdService = SpringUtil.getBean(TBShopProdService.class);

    public TBShopSearchByProdCrawer( String qkey){
        this.qkey =qkey;
    }

    public TBShopSearchByProdCrawer(String city, String qkey){
        this.city = city;
        this.qkey =qkey;
    }

    //开始抓取数据
    public void start() throws Exception {
        //url参数
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now=dtf2.format(LocalDate.now());
        String q = java.net.URLEncoder.encode(qkey,"utf-8");
        String loc = java.net.URLEncoder.encode(this.city,"utf-8");
        //分页抓取
        for(int p=pageNo;p<maxPage;p++){

            int s = p*pageSize - pageSize;
            StringReplace surl = new StringReplace(cBaseUrl);
            surl.put("now",now);
            surl.put("q",q);
            surl.put("loc",loc);
            surl.put("s",s+"");
            String url =surl.toMarkString();
            int ret = 0;
            //尝试X次，如果都失败，则退出
            for(int t=0;t<reTryTime;t++){
                ret = crawer(url,p);
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
    private int crawer(String url,int p) throws Exception {
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
        //headers.put("cookie","enc=O7CDUzcOveUfGeRXgo%2FdsCvZuKgbuM88P8BkI9Ms6DRoUVx%2Bqc0epc%2BoE8GmYRkedNfGVrCALp71zr6uj6siLw%3D%3D; t=4690a04cb7044ee27e29ab51fe7928d8; cna=VRHvEc5Li3QCAdOI/eZ8Nwpp; thw=cn; cookie2=19fea402cbca8162da2d2f8c6bcfb743; v=0; _tb_token_=75e51765b736a; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci%3D-1_0; "+ TBJsessionId.getJsessionId());
        conn.headers(headers);
        conn.timeout(1000*20);
        Connection.Response response = conn.execute();
        String body = response.body();
        if(body.indexOf(sussidx)==-1){
            System.out.println( body);
            //Map<String, String> hmap = response.headers();
            return 1;
        }
        //抽取数据
        StringExtract sr = new StringExtract(body);
        String g_pagestr = sr.extractLine("g_page_config","pageName");
        sr = new StringExtract(g_pagestr);
        g_pagestr = g_pagestr.substring(g_pagestr.indexOf("g_page_config = ")+"g_page_config = ".length());
        //itemlist，则分页已经结束了
        if(g_pagestr.indexOf("auctions")==-1 || g_pagestr.indexOf("data")==-1){
            return 2;
        }
        try{
            crawerPage(g_pagestr);
        }catch (Exception e){
            System.out.println(g_pagestr);
            e.printStackTrace();
        }
        Thread.sleep(sleep);
        return 0;
    }

    //处理分页数据
    private int crawerPage(String pagejson) throws Exception {
        JSONObject json = new JSONObject(pagejson);
        json = json.getJSONObject("mods");
        json = json.getJSONObject("itemlist");
        json = json.getJSONObject("data");
        JSONArray ja = json.getJSONArray("auctions");
        for(int i=0;i<ja.length();i++){
            JSONObject item  = (JSONObject)ja.get(i);

            long productId = item.getLong("nid");//商品ID
            String prodName = item.getString("raw_title");//商品名称
            String loc = item.getString("item_loc");//归属地市
            long uid = item.getLong("user_id");//用户ID
            Long cid =null;//商品类目ID
            if(item.has("category") && item.getString("category").length()>0){
                cid = item.getLong("category");//商品类目ID
            }
            String nick = item.getString("nick");//用户昵称
            long shop_price  = (long)(item.getDouble("view_price") *100);//商品价格，淘宝价(分)

            //评价数，评价数为0的不计算
            int comment_count =0;
            if(item.has("comment_count") && item.getString("comment_count").length()>0){
                comment_count = item.getInt("comment_count");
            }

            //商品
            TBShopProd prod = new TBShopProd();
            prod.setProductId(productId);
            prod.setName(prodName);
            prod.setCid(cid);
            prod.setLoc(loc);
            prod.setShopPrice(shop_price);

            //用户
            TBUser user = new TBUser();
            user.setUid(uid);
            user.setNick(nick);
            user.setLoc(loc);
            if(!keyMap.containsKey(productId) && comment_count>0){
                keyMap.put(productId+"",null);
                try {
                    tbshopProdService.UpdateNoNull(prod);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(!keyMap.containsKey(uid+"u")){
                keyMap.put(uid+"u",null);
                try{
                    tbuserService.UpdateNoNull(user);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }




    public static void main(String args[]) throws Exception {
        String url = "https://s.taobao.com/search?initiative_id=tbindexz_20170306&ie=utf8&spm=a21bo.2017.201856-taobao-item.2&sourceId=tb.index&search_type=item&ssid=s5-e&commend=all&imgfile=&q=吃&suggest=history_1&_input_charset=utf-8&wq=&suggest_query=&source=suggest";
        url = java.net.URLEncoder.encode(url);
        TBShopSearchByProdCrawer ts = new TBShopSearchByProdCrawer("茂名","女装");
        ts.start();;



    }


}
