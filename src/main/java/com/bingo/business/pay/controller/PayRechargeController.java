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
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
        Connection conn = Jsoup.connect(pay_local_url+"/payapi/create");
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        conn.headers(headers);
        //post data
        //conn.data(entry.getKey(), entry.getValue());
        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(System.currentTimeMillis()+"");
        payi.setPay_ext1(loginuser.getUserid()+"");
        payi.setPay_ext2(price+"");
        payi.setPay_name("支付平台充值(￥"+price+"元)");
        payi.setPay_type(pay_type);
        payi.setPrice(price);
        //payi.setReturn_url();
        conn.data(payi.getPostData(pay_sign_key));

        conn.timeout(1000*10);
        conn.followRedirects(true);
        //post
        Connection.Response response = conn.method(Connection.Method.POST).execute();
        String body = response.body();
        //1.查询商户
        JSONObject retj = new JSONObject(body);
        if(retj.getInt("ret_code")==1){
            //创建支付订单成功
            ret.setSuccess(true);
            ret.setData(retj.getString("pay_id"));
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
        //支付成功,进行账户充值
        Long userid = new Long(payret.getPay_ext1());
        Float price = new Float(payret.getPay_ext2());
        //充值
        recharge(payret.getPay_id(),userid,price);
        return "SUCCESS";
    }

    /**
     * 支付跳转
     * @return
     * @throws Exception
     */
    @RequestMapping("/goback")
    public ModelAndView goback(String orderid) throws Exception {
        //查询订单，查看订单是否成功
        //查询订单接口
        Connection conn = Jsoup.connect(pay_local_url+"/payapi/query");
        //伪装http头
        Map<String,String> headers = new HashMap<>();
        headers.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("cache-control","max-age=0");
        headers.put("upgrade-insecure-requests","1");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        conn.headers(headers);

        PayInput payi = new PayInput();
        payi.setUid(pay_uid);
        payi.setNonce_str(System.currentTimeMillis()+"");
        payi.setOrderid(orderid);
        conn.data(payi.getPostData(pay_sign_key));

        conn.timeout(1000*10);
        conn.followRedirects(true);
        //post
        Connection.Response response = conn.method(Connection.Method.POST).execute();
        String body = response.body();
        //
        JSONObject retj = new JSONObject(body);
        if(retj.getInt("ret_code")==1){
            //查询订单成功
            String pay_id = retj.getString("pay_id");
            Long userid = new Long(retj.getString("pay_ext1"));
            Float price = new Float(retj.getString("pay_ext2"));
            recharge(pay_id,userid,price);
        }else{

        }

        return null;
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
        payc.setCid(pay_id);
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
