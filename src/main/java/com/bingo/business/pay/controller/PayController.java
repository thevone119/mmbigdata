package com.bingo.business.pay.controller;

import com.bingo.business.pay.model.*;
import com.bingo.business.pay.parameter.PayInput;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.business.pay.service.*;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.model.SessionUser;
import com.bingo.common.service.RedisCacheService;
import com.bingo.common.service.SessionCacheService;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018-07-30.
 * 支付平台外部接口提供
 * 多列模式,共享属性
 */
@RestController
@RequestMapping("/payapi")
@Scope("prototype")
public class PayController {
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);

    //子账号轮训的放这里
    private static Map<Long,Integer> subMap = new HashMap<Long,Integer>();

    @Resource
    private PayLogService paylogService;

    @Resource
    private PayProdImgService payProdImgService;

    @Resource
    private PayProdService payProdService;


    @Resource
    private PayService payService;

    @Resource
    private SysUserService sysuserService;

    @Resource
    private PaySubAccountService paySubAccountService;




    @Resource
    private SessionCacheService sessionCache;



    //商户对象
    private SysUser bus=null;

    //支付日志对象
    private PayLog log=null;

    //格式化，这个也有线程安全问题，多列则没问题
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    @Resource
    private RedisCacheService redis;

    private static Lock lock = new ReentrantLock();    //全局锁，一定要在try,catch中
    /**
     * 提供给外部查询订单接口
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
    @RequestMapping("/query")
    public PayReturn query(PayInput payin) throws Exception {
        //1.查询商户
        PayReturn ret  =new PayReturn();
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getNonce_str()==null||payin.getSign()==null){
            ret.setRet_code(11);
            ret.setRet_msg("参数不完整，请核对相关参数是否正确");
            return ret;
        }
        bus = sysuserService.queryByUuid(payin.getUid());
        if(bus==null){
            ret.setRet_code(12);
            ret.setRet_msg("商户无效");
            return ret;
        }
        //2.校验sign签名是否正确
        //签名
        String csign = payin.MarkSign(bus.getSignKey());
        if(!csign.equals(payin.getSign())){
            ret.setRet_code(13);
            ret.setRet_msg("签名错误");
            return ret;
        }

        log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
        if(log==null){
            ret.setRet_code(31);
            ret.setRet_msg("没有此订单，请确认订单ID是否正确");
            return ret;
        }
        ret=new PayReturn(log);
        ret.setRet_code(1);
        ret.setRet_msg("ok");
        ret.reSetSign(bus.getSignKey());
        return ret;
    }

    /**
     * 收款确认接口，提供给外围系统进行收款确认
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/pay_check")
    public PayReturn pay_check(PayInput payin) throws Exception {
        //1.查询商户
        PayReturn ret  =new PayReturn();
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getNonce_str()==null||payin.getSign()==null){
            ret.setRet_code(11);
            ret.setRet_msg("参数不完整，请核对相关参数是否正确");
            return ret;
        }
        bus = sysuserService.queryByUuid(payin.getUid());
        if(bus==null){
            ret.setRet_code(12);
            ret.setRet_msg("商户无效");
            return ret;
        }
        //2.校验sign签名是否正确
        //签名
        String csign = payin.MarkSign(bus.getSignKey());
        if(!csign.equals(payin.getSign())){
            ret.setRet_code(13);
            ret.setRet_msg("签名错误");
            return ret;
        }
        log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
        XJsonInfo retj = payService.checkPay(log,3);
        if(retj.getSuccess()){
            ret.setRet_code(1);
            ret.setRet_msg("ok");
        }else{
            ret.setRet_code(retj.getCode());
            ret.setRet_msg(retj.getMsg());
        }
        return ret;
    }

    /**
     * 查询支付结果
     * @param pay_id 订单的rid
     * @return
     * @throws Exception
     */
    @RequestMapping("/getresult")
    public PayReturn query(String pay_id) throws Exception {
        PayReturn ret  =new PayReturn();
        log = paylogService.queryByRid(pay_id);
        if(log==null){
            ret.setRet_code(31);
            ret.setRet_msg("没有此订单，请确认订单ID是否正确");
            return ret;
        }
        ret=new PayReturn(log);
        ret.setRet_code(1);
        ret.setRet_msg("ok");
        //ret.reSetSign(bus.getSignKey());
        return ret;
    }

    /**
     *去到支付页面，支付平台的支付页面
     *
     * 做支付页面分享，转发给别人进行支付,可以复制支付页面给别人进行支付
     *
     * 支付平台的支付页面，将根据订单生产支付html页面
     * 此html页面将自动识别浏览器类型，如果是PC浏览器，则显示支付2维码提供支付
     * 如果是手机浏览器，则会自动识别微信，支付宝等进行相关的适配处理
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/pay_page")
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response, String pay_id,String nonce_str) throws Exception {
        PayReturn ret  =new PayReturn();
        ret.setRet_code(-1);
        ret.setPay_type(1);
        if(pay_id==null||nonce_str==null){
            ret.setRet_msg("参数错误");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
            //return "/pay/flow/pay_error";
        }
        log = paylogService.queryByRid(pay_id);
        bus = sysuserService.queryByUuid(log.getUid());
        ret  = new PayReturn(log);

        if(log.getPayState()==1){
            ret.setRet_code(32);
            ret.setRet_msg("当前订单已支付完成");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }

        //判断订单的状态
        //判断订单是否已过期,如果还没过期，则重新锁定原收款码
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.util.Date createtime = format.parse(log.getCreatetime());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,-bus.getPayTimeOut());
        //创建超过一天的订单，直接作废
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_MONTH,-1);
        if(cal2.getTime().after(createtime)){
            //过期
            ret.setRet_code(33);
            ret.setRet_msg("对不起，当前订单已过期,请重新创建支付订单后进行支付");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        //更新订单的收款码
        boolean updateimg = updateLogPayImg();
        if(!updateimg){
            ret.setRet_code(41);
            ret.setRet_msg("当前没有可用的收款码，无法创建支付订单，请稍候再试");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        paylogService.saveOrUpdate(log);
        //超时时间格式化
        Calendar tocal = Calendar.getInstance();
        tocal.add(Calendar.MINUTE,bus.getPayTimeOut());
        String timeoutfm = format2.format(tocal.getTime());
        //
        request.setAttribute("timeoutfm",timeoutfm);
        request.setAttribute("bus",bus);
        request.setAttribute("log",log);
        request.setAttribute("ret",new PayReturn(log));
        return new ModelAndView("/pay/flow/order_pay");
        //return "/pay/flow/order_pay";
    }

    /**
     * 创建订单，并且去到支付页面
     * @param payin
     * @return
     * @throws Exception
     */
    @RequestMapping("/create_pay")
    public ModelAndView createAndPay(HttpServletRequest request, HttpServletResponse response, PayInput payin) throws Exception {
        PayReturn ret = createOrder(payin);
        if(ret.getRet_code()==1){
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //超时时间格式化
            Calendar tocal = Calendar.getInstance();
            tocal.add(Calendar.MINUTE,bus.getPayTimeOut());
            String timeoutfm = format2.format(tocal.getTime());
            request.setAttribute("timeoutfm",timeoutfm);
            request.setAttribute("bus",bus);
            request.setAttribute("log",log);
            request.setAttribute("ret",new PayReturn(log));
            return new ModelAndView("/pay/flow/order_pay");
        }else{
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
            //return "/pay/flow/pay_error";
        }
    }

    /**创建订单
     * 加锁，同一个商户并发调用创建订单接口，要加锁，
     * 避免生成了重复的订单，包括重复金额的订单
     * @param payin
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/create")
    public PayReturn createOrder(PayInput payin) throws Exception {
        PayReturn ret  =new PayReturn();
        if(payin.getUid()==null||payin.getOrderid()==null||payin.getPrice()==null||payin.getSign()==null||payin.getPay_type()==null){
            ret.setRet_code(11);
            ret.setRet_msg("参数不完整，请核对相关参数是否正确");
            return ret;
        }
        //同一商户，5秒内重复的，都锁
        boolean islock = false;
        String key = "l_uid_"+payin.getUid();
        try{
            lock.lock();
            if(redis.get(key)==null){
                redis.set(key,1,5000, TimeUnit.SECONDS);
            }else{
                islock =true;
                redis.set(key,1,5000, TimeUnit.SECONDS);
            }
        }catch (Exception e){

        }finally {
            lock.unlock();
        }

        try{
            if(islock){
                lock.lock();
            }
            return createOrderLock(payin);

        }catch (Exception e){
            ret.setRet_code(11);
            ret.setRet_msg("未知错误");
            return ret;
        }finally {
            if(islock){
                lock.unlock();
            }
        }
    }

    /**
     * 创建订单,返回json
     * 1.判断是否已存在
     * 2.如果已存在，则判断是否过期，没过期的直接续期
     * 3.如果已过期，则判断判断之前使用的收款码是否被使用，如果没被使用，则优先使用之前的收款码。
     * 4.如果已被使用，则使用新的收款码。
     * 这个要考虑加锁，避免并发下创建了相同金额的订单哦
     * @return
     */
    private PayReturn createOrderLock(PayInput payin) throws Exception {
        PayReturn ret  =new PayReturn();
        try{
            if(payin.getUid()==null||payin.getOrderid()==null||payin.getPrice()==null||payin.getSign()==null||payin.getPay_type()==null){
                ret.setRet_code(11);
                ret.setRet_msg("参数不完整，请核对相关参数是否正确");
                return ret;
            }
            //验证签名
            bus = sysuserService.queryByUuid(payin.getUid());
            //先注入返回地址，这样出错可以直接返回这个地址
            ret.setReturn_url(bus.getGobackUrl());
            ret.setPay_type(payin.getPay_type());
            if(payin.getReturn_url()!=null && payin.getReturn_url().length()>5){
                ret.setReturn_url(payin.getReturn_url());
            }

            String csign = payin.MarkSign(bus.getSignKey());
            if(!csign.equals(payin.getSign())){
                ret.setRet_code(12);
                ret.setRet_msg("签名错误");
                return ret;
            }

            //验证是否有足够的手续费,大于1元才有手续费
            float sprice = 0.0f;
            if(payin.getPrice()>1){
                if(bus.getBusType()==0){
                    ret.setRet_code(21);
                    ret.setRet_msg("对不起，您还未开通支付套餐，请先开通套餐后再进行支付");
                    return ret;
                }
                float serviceFeeFee = PayTaoCan.getPayTaoCanServiceFeeFee(bus.getBusType());
                //代理商户的，可以指定费率的
                if(bus.getBusType()==4){
                    serviceFeeFee = bus.getBusRate()/1000.0f;
                }
                sprice = payin.getPrice() * serviceFeeFee;
                //手续费最低是0.01,小于0.01按照0.01计算
                if(sprice<0.01){
                    sprice=0.01f;
                }

                //试用期,3个月内免手续费（废弃）
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH,-3);
                if(format.parse(bus.getCreatetime()).getTime()>cal.getTime().getTime()){
                    //sprice=0.0f;
                }

                if(bus.geteMoney()<sprice){
                    ret.setRet_code(22);
                    ret.setRet_msg("对不起，当前商户余额不足，不能进行支付");
                    return ret;
                }
            }

            //查询是否已有订单
            log = paylogService.queryByUidOrderid(payin.getUid(),payin.getOrderid());
            //已有订单，判断订单的状态，有效期等
            if(log!=null) {
                //判断订单状态，如果已支付的，直接返回
                if (log.getPayState() == 1) {
                    ret.setRet_code(32);
                    ret.setRet_msg("对不起，当前订单已支付完成，不能再次创建");
                    return ret;
                }
                java.util.Date createtime = format.parse(log.getCreatetime());
                //创建超过一天的订单，直接作废
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DAY_OF_MONTH, -1);
                if (cal2.getTime().after(createtime)) {
                    ret.setRet_code(33);
                    ret.setRet_msg("对不起，当前订单已过期,请重新创建支付订单后进行支付");
                    return ret;
                }
            }

            //新订单,注入订单的相关信息
            if(log==null){
                log = new PayLog();
                //注入订单的其他信息
                log.setBusId(bus.getUserid());
                log.setBusAcc(bus.getUseracc());
                log.setBusName(bus.getNikename());
                log.setBusType(bus.getBusType());
                log.setNotifyCount(0);
                log.setNotifyState(0);
                log.setPayState(0);
            }

            //不管新旧订单，这些内容都可以更改
            log.setOrderid(payin.getOrderid());
            //返回地址
            log.setReturnUrl(bus.getGobackUrl());
            if(payin.getReturn_url()!=null && payin.getReturn_url().length()>5){
                log.setReturnUrl(payin.getReturn_url());
            }
            log.setProdPrice(payin.getPrice());
            log.setUid(payin.getUid());
            log.setPayDemo(payin.getPay_demo());
            log.setPayExt1(payin.getPay_ext1());
            log.setPayExt2(payin.getPay_ext2());
            log.setPayName(payin.getPay_name());
            log.setPayType(payin.getPay_type());
            log.setUserAddress(payin.getUserAddress());
            log.setUserName(payin.getUserName());
            log.setUserPhone(payin.getUserPhone());

            //更新订单的收款码
            boolean updateimg = updateLogPayImg();
            if(!updateimg){
                ret.setRet_code(41);
                ret.setRet_msg("当前没有可用的收款码，无法创建支付订单，请稍候再试");
                return ret;
            }

            paylogService.saveOrUpdate(log);
            ret=new PayReturn(log);
            ret.setRet_code(1);
            ret.setRet_msg("ok");
            ret.reSetSign(bus.getSignKey());
        }catch (Exception e){
            e.printStackTrace();
            ret.setRet_code(-1);
            ret.setRet_msg("系统异常");
        }
        return ret;
    }


    /**
     * 更新订单的收款码，如果更新成功，直接锁单哦
     * 这里要加锁，锁商户号，就是同一个商户号，不允许对此方法进行并发，避免出现同样金额的订单
     *
     * @return
     * @throws Exception
     */
    private boolean  updateLogPayImg() throws Exception {
        boolean rett = updateLogPayImg2();
        if(rett){
            //这里直接锁住订单哦
            payService.putMoneyLock(log.getBusId(),log.getSubAid(),log.getPayType(),log.getPayImgPrice(),bus.getPayTimeOut()+1);
        }
        return rett;
    }


    /**
     * 重点主要逻辑哟
     * 更新订单的收款码
     * 重点测试哦
     *
     * @return
     */
    private boolean  updateLogPayImg2() throws Exception {
        java.util.Date updatetime = format.parse(log.getUpdatetime());
        Calendar cal = Calendar.getInstance();
        //锁多30秒
        cal.add(Calendar.SECOND,-(bus.getPayTimeOut()*60)-30);
        //如果已有收款码，并且未过期，则直接更新收款码使用期限即可
        if(log.getPayImgPrice()!=null && log.getPayImgPrice()>0){
            if(cal.getTime().before(updatetime)){
                log.setUpdatetime(format.format(new Date()));
                return true;
            }
        }

        //在用的订单
        //List<PayLog> useinglog =  paylogService.queryByUseingLog(log.getUid(),log.getPayType(),bus.getPayTimeOut(),null);

        //这里要判断子账号哦
        //当前收款金额的是否被使用，没有被使用，则直接使用当前的
        if(log.getPayImgPrice()!=null && log.getPayImgPrice()>0){
            if(!payService.hasMoneyLock(log.getBusId(),log.getSubAid(),log.getPayType(),log.getPayImgPrice())){
                //payService.putMoneyLock(log.getBusId(),log.getSubAid(),log.getPayType(),log.getPayImgPrice());
                log.setUpdatetime(format.format(new Date()));
                return true;
            }
        }


        //查询有效的子账号
        List<PaySubAccount> listSubAccount = paySubAccountService.listValidSubAccount(bus.getUserid(),log.getPayType());
        //如果有子账号，那么优先使用子账号哦
        //采用轮训的方式进行
        //1.使用子账号固码收款
        if(listSubAccount!=null&&listSubAccount.size()>0){
            Integer currIdx = subMap.get(bus.getUserid());
            if(currIdx==null){
                currIdx=0;
            }
            for(int i=0;i<listSubAccount.size();i++){
                currIdx++;
                if(currIdx>=listSubAccount.size()){
                    currIdx=0;
                }
                PaySubAccount sub = listSubAccount.get(currIdx);
                if(!sub.getPayType().equals(log.getPayType())){
                    continue;
                }
                List<PayProdImg> pimgList = payProdImgService.listBySubPrice(bus.getUserid(),sub.getSid(),log.getPayType(),log.getProdPrice()-bus.getMaxLowerMoney(),log.getProdPrice()+bus.getMaxUpperMoney());
                //判断固码价格是否可使用
                if(pimgList!=null && pimgList.size()>0){
                    for(PayProdImg pi:pimgList){
                        if(!payService.hasMoneyLock(log.getBusId(),pi.getSubAid(),log.getPayType(),pi.getImgPrice())){
                            subMap.put(bus.getUserid(),currIdx);
                            //定额
                            //注入订单的收款信息
                            log.setProdImgId(pi.getCid());
                            log.setProdImgName("子账户固码收款");
                            //log.setProdPrice(payin.getPrice());
                            log.setSubAid(sub.getSid());
                            log.setSubAccount(sub.getSubaccount());
                            log.setPayImgPrice(pi.getImgPrice());
                            log.setPayImgContent(pi.getImgContent());
                            log.setUpdatetime(format.format(new Date()));

                            payService.updateSubPlanAmout(log.getSubAid(),log.getPayImgPrice());
                            return true;
                        }
                    }
                }
            }
        }

        //2.使用子账号通码收款
        if(listSubAccount!=null&&listSubAccount.size()>0 ){
            Integer currIdx = subMap.get(bus.getUserid());
            if(currIdx==null){
                currIdx=0;
            }
            for(int i=0;i<listSubAccount.size();i++){
                currIdx++;
                if(currIdx>=listSubAccount.size()){
                    currIdx=0;
                }
                PaySubAccount sub = listSubAccount.get(currIdx);
                if(!sub.getPayType().equals(log.getPayType())){
                    continue;
                }
                if(sub.getPayImgContent()==null ||sub.getPayImgContent().length()<5){
                    continue;
                }
                //通码取金额（先从当前金额向上取）
                boolean machcomm = false;//是否匹配到通码
                Float imgPrice = log.getProdPrice();
                for(int k=0;k<50;k++){
                    imgPrice = imgPrice+k*0.01f;
                    if(imgPrice>log.getProdPrice()+bus.getMaxUpperMoney()){
                        break;
                    }
                    if(!payService.hasMoneyLock(log.getBusId(),sub.getSid(),log.getPayType(),imgPrice)){
                        machcomm = true;
                        break;
                    }
                }
                //没有匹配到，则向下取
                if(!machcomm){
                    imgPrice = log.getProdPrice();
                    for(int k=0;k<50;k++){
                        imgPrice = imgPrice-k*0.01f;
                        if(imgPrice<=0){
                            break;
                        }
                        if(imgPrice<log.getProdPrice()-bus.getMaxLowerMoney()){
                            break;
                        }
                        if(!payService.hasMoneyLock(log.getBusId(),sub.getSid(),log.getPayType(),imgPrice)){
                            machcomm = true;
                            break;
                        }
                    }
                }
                //如果匹配到，则使用当前的子账号通码
                if(machcomm){
                    subMap.put(bus.getUserid(),currIdx);
                    //注入订单的收款信息
                    log.setProdImgId(0L);
                    log.setProdImgName("子账户通码收款");
                    log.setPayImgPrice(imgPrice);
                    log.setSubAid(sub.getSid());
                    log.setSubAccount(sub.getSubaccount());
                    log.setPayImgContent(sub.getPayImgContent());
                    log.setUpdatetime(format.format(new Date()));

                    payService.updateSubPlanAmout(log.getSubAid(),log.getPayImgPrice());
                    return true;
                }
            }
        }

        //2.使用子账号PID(针对支付宝)进行收款
        if(listSubAccount!=null&&listSubAccount.size()>0 ){
            Integer currIdx = subMap.get(bus.getUserid());
            if(currIdx==null){
                currIdx=0;
            }
            for(int i=0;i<listSubAccount.size();i++){
                currIdx++;
                if(currIdx>=listSubAccount.size()){
                    currIdx=0;
                }
                PaySubAccount sub = listSubAccount.get(currIdx);
                if(!sub.getPayType().equals(log.getPayType())){
                    continue;
                }
                //支付宝模式
                if(sub.getPayType()!=1){
                    continue;
                }
                //通码取金额（先从当前金额向上取）
                boolean machcomm = false;//是否匹配到通码
                Float imgPrice = log.getProdPrice();

                for(int k=0;k<50;k++){
                    imgPrice = imgPrice+k*0.01f;
                    if(imgPrice>log.getProdPrice()+bus.getMaxUpperMoney()){
                        break;
                    }
                    if(!payService.hasMoneyLock(log.getBusId(),sub.getSid(),log.getPayType(),imgPrice)){
                        machcomm = true;
                        break;
                    }
                }
                //没有匹配到，则向下取
                if(!machcomm){
                    imgPrice = log.getProdPrice();
                    for(int k=0;k<50;k++){
                        imgPrice = imgPrice-k*0.01f;
                        if(imgPrice<=0){
                            break;
                        }
                        if(imgPrice<log.getProdPrice()-bus.getMaxLowerMoney()){
                            break;
                        }
                        if(!payService.hasMoneyLock(log.getBusId(),sub.getSid(),log.getPayType(),imgPrice)){
                            machcomm = true;
                            break;
                        }
                    }
                }
                //如果匹配到，则使用当前的子账号通码
                if(machcomm){
                    subMap.put(bus.getUserid(),currIdx);
                    //注入订单的收款信息
                    log.setProdImgId(1L);
                    log.setProdImgName("子账户PID收款");
                    log.setPayImgPrice(imgPrice);
                    log.setSubAid(sub.getSid());
                    log.setSubAccount(sub.getSubaccount());
                    log.setPayImgContent(sub.getZFBPIDContent(imgPrice,log.getOrderid()));
                    log.setUpdatetime(format.format(new Date()));

                    payService.updateSubPlanAmout(log.getSubAid(),log.getPayImgPrice());
                    return true;
                }
            }
        }


        //3.采用主账号固码
        List<PayProdImg> pimgList = payProdImgService.listBySubPrice(bus.getUserid(),0L,log.getPayType(),log.getProdPrice()-bus.getMaxLowerMoney(),log.getProdPrice()+bus.getMaxUpperMoney());
        //3.1判断固码价格是否可使用
        if(pimgList!=null && pimgList.size()>0){
            for(PayProdImg pi:pimgList){
                if(!payService.hasMoneyLock(log.getBusId(),pi.getSubAid(),log.getPayType(),pi.getImgPrice())){
                    //定额
                    //注入订单的收款信息
                    log.setProdImgId(pi.getCid());
                    log.setProdImgName("主账户固码收款");
                    //log.setProdPrice(payin.getPrice());
                    log.setSubAid(pi.getSubAid());
                    //log.setSubAccount();
                    log.setPayImgPrice(pi.getImgPrice());
                    log.setPayImgContent(pi.getImgContent());
                    log.setUpdatetime(format.format(new Date()));
                    return true;
                }
            }
        }

        //4.最后才采用主账号通码
        String payImgContent = null;
        //支付宝
        if(log.getPayType()==1){
            payImgContent = bus.getPayImgContentZfb();
        }
        //微信
        if(log.getPayType()==2){
            payImgContent = bus.getPayImgContentWx();
        }
        if(payImgContent==null||payImgContent.length()<5){
            return false;
        }
        //通码取金额（先从当前金额向上取）
        boolean machcomm = false;//是否匹配到通码
        Float imgPrice = log.getProdPrice();
        for(int k=0;k<50;k++){
            imgPrice = imgPrice+k*0.01f;
            if(imgPrice>log.getProdPrice()+bus.getMaxUpperMoney()){
                break;
            }
            if(!payService.hasMoneyLock(log.getBusId(),0L,log.getPayType(),imgPrice)){
                machcomm = true;
                break;
            }
        }
        //没有匹配到，则向下取
        if(!machcomm){
            imgPrice = log.getProdPrice();
            for(int k=0;k<50;k++){
                imgPrice = imgPrice-k*0.01f;
                if(imgPrice<=0){
                    break;
                }
                if(imgPrice<log.getProdPrice()-bus.getMaxLowerMoney()){
                    break;
                }
                if(!payService.hasMoneyLock(log.getBusId(),0L,log.getPayType(),imgPrice)){
                    machcomm = true;
                    break;
                }
            }
        }
        if(machcomm){
            //注入订单的收款信息
            log.setProdImgId(0L);
            log.setProdImgName("主账户通码收款");
            log.setSubAid(0);
            log.setPayImgPrice(imgPrice);
            log.setPayImgContent(payImgContent);
            log.setUpdatetime(format.format(new Date()));
            return true;
        }
        return false;
    }


    /**
     * 生成签名,只能生成自己的签名
     * @param payin
     * @return
     * @throws ServiceException
     * @throws DaoException
     */
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
        bus = sysuserService.get(loginuser.getUserid());

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



    /**
     * 直接在线查看二维码
     * @param request
     * @param response
     * @throws IOException
     * @throws DaoException
     */
    @RequestMapping("/showImage")
    public void showImage(HttpServletRequest request, HttpServletResponse response, String url) throws IOException, DaoException {
        OutputStream out = null;
        try{
            out = response.getOutputStream();
            QRCodeUtils.createQRcode(url,out);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(out!=null) out.close();
            }catch(Exception e){}
        }
    }

    /**
     * 创建快捷商品支付订单
     * @param prodid,快捷支付商品ID
     * @param orderid,订单号
     * @return
     * @throws Exception
     */
    @RequestMapping("/create_quick")
    public ModelAndView create_quick(HttpServletRequest request, HttpServletResponse response, String prodid,String orderid) throws Exception {
        PayReturn ret = new PayReturn();
        if(orderid==null||orderid.length()==0){
            orderid=System.currentTimeMillis()+"";
        }
        //查询快捷支付商品
        PayProd prod = payProdService.getByRid(prodid);
        if(prod==null||prod.getState()==0){
            ret.setRet_msg("快捷支付商品不存在，无法创建订单");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        request.setAttribute("prod",prod);
        request.setAttribute("orderid",orderid);
        //直接去到收货地址，支付方式选择的页面
        return new ModelAndView("/pay/flow/order_address");
    }

    /**
     * 快捷支付提交,提交后，去到支付页面
     * @param prodid,快捷支付商品ID
     * @param orderid,订单号
     * @return
     * @throws Exception
     */
    @RequestMapping("/submit_quick")
    public ModelAndView submit_quick(HttpServletRequest request, HttpServletResponse response, String prodid,String orderid,String userName,String userPhone,String userAddress,Integer pay_type) throws Exception {
        PayReturn ret = new PayReturn();
        if(orderid==null||orderid.length()==0){
            orderid=System.currentTimeMillis()+"";
        }
        if(pay_type<1||pay_type>2){
            ret.setRet_msg("参数错误");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        //查询快捷支付商品
        PayProd prod = payProdService.getByRid(prodid);
        if(prod==null||prod.getState()==0){
            ret.setRet_msg("快捷支付商品不存在，无法创建订单");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        //查询商户
        bus = sysuserService.get(prod.getBusId());
        if(bus==null){
            ret.setRet_msg("商户过期，或已失效");
            request.setAttribute("ret",ret);
            return new ModelAndView("/pay/flow/pay_error");
        }
        //创建支付订单
        PayInput input = new PayInput();
        input.setPrice(prod.getProdPrice()+"");
        input.setPay_type(pay_type);
        input.setPay_name(prod.getProdName());
        input.setOrderid(orderid);
        input.setUid(bus.getUuid());
        input.setPay_demo("快捷商品支付");
        input.setUserAddress(userAddress);
        input.setUserName(userName);
        input.setUserPhone(userPhone);
        input.setNonce_str(System.currentTimeMillis()+"");
        //签名
        input.setSign(input.MarkSign(bus.getSignKey()));

        //直接去到支付页面
        return createAndPay(request,response,input);
    }


}
