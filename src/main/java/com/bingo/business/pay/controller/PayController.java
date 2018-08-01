package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.PayBus;
import com.bingo.business.pay.model.PayInput;
import com.bingo.business.pay.model.PayLog;
import com.bingo.business.pay.model.PayProd;
import com.bingo.business.pay.service.PayBusService;
import com.bingo.business.pay.service.PayLogService;
import com.bingo.business.pay.service.PayProdService;
import com.bingo.business.pay.service.PayService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018-07-30.
 * 支付平台外部接口提供
 *
 */
@RestController
@RequestMapping("/payapi")
public class PayController {
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    @Resource
    private PayLogService paylogService;
    @Resource
    private PayBusService paybusService;
    @Resource
    private PayProdService payProdService;



    @Resource
    private SessionCacheService sessionCache;

    private int ORDER_TIME = 5;//5分钟订单过期

    /**
     * 提供给外部查询订单接口
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(PayInput payin) throws ServiceException, DaoException {
        //1.查询商户
        XJsonInfo ret=new XJsonInfo(false);
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getR()==null||payin.getSign()==null){
            ret.setMsg("参数不完整，请核对查询参数");
            return ret;
        }
        PayBus bus = paybusService.queryByUuid(payin.getUid());
        if(bus==null){
            ret.setMsg("商户无效");
            return ret;
        }
        //2.校验sign签名是否正确
        //签名
        String csign = payin.MarkSign(bus.getSignKey());
        if(!csign.equals(payin.getSign())){
            ret.setMsg("签名错误");
            return ret;
        }

        PayLog log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
        if(log==null){
            ret.setMsg("没有此订单，请确认订单ID是否正确");
            return ret;
        }
        return new XJsonInfo().setData(log);
    }

    /**
     * 发起支付
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/create")
    public XJsonInfo create(PayInput payin) throws Exception {
        XJsonInfo ret=new XJsonInfo(false);
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getPrice()==null||payin.getSign()==null||payin.getPay_type()==null){
            ret.setCode(11);
            ret.setMsg("参数不完整，请核对相关参数是否正确");
            return ret;
        }
        //验证签名
        PayBus bus = paybusService.queryByUuid(payin.getUid());
        String csign = payin.MarkSign(bus.getSignKey());
        if(!csign.equals(payin.getSign())){
            ret.setCode(12);
            ret.setMsg("签名错误");
            return ret;
        }

        //验证是否有足够的手续费





        //查询是否已有订单
        PayLog log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
        if(log!=null){
            //判断订单是否已过期
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            java.util.Date createtime = format.parse(log.getCreatetime());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE,-ORDER_TIME);
            if(cal.getTime().after(createtime)){
                ret.setCode(13);
                ret.setMsg("当前支付订单已过期，请重新创建订单");
                return ret;
            }
        }



        //如果不存在，则创建订单
        if(log==null){
            log = new PayLog();
            //1.查询是否有空闲的收款码
            List<PayProd> listprod = payProdService.queryByPrice(bus.getBusId(),payin.getPrice(),payin.getPay_type());
            if(listprod==null||listprod.size()==0){
                //2.如果没有定额的收款码，则使用非定额的
                List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),null);
                if(useinglog!=null && useinglog.size()>0){
                    ret.setCode(14);
                    ret.setMsg("当前没有可用的收款码，无法创建支付订单，请稍候再试");
                    return ret;
                }
                String payImgContent = null;
                //支付宝
                if(payin.getPay_type()==1){
                    payImgContent = bus.getPayImgContentZfb();
                }
                //微信
                if(payin.getPay_type()==2){
                    payImgContent = bus.getPayImgContentWx();
                }
                if(payImgContent==null||payImgContent.length()<5){
                    ret.setCode(14);
                    ret.setMsg("当前没有可用的收款码，无法创建支付订单，请稍候再试");
                    return ret;
                }
                //注入订单的收款信息
                log.setProdId(0L);
                log.setProdName("非定额收款码收款");
                log.setProdPrice(payin.getPrice());
                log.setPayImgPrice(payin.getPrice());
                log.setPayImgContent(payImgContent);
            }else{
                //2.如果有定额的，随机取一个定额的收款码
                PayProd sprod = null;
                List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),payin.getPrice());
                for(PayProd p:listprod){
                    boolean has= false;
                    for(PayLog l:useinglog){
                        if(p.getProdId().equals(l.getProdId())){
                            has =true;
                        }
                    }
                    if(!has){
                        sprod = p;
                        break;
                    }
                }
                //3.如果取不到有效的收款码。则返回
                if(sprod==null){
                    ret.setCode(14);
                    ret.setMsg("当前没有可用的收款码，无法创建支付订单，请稍候再试");
                    return ret;
                }
                //注入订单的收款信息
                log.setProdId(sprod.getProdId());
                log.setProdName(sprod.getProdName());
                log.setProdPrice(payin.getPrice());
                log.setPayImgPrice(sprod.getPayImgPrice());
                log.setPayImgContent(sprod.getPayImgContent());
            }
            //注入订单的其他信息
            log.setBusId(bus.getBusId());
            log.setBusAcc(bus.getBusAcc());
            log.setBusName(bus.getBusName());
            log.setBusType(bus.getBusType());
            log.setNotifyCount(0);
            log.setNotifyState(0);
            log.setOrderid(payin.getOrderid());
            log.setPayDemo(payin.getPay_demo());
            log.setPayExt1(payin.getPay_ext1());
            log.setPayExt2(payin.getPay_ext2());
            log.setPayName(payin.getPay_name());
            log.setPayType(payin.getPay_type());
            log.setReturnUrl(payin.getReturn_url());
            log.setProdPrice(payin.getPrice());
            log.setUid(payin.getUid());
            log.setPayState(0);
            paylogService.saveOrUpdate(log);
        }

        ret.setSuccess(true);
        ret.setCode(0);
        ret.setMsg("订单创建成功");
        return ret;
    }

    /**
     * 生成签名,只能生成自己的签名
     * @param payin
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/markSign")
    public XJsonInfo markSign(PayInput payin) throws ServiceException, DaoException {
        XJsonInfo ret=new XJsonInfo(false);
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getR()==null){
            ret.setMsg("参数不完整，请核对相关参数是否正确");
            return ret;
        }
        //权限控制，只能查看自己的
        SessionUser loginuser = sessionCache.getLoginUser();
        if(loginuser==null){
            ret.setMsg("对不起，您还未登陆，请登录后再生成签名");
            return ret;
        }
        PayBus bus = paybusService.get(loginuser.getUserid());

        if(bus==null){
            ret.setMsg("对不起，当前商户无效");
            return ret;
        }

        if(!bus.getUuid().equals(payin.getUid())){
            ret.setMsg("对不起，您只能生成自己的uid的订单签名");
            return ret;
        }
        ret.setSuccess(true);
        ret.setMsg(payin.MarkSign(bus.getSignKey()));
        return ret;
    }

}
