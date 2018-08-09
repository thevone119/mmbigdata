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
import com.bingo.common.http.MyRequests;
import com.bingo.common.model.SessionUser;
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
    private SessionCacheService sessionCache;



    /**
     * 创建支付订单,进行充值
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @RequestMapping("/createpay")
    public XJsonInfo createpay(Integer pay_type,Float price) throws Exception {
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

        //创建支付订单
        String url = pay_local_url+"/payapi/create";

        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(System.currentTimeMillis()+"");
        payi.setPay_ext1(loginuser.getUserid()+"");
        payi.setPay_ext2(price+"");
        payi.setPay_name("支付平台充值(￥"+price+"元)");
        payi.setPay_type(pay_type);
        payi.setPrice(price);
        MyRequests req = new MyRequests();
        String body = req.httpPost(url,payi.getPostData(pay_sign_key));
        //String body = response.body();
        //1.查询商户
        JSONObject retj = new JSONObject(body);
        if(retj.getInt("ret_code")==1){
            //创建支付订单成功,跳转到支付页
            ret.setSuccess(true);
            ret.setData(pay_url+"/payapi/pay_page?pay_id="+retj.getString("pay_id")+"&nonce_str="+System.currentTimeMillis());
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
        //支付成功,进行账户充值
        Long userid = new Long(payret.getPay_ext1());
        Float price = new Float(payret.getPay_ext2());
        //充值
        recharge(payret.getPay_id(),userid,price);
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
        MyRequests req = new MyRequests();

        String body = req.httpPost(url,payi.getPostData(pay_sign_key));
        //
        JSONObject retj = new JSONObject(body);
        //查询订单成功
        if(retj.getInt("ret_code")==1){
            String pay_id = retj.getString("pay_id");
            Long userid = retj.getLong("pay_ext1");
            Float price = new Float(retj.getString("pay_ext2"));
            Integer pay_state = retj.getInt("pay_state");
            Integer pay_type = retj.getInt("pay_type");

            request.setAttribute("pay_type",pay_type);
            //支付成功
            if(pay_state==1){
                recharge(pay_id,userid,price);
                request.setAttribute("msg","充值完成，充值金额已到账");
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
