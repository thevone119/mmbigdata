package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.parameter.PayInput;
import com.bingo.business.pay.service.*;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.XJsonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private PayProdImgService payProdImgService;



    @Resource
    private SessionCacheService sessionCache;


    /**
     * 提供给外部查询订单接口
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @ResponseBody
    @RequestMapping("/query")
    public XJsonInfo query(PayInput payin) throws Exception {
        //1.查询商户
        XJsonInfo ret=new XJsonInfo(false);
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getNonce_str()==null||payin.getSign()==null){
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

    @ResponseBody
    @RequestMapping("/create")
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response, PayInput payin) throws Exception {
        XJsonInfo ret = new XJsonInfo(false);
        try{
            ret = createOrder(payin);
        }catch (Exception e){
            e.printStackTrace();
            ret.setSuccess(false);
            ret.setCode(-1);
            ret.setMsg("系统异常，请稍候再试");
        }
        //如果是json，则返回json
        if(payin.getReturn_type()!=null && payin.getReturn_type().toLowerCase().equals("json")){
            ModelAndView mv = new ModelAndView();
            mv.addObject("ret",ret);
            mv.setViewName("jsonView");
            return mv;
        }else{
            if(ret.getSuccess()){
                request.setAttribute("payin",payin);
                return new ModelAndView("/pay/flow/order_pay");
            }else{
                request.setAttribute("payin",payin);
                request.setAttribute("ret",ret);
                return new ModelAndView("/pay/flow/pay_error");
            }
        }
    }

    /**
     * 创建订单
     * 1.判断是否已存在
     * 2.如果已存在，则判断是否过期，没过期的直接续期
     * 3.如果已过期，则判断判断之前使用的收款码是否被使用，如果没被使用，则优先使用之前的收款码。
     * 4.如果已被使用，则使用新的收款码。
     *
     *
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    public XJsonInfo createOrder(PayInput payin) throws Exception {
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

        //验证是否有足够的手续费,大于1元才有手续费
        float sprice = 0.0f;
        if(payin.getPrice()>1){
            if(bus.getBusType()==null || bus.getBusType()==0){
                ret.setCode(15);
                ret.setMsg("对不起，您还未开通支付套餐，请先开通套餐后再进行支付");
                return ret;
            }

            sprice = payin.getPrice() * PayTaoCan.getPayTaoCanServiceFeeFee(bus.getBusType());
            if(bus.geteMoney()<sprice){
                ret.setCode(16);
                ret.setMsg("对不起，当前商户余额不足，不能进行支付");
                return ret;
            }
        }

        //查询是否已有订单
        PayLog log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
        if(log!=null){
            //判断订单状态，如果已支付的，直接返回
            if(log.getPayState()==1){
                ret.setCode(21);
                ret.setMsg("对不起，当前订单已支付完成，不能再次创建");
                return ret;
            }


            //判断订单是否已过期,如果还没过期，则重新锁定原收款码
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            java.util.Date updatetime = format.parse(log.getUpdatetime());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE,-bus.getPayTimeOut());
            //超过一天的订单，直接作废
            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_MONTH,-1);
            if(cal2.getTime().after(updatetime)){
                ret.setCode(22);
                ret.setMsg("对不起，当前订单已过期,请重新创建支付订单后进行支付");
                return ret;
            }


            if(cal.getTime().after(updatetime)){
                //过期
                if(log.getProdId()!=null && log.getProdId()>0){
                    //定额收款码
                    List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),bus.getPayTimeOut(),payin.getPrice());
                    if(useinglog==null||useinglog.size()==0){
                        log.setUpdatetime(format.format(new Date()));
                        paylogService.saveOrUpdate(log);
                    }else{
                        boolean hasuse = false;
                        for(PayLog l:useinglog){
                            if(log.getProdId().equals(l.getProdId())){
                                hasuse = true;
                                break;
                            }
                        }
                        if(hasuse){
                            List<PayProdImg> listprod = payProdImgService.listByPrice(bus.getBusId(),payin.getPrice(),payin.getPay_type());
                            PayProdImg sprod = null;
                            for(PayProdImg p:listprod){
                                boolean has= false;
                                for(PayLog l:useinglog){
                                    if(p.getCid().equals(l.getProdId())){
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
                            log.setProdId(sprod.getCid());
                            //log.setProdName(sprod.getProdName());
                            log.setProdPrice(payin.getPrice());
                            log.setPayImgPrice(sprod.getImgPrice());
                            log.setPayImgContent(sprod.getImgContent());
                            log.setUpdatetime(format.format(new Date()));
                            paylogService.saveOrUpdate(log);
                        }else{
                            log.setUpdatetime(format.format(new Date()));
                            paylogService.saveOrUpdate(log);
                        }
                    }
                }else{
                    //非定额的收款码
                    List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),bus.getPayTimeOut(),null);
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
                    log.setUpdatetime(format.format(new Date()));
                    paylogService.saveOrUpdate(log);
                }
            }else{
                //未过期
                log.setUpdatetime(format.format(new Date()));
                paylogService.saveOrUpdate(log);
            }
        }



        //如果不存在，则创建订单
        if(log==null){
            log = new PayLog();
            //1.查询是否有空闲的收款码
            List<PayProdImg> listprod = payProdImgService.listByPrice(bus.getBusId(),payin.getPrice(),payin.getPay_type());
            //List<PayProd> listprod = payProdService.queryByPrice(bus.getBusId(),payin.getPrice(),payin.getPay_type());
            if(listprod==null||listprod.size()==0){
                //2.如果没有定额的收款码，则使用非定额的
                List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),bus.getPayTimeOut(),null);
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
                PayProdImg sprod = null;
                List<PayLog> useinglog =  paylogService.queryByUseingLog(payin.getUid(),payin.getPay_type(),bus.getPayTimeOut(),payin.getPrice());
                for(PayProdImg p:listprod){
                    boolean has= false;
                    for(PayLog l:useinglog){
                        if(p.getCid().equals(l.getProdId())){
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
                log.setProdId(sprod.getCid());
                //log.setProdName(sprod.getProdName());
                log.setProdPrice(payin.getPrice());
                log.setPayImgPrice(sprod.getImgPrice());
                log.setPayImgContent(sprod.getImgContent());
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
    public XJsonInfo markSign(PayInput payin) throws Exception {
        XJsonInfo ret=new XJsonInfo(false);
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getNonce_str()==null){
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
