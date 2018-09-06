package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.parameter.PayInput;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.business.pay.service.PayBusService;
import com.bingo.business.pay.service.PayLogService;
import com.bingo.business.pay.service.PayProdImgService;
import com.bingo.business.pay.service.PayService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.http.HttpReturn;
import com.bingo.common.http.MyOkHttp;
import com.bingo.common.http.MyRequests;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.WebClass;
import com.bingo.common.utility.XJsonInfo;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018-07-30.
 * 支付平台充值
 *
 */
@RestController
@RequestMapping("/api/pay/recharge")
public class PayRechargeController {
    private static final Logger logger = LoggerFactory.getLogger(PayRechargeController.class);

    @Value("${heimi.pay.url}")
    private String pay_url;

    @Value("${heimi.pay.local.url}")
    private String pay_local_url;

    @Value("${heimi.pay.uid}")
    private String pay_uid;

    @Value("${heimi.pay.sign_key}")
    private String pay_sign_key;

    @Resource
    private PayService payService;

    @Resource
    private PayLogService paylogService;



    @Resource
    private SessionCacheService sessionCache;

    @Resource
    private RedisCacheService redis;



    /**
     * 创建支付订单,进行充值
     * 为了避免重复创建支付订单，这里对同一个商户创建的固定金额的订单，做缓存处理
     * 1.注意，这里不能做缓存处理，如果缓存了，那么会影响到前端的多次支付逻辑。
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @RequestMapping("/createpay")
    public XJsonInfo createpay(HttpServletRequest request, Integer pay_type,Float price) throws Exception {
        XJsonInfo ret = new XJsonInfo(false);
        SessionUser loginuser = sessionCache.getLoginUser();
        if(loginuser==null){
            ret.setMsg("对不起，您还未登陆，请登录后再进行充值");
            return ret;
        }

        //校验充值金额
        if(price!=10 && price!=50 && price!=100){
            ret.setMsg("对不起，目前只能只能进行10、50、100 三种金额的充值");
            return ret;
        }

        StringBuffer requrl = request.getRequestURL();
        String tempContextUrl = requrl.delete(requrl.length() - request.getRequestURI().length(), requrl.length()).toString();

        //创建支付订单
        String url = pay_local_url+"/payapi/create";

        String c_key  = "pay:recharge_orderid:"+loginuser.getUserid()+","+pay_type+","+price;
        //缓存2小时吧
        String orderid = (String)redis.get(c_key);
        if(orderid==null){
            orderid = System.currentTimeMillis()+"";
            redis.set(c_key,orderid,60*2);
        }
        //查询订单是否已支付了，如果已支付了。则使用新的订单号哦
        PayLog log = paylogService.queryByUidOrderid(pay_uid,orderid);
        if(log!=null && log.getPayState()==1){
            orderid = System.currentTimeMillis()+"";
            redis.set(c_key,orderid,60*2);
        }


        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(orderid);
        payi.setPay_ext1(loginuser.getUserid()+"");
        payi.setPay_ext2(price+"");
        payi.setPay_name("支付平台充值(￥"+price+"元)");
        payi.setPay_type(pay_type);
        payi.setPrice(price);
        MyOkHttp req = new MyOkHttp();
        HttpReturn httpret = req.post(url,payi.getPostData(pay_sign_key));
        //String body = response.body();
        //1.查询商户
        JSONObject retj = new JSONObject(httpret.body);
        if(retj.getInt("ret_code")==1){
            //创建支付订单成功,跳转到支付页
            ret.setSuccess(true);
            ret.setData(tempContextUrl+"/payapi/pay_page?pay_id="+retj.getString("pay_id")+"&nonce_str="+System.currentTimeMillis());
        }else{
            ret.setSuccess(false);
            ret.setMsg(retj.getString("ret_msg"));
        }
        return ret;
    }

    /**
     * 创建小额测试订单，不用登录，不用充值
     * @param pay_type
     * @return
     * @throws Exception
     */
    @RequestMapping("/createtest")
    public XJsonInfo createtest(HttpServletRequest request, Integer pay_type) throws Exception {
        XJsonInfo ret = new XJsonInfo(false);
        String sessid = sessionCache.getCurrSessionId();

        //小额测试,5分钱
        Float price = 0.05f;

        //创建支付订单
        String url = pay_local_url+"/payapi/create";

        String c_key  = "pay:test:"+sessid+","+pay_type+","+price;
        //缓存2小时吧
        String orderid = (String)redis.get(c_key);
        if(orderid==null){
            orderid = System.currentTimeMillis()+"";
            redis.set(c_key,orderid,60*2);
        }
        //查询订单是否已支付了，如果已支付了。则使用新的订单号哦
        PayLog log = paylogService.queryByUidOrderid(pay_uid,orderid);
        if(log!=null && log.getPayState()==1){
            orderid = System.currentTimeMillis()+"";
            redis.set(c_key,orderid,60*2);
        }

        StringBuffer requrl = request.getRequestURL();
        String tempContextUrl = requrl.delete(requrl.length() - request.getRequestURI().length(), requrl.length()).toString();


        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(orderid);
        payi.setPay_ext1("0");//测试时，用户ID为0
        payi.setPay_ext2(price+"");
        payi.setPay_name("测试支付(￥"+price+"元)");
        payi.setPay_type(pay_type);
        payi.setPrice(price);

        MyOkHttp req = new MyOkHttp();
        HttpReturn httpret = req.post(url,payi.getPostData(pay_sign_key));
        //String body = response.body();
        //1.查询商户
        JSONObject retj = new JSONObject(httpret.body);
        if(retj.getInt("ret_code")==1){
            //创建支付订单成功,跳转到支付页
            ret.setSuccess(true);
            ret.setData(tempContextUrl+"/payapi/pay_page?pay_id="+retj.getString("pay_id")+"&nonce_str="+System.currentTimeMillis());
        }else{
            ret.setSuccess(false);
            ret.setMsg(retj.getString("ret_msg"));
        }
        return ret;
    }

    /**
     * 回调通知
     * @return
     * @throws Exception
     */
    @RequestMapping("/notify")
    public String notify(PayReturn payret) throws Exception {
        String sign = payret.MarkSign(pay_sign_key);
        if(!sign.equals(payret.getSign())){
            return "ERROR";
        }
        if(payret.getPay_state()!=1){
            //
            return "ERROR";
        }
        //logger.info("SUCCESS1");
        //部分非充值业务，直接返回
        if(payret.getPay_ext1()==null){
            return "SUCCESS";
        }
        //支付成功,进行账户充值
        Long userid = new Long(payret.getPay_ext1());
        Float price = new Float(payret.getPay_ext2());
        if(userid>0){
            //充值
            recharge(payret.getPay_id(),userid,price);
        }
        //logger.info("SUCCESS2");
        return "SUCCESS";
    }

    /**
     * 支付跳转
     * @return
     * @throws Exception
     */
    @RequestMapping("/goback")
    public ModelAndView goback(HttpServletRequest request,String orderid) throws Exception {
        request.setAttribute("msg","充值过程中发生错误，请稍候再试");
        request.setAttribute("pay_type",1);
        //查询订单，查看订单是否成功
        //查询订单接口
        String url = pay_local_url+"/payapi/query";
        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(orderid);

        MyOkHttp req = new MyOkHttp();
        HttpReturn httpret = req.post(url,payi.getPostData(pay_sign_key));
        //
        JSONObject retj = new JSONObject(httpret.body);
        //查询订单成功
        if(retj.getInt("ret_code")==1){
            Integer pay_type = retj.getInt("pay_type");
            request.setAttribute("pay_type",pay_type);
            Integer pay_state = retj.getInt("pay_state");
            //支付成功
            if(pay_state==1){
                //快捷支付测试，没有扩展字段
                if(!retj.has("pay_ext1")){
                    request.setAttribute("msg","测试成功，已完成支付");
                    return new  ModelAndView("/pay/recharge_test_end");
                }
                String pay_id = retj.getString("pay_id");
                Long userid = retj.getLong("pay_ext1");
                Float price = new Float(retj.getString("pay_ext2"));
                if(userid>0){
                    recharge(pay_id,userid,price);
                    request.setAttribute("msg","充值完成，充值金额已到账");
                }else{
                    request.setAttribute("msg","测试成功，已完成支付");
                    return new  ModelAndView("/pay/recharge_test_end");
                }
            }else{

            }
        }else{

        }
        //无论是否支付成功，都跳转到充值完成页
        return new  ModelAndView("/pay/recharge_end");
    }

    /**
     * 对商户进行充值
     */
    private void recharge(String pay_id,Long userid,Float price) throws Exception {
        Float emoney  = price;
        if (price == 50) {
            emoney  = price+price*0.05f;
        }
        if (price == 100) {
            emoney  = price+price*0.1f;
        }
        PayBusChange payc = new PayBusChange();
        payc.setCid("recharge_"+pay_id);
        payc.setBizId(pay_id);
        payc.setBusId(userid);
        payc.setState(1);
        payc.setEmoney(emoney);
        payc.setCtype(1);
        payc.setDemo("支付平台充值("+price+"元)，实际到账("+emoney+"元)");
        //payc.setLogId();
        payService.busChange(payc,null,null);

    }






}
