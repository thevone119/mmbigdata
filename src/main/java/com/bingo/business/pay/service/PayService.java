package com.bingo.business.pay.service;

import com.bingo.SpringUtil;
import com.bingo.action.BaseErrorController;
import com.bingo.business.pay.model.*;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.business.pay.repository.*;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.http.MyRequests;
import com.bingo.common.thread.MyThreadPool;
import com.bingo.common.utility.SecurityClass;
import com.bingo.common.utility.XJsonInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Administrator on 2018-07-26.
 * 1.统一对支付多个业务逻辑进行处理
 * 2.对多个表进行整合处理
 */
@Service
@Transactional
public class PayService {
    private static final Logger logger = LoggerFactory.getLogger(PayService.class);

    @Resource
    private PayBusChangeRepository payBusChangeRepository;
    @Resource
    private PayBusRepository payBusRepository;
    @Resource
    private PayLogRepository paylogRepository;


    @Resource
    private PayLogNotifyRepository payLogNotifyRepository;


    /**
     * 充值，消费接口
     * @param vo
     * @throws ServiceException
     * @throws DaoException
     */
    public void busChange(PayBusChange vo,Integer  busType,Long  busValidity) throws Exception {
        //不重复充值，消费
        if(payBusChangeRepository.getById(vo.getCid())!=null){
            return;
        }
        //1.对商户进行费用加减
        String hql = "update PayBus set eMoney=eMoney+? where busId=?";
        //如果有套餐类型和有效期，则同时更新套餐类型和有效期
        if(busType!=null && busValidity!=null){
            hql = "update PayBus set eMoney=eMoney+?,busType="+busType+",busValidity="+busValidity+" where busId=?";
        }
        payBusRepository.executeByHql(hql,new Object[]{vo.getEmoney(),vo.getBusId()});
        //2.保存消费记录
        payBusChangeRepository.saveOrUpdate(vo);
    }

    /**
     * 校验套餐的有效期
     * 如果已过期了，则自动续费
     * @param bus
     * @return
     */
    public boolean checkBusValidity(PayBus bus) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        //判断过期
        if(bus.getBusValidity()==null || bus.getBusValidity()<Integer.parseInt(format.format(new Date()))){
            if(bus.getAutoReFee()==0){
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,1);
            float refee=0.0f;//续费金额
            Long busValidity = new Long(format.format(calendar.getTime()));//续费期限
            Integer busType = bus.getAutoReFee();//续费后的商户类型
            refee = PayTaoCan.getPayTaoCanFee(busType);
            if(refee==0){
                return false;
            }
            //费用不足
            if(bus.geteMoney()<refee){
                return false;
            }

            //充值，消费
            PayBusChange change = new PayBusChange();
            change.setBusId(bus.getBusId());
            change.setCtype(2);
            change.setEmoney(-refee);
            change.setState(1);
            change.setDemo("套餐自动续费");

            this.busChange(change,busType,busValidity);
        }
        return true;
    }

    /**
     * 确认收款接口
     * @param paylog 支付订单
     * @param checkType 1:系统确认，2：手工确认,3:商户调用接口确认
     */
    public XJsonInfo checkPay(PayLog paylog, int checkType) throws Exception{
        XJsonInfo ret = new XJsonInfo(false);
        ret.setCode(1);

        if(paylog==null){
            ret.setCode(31);
            ret.setMsg("没有找到订单");
            return ret;
        }
        if(paylog.getPayState()==1){
            ret.setCode(32);
            ret.setMsg("订单状态已是已支付状态");
            return ret;
        }
        //计算扣减的费用
        PayBus bus = payBusRepository.getById(paylog.getBusId());
        //校验套餐
        if(!this.checkBusValidity(bus)){
            ret.setCode(21);
            ret.setMsg("商户套餐失效，无法完成支付.");
            return ret;
        }

        float refee=0.0f;//扣减的费用
        String demo = "手工确认收款，不扣费";
        if(checkType==1){
            if(paylog.getProdPrice()<1){
                demo = "低于1元的订单，不收手续费";
            }else{
                demo = "支付订单手续费";
                float serviceFeeFee = PayTaoCan.getPayTaoCanServiceFeeFee(bus.getBusType());
                refee = paylog.getProdPrice()*serviceFeeFee;
            }
        }
        if(checkType==2){
            demo = "手工确认收款，不扣费";
        }
        if(checkType==3){
            demo = "商户调用接口确认收款，不收手续费";
        }

        //扣减费用
        PayBusChange change = new PayBusChange();
        change.setCid(paylog.getRid());
        change.setBusId(bus.getBusId());
        change.setCtype(2);
        change.setEmoney(-refee);
        change.setState(1);
        change.setDemo(demo);
        change.setBizId(paylog.getRid());
        this.busChange(change,null,null);

        //2.设置为已收款
        paylogRepository.executeByHql("update PayLog set payState=1 where logId=?",new Long[]{paylog.getLogId()});

        //5.触发通知回调
        this.payNotifyThread(paylog,bus);

        ret.setSuccess(true);
        logger.info("checkPay end");
        return ret;
    }




    /**
     * 发起支付通知
     * 采用线程通知，避免通知IO线程阻塞
     * @param paylog
     * @param bus
     * @throws Exception
     */
    public void payNotifyThread(PayLog paylog, PayBus bus) throws Exception{
        paylog.setPayState(1);
        //采用多线程执行
        MyThreadPool pool = new MyThreadPool(2,20);
        pool.execute(() -> {
            //String name = Thread.currentThread().getName();
            try {
                SpringUtil.getBean(PayService.class).payNotify(paylog,bus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pool.shutdown();
    }

    /**
     * 发起支付通知
     * 采用线程通知，避免通知IO线程阻塞
     * @param paylog
     * @return
     */
    public XJsonInfo payNotify(PayLog paylog, PayBus bus) throws Exception{
        XJsonInfo ret = new XJsonInfo(false);
        if(paylog.getNotifyState()==1){
            ret.setMsg("已成功通知，无需重复通知");
            return ret;
        }
        if(paylog.getPayState()!=1){
            ret.setMsg("当前订单未支付，无法发起回调");
            return ret;
        }
        String uhql = "update PayLog set notifyState=?,notifyCount=notifyCount+1 where logId=? and notifyState!=1";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PayLogNotify notify = new PayLogNotify();
        notify.setLogId(paylog.getLogId());

        if(bus.getNotifyUrl()==null||bus.getNotifyUrl().length()<5|| bus.getNotifyUrl().toLowerCase().indexOf("http")==-1){
            notify.setNotifyState(2);
            notify.setNotifyResult("通知地址错误，发起通知失败");
            notify.setNotifyEndTime(format.format(new Date()));
            payLogNotifyRepository.saveOrUpdate(notify);
            paylogRepository.executeByHql(uhql,new Object[]{2,paylog.getLogId()});
            ret.setMsg("通知地址错误，发起通知失败");
            return ret;
        }
        try{
            String url = bus.getNotifyUrl();
            PayReturn pret = new PayReturn(paylog);
            pret.setRet_code(1);
            pret.setRet_msg("ok");
            MyRequests req = new MyRequests();
            String body = req.httpPost(url,pret.getPostData(bus.getSignKey()));

            if(body!=null && body.indexOf("SUCCESS")!=-1){
                //通知成功
                notify.setNotifyState(1);
                notify.setNotifyResult("支付通知成功:"+body);
                notify.setNotifyEndTime(format.format(new Date()));
                payLogNotifyRepository.saveOrUpdate(notify);
                paylogRepository.executeByHql(uhql,new Object[]{1,paylog.getLogId()});
                ret.setSuccess(true);
                ret.setMsg("支付通知成功:"+body);
                return ret;
            }else{
                //通知失败
                notify.setNotifyState(2);
                notify.setNotifyResult("支付通知失败:"+body);
                notify.setNotifyEndTime(format.format(new Date()));
                payLogNotifyRepository.saveOrUpdate(notify);
                paylogRepository.executeByHql(uhql,new Object[]{2,paylog.getLogId()});
                ret.setMsg("支付通知失败:"+body);
                return ret;
            }
        }catch (Exception e){
            e.printStackTrace();
            notify.setNotifyState(2);
            notify.setNotifyResult("发起通知异常:"+e.getMessage());
            notify.setNotifyEndTime(format.format(new Date()));
            payLogNotifyRepository.saveOrUpdate(notify);
            paylogRepository.executeByHql(uhql,new Object[]{2,paylog.getLogId()});
            ret.setMsg("发起通知异常:"+e.getMessage());
            return ret;
        }
        //ret.setSuccess(true);
        //return ret;
    }


    /**
     * 定时通知任务
     * 每分钟执行任务
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void payNotifyTask(){
        //1.查询所有待通知的记录
        logger.info("payNotifyTask start");
        String hql = "from PayLog where payState=1 and notifyState!=1 and notifyCount<5";
        List<PayLog> list = paylogRepository.query(hql);
        //采用多线程执行,5个线程进行通知调用
        MyThreadPool pool = new MyThreadPool(5,20);
        for(PayLog paylog:list){
            PayBus bus = payBusRepository.getById(paylog.getBusId());
            try{
                pool.execute(() -> {
                    //String name = Thread.currentThread().getName();
                    try {
                        SpringUtil.getBean(PayService.class).payNotify(paylog,bus);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        pool.shutdown();
        logger.info("payNotifyTask end");
    }

    /**
     * 定时续费
     * 每小时执行
     */
    @Scheduled(cron = "0 3 * * * ?")
    public void reChangeTask(){
        logger.info("reChangeTask start");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        //查询套餐过期的商户
        String hql = "from PayBus where autoReFee>0 and busValidity<?";
        List<PayBus> list = payBusRepository.query(hql,new Long[]{new Long(format.format(new Date()))});
        for(PayBus bus:list){
            try{
                checkBusValidity(bus);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("reChangeTask end");
    }



}
