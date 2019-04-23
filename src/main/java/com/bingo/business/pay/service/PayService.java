package com.bingo.business.pay.service;

import com.bingo.SpringUtil;
import com.bingo.action.BaseErrorController;
import com.bingo.business.pay.model.*;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.business.pay.repository.*;
import com.bingo.business.sys.model.SysUser;
import com.bingo.business.sys.repository.SysUserRepository;
import com.bingo.business.sys.service.SysUserService;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.http.HttpReturn;
import com.bingo.common.http.MyOkHttp;
import com.bingo.common.http.MyRequests;
import com.bingo.common.service.RedisCacheService;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private PayAppNotificationService payappnotificationService;

    @Resource
    private PayBusChangeRepository payBusChangeRepository;
    @Resource
    private SysUserRepository sysUserRepository;
    @Resource
    private PayLogRepository paylogRepository;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private PayLogService paylogService;

    @Resource
    private PayLogNotifyRepository payLogNotifyRepository;

    @Resource
    private PaySubAccountService paySubAccountService;


    @Resource
    private RedisCacheService redis;

    private static Lock lock = new ReentrantLock();    //全局锁，一定要在try,catch中




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
        String hql = "update SysUser set eMoney=eMoney+? where userid=?";
        //如果有套餐类型和有效期，则同时更新套餐类型和有效期
        if(busType!=null && busValidity!=null){
            hql = "update SysUser set eMoney=eMoney+?,busType="+busType+",busValidity="+busValidity+" where userid=?";
        }
        sysUserRepository.executeByHql(hql,new Object[]{vo.getEmoney(),vo.getBusId()});
        //2.保存消费记录
        payBusChangeRepository.saveOrUpdate(vo);
    }


    /**
     * 更新子账号应收款数
     * @throws ServiceException
     * @throws DaoException
     */
    public void updateSubPlanAmout(long sid,Float payPlanAmout) throws Exception {
        if(sid==0){
            return;
        }
        //不重复充值，消费
        //1.对商户进行费用加减
        String hql = "update PaySubAccount set payPlanAmout=payPlanAmout+?,payPlanCount=payPlanCount+1 where sid=?";
        sysUserRepository.executeByHql(hql,new Object[]{payPlanAmout,sid});
    }

    /**
     * 更新子账号实收款数
     * @throws ServiceException
     * @throws DaoException
     */
    public void updateSubAmout(long sid,Float payAmout) throws Exception {
        if(sid==0){
            return;
        }
        //不重复充值，消费
        //1.对商户进行费用加减
        String hql = "update PaySubAccount set payAmout=payAmout+?,payCount=payCount+1 where sid=?";
        sysUserRepository.executeByHql(hql,new Object[]{payAmout,sid});
    }

    /**
     * 校验套餐的有效期
     * 如果已过期了，则自动续费
     * @param bus
     * @return
     */
    public boolean checkBusValidity(SysUser bus) throws Exception {
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
            change.setBusId(bus.getUserid());
            change.setCtype(2);
            change.setEmoney(-refee);
            change.setState(1);
            change.setDemo("套餐自动续费");

            this.busChange(change,busType,busValidity);
        }
        return true;
    }

    /**
     * 收到APP的通知，进行相关的处理
     * 对通知进行后续的匹配处理
     * 重点改造哦
     * 需要针对子账号进行改造
     * 1.支付宝通知 庭旺通过扫码向你付款0.05元
     * 2.支付宝通知 成功收款0.01元。享免费提现等更多专属服务，点击查看
     * 1.微信支付 [3条]微信支付: 微信支付收款0.01元
     * 2.微信支付 微信支付收款0.05元
     *title='notifymessage', text='<![CDATA[微信支付收款0.01元(朋友到店)]]></title> 	<des><![CDATA[收款金额￥0.01
     汇总今日第1笔收款，共计￥0.01
     备注收款成功，已存入零钱。点击可查看详情]]>'
     <![CDATA[[店员消息]收款到账0.05元]]></title> 	<des><![CDATA[收款金额￥0.05
     汇总今日第2笔收款, 共计￥0.10
     说明已存入店长黑米支付(**旺)的零钱]]></des>
     * @param vo
     * @return
     */
    public XJsonInfo doNotification(PayAppNotification vo){
        XJsonInfo ret = new XJsonInfo(false);
        //这里处理相关的匹配逻辑
        try{
            //解析通知内容--待编程
            float payImgPrice = 0;//支付金额
            int payType= 0;//支付渠道
            long subAid=0;//子账号ID
            //这个是通知
            if(vo.getNkey().startsWith("n_")){
                //1.支付宝1
                if(vo.getTitle().indexOf("支付宝通知")!=-1&&vo.getText().indexOf("成功收款")!=-1 && vo.getText().indexOf("元")!=-1){
                    payType=1;
                    int start = vo.getText().indexOf("成功收款")+"成功收款".length();
                    int end = vo.getText().indexOf("元",start);
                    String emoney =  vo.getText().substring(start,end);
                    payImgPrice = new Float(emoney);
                }
                //支付宝收款2
                if(vo.getTitle().indexOf("支付宝通知")!=-1&&vo.getText().indexOf("通过扫码向你付款")!=-1 && vo.getText().indexOf("元")!=-1){
                    payType=1;
                    int start = vo.getText().indexOf("通过扫码向你付款")+"通过扫码向你付款".length();
                    int end = vo.getText().indexOf("元",start);
                    String emoney =  vo.getText().substring(start,end);
                    payImgPrice = new Float(emoney);
                }


                //2.微信
                if(vo.getTitle().indexOf("微信支付")!=-1&&vo.getText().indexOf("微信支付收款")!=-1 && vo.getText().indexOf("元")!=-1){
                    payType=2;
                    int start = vo.getText().indexOf("微信支付收款")+"微信支付收款".length();
                    int end = vo.getText().indexOf("元",start);
                    String emoney =  vo.getText().substring(start,end);
                    payImgPrice = new Float(emoney);
                }
            }
            //查询商户信息
            SysUser bus = sysUserService.queryByUuid(vo.getUid());
            if(bus==null){
                ret.setSuccess(false);
                ret.setCode(1);
                ret.setMsg("商户不存在");
                logger.info("商户不存在，商户UID:"+vo.getUid());
                return ret;
            }

            //这个是微信数据监控
            if(vo.getNkey().startsWith("wxm_")){
                //子号收款
                if(vo.getText().indexOf("存入店长")!=-1 && vo.getText().indexOf("元")!=-1){
                    payType=2;
                    int start = vo.getText().indexOf("收款到账")+"收款到账".length();
                    int end = vo.getText().indexOf("元",start);
                    String emoney =  vo.getText().substring(start,end);
                    payImgPrice = new Float(emoney);
                    //获取店长名称
                    start = vo.getText().indexOf("存入店长")+"存入店长".length();
                    end = vo.getText().indexOf("的",start);
                    String dz = vo.getText().substring(start,end);
                    if(dz!=null && dz.indexOf("(")!=-1){
                        dz = dz.substring(0,dz.indexOf("("));
                    }
                    //查询子账号
                    List<PaySubAccount> listsub = paySubAccountService.queryByAccount(bus.getUserid(),dz);
                    if(listsub==null||listsub.size()==0){
                        ret.setSuccess(false);
                        ret.setCode(1);
                        ret.setMsg("没有找到对应的子账号("+dz+")");
                        logger.info("没有找到对应的子账号("+dz+")");
                        return ret;
                    }else{
                        subAid = listsub.get(0).getSid();
                    }
                }else if(vo.getText().indexOf("微信支付收款")!=-1 && vo.getText().indexOf("元")!=-1){//主号收款
                    payType=2;
                    int start = vo.getText().indexOf("微信支付收款")+"微信支付收款".length();
                    int end = vo.getText().indexOf("元",start);
                    String emoney =  vo.getText().substring(start,end);
                    payImgPrice = new Float(emoney);
                }
            }


            logger.info("payImgPrice："+payImgPrice);

            if(payImgPrice==0||payType==0){
                ret.setSuccess(false);
                ret.setCode(1);
                ret.setMsg("解析APP通知错误");
                logger.info("解析APP通知错误:"+vo.toString());
                return ret;
            }

            //查询所有匹配的订单
            List<PayLog> listlog = paylogService.queryByUseingLog(bus.getUuid(),subAid,payType,bus.getPayTimeOut(),null,payImgPrice,vo.getPostTimeService());
            if(listlog==null||listlog.size()==0){
                ret.setSuccess(false);
                ret.setCode(1);
                ret.setMsg("没有找到匹配的订单");
                logger.info("没有找到匹配的订单:"+vo.toString());
                return ret;
            }
            //如果只有一笔，那么就是这笔成功支付了。
            if(listlog.size()==1){
                PayLog paylog = listlog.get(0);
                checkPay(bus,paylog,1);
                vo.setLogid(paylog.getLogId()+"");
                payappnotificationService.saveOrUpdate(vo);
                ret.setSuccess(true);
            }else if(listlog.size()>1){
                //如果存在多笔，则记录异常吧。先不处理
                ret.setSuccess(false);
                ret.setCode(1);
                ret.setMsg("ERROR-----错误。。。存在多笔相同金额的订单，系统出错啦");
                logger.info("ERROR-----错误。。。存在多笔相同金额的订单，系统出错啦:"+vo.toString());
                return ret;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 确认收款接口
     * @param paylog 支付订单
     * @param checkType 1:系统确认，2：手工确认,3:商户调用接口确认
     */
    public XJsonInfo checkPay(PayLog paylog, int checkType) throws Exception{
        return checkPay(null,paylog,checkType);
    }

    /**
     * 这个做个锁单的，避免重复订单重复扣费
     * @param bus
     * @param paylog
     * @param checkType
     * @return
     * @throws Exception
     */
    public XJsonInfo checkPay(SysUser bus,PayLog paylog, int checkType) throws Exception{
        XJsonInfo ret = new XJsonInfo(false);
        ret.setCode(1);
        if(paylog==null){
            ret.setCode(31);
            ret.setMsg("没有找到订单");
            return ret;
        }
        //10秒内，重复的，直接返回订单已经提交
        try{
            lock.lock();
            String lockkey = "lock_log"+paylog.getLogId();
            if(redis.get(lockkey)==null){
                redis.set(lockkey,1,10000,TimeUnit.SECONDS);
            }else{
                ret.setCode(32);
                ret.setMsg("订单重复确认");
                return ret;
            }
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return checkPayLock(bus,paylog,checkType);
    }

    /**
     * 确认收款接口
     * @param paylog 支付订单
     * @param checkType 1:系统确认，2：手工确认,3:商户调用接口确认
     */
    private XJsonInfo checkPayLock(SysUser bus,PayLog paylog, int checkType) throws Exception{
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
        if(bus==null){
            bus = sysUserRepository.getById(paylog.getBusId());
        }
        //校验套餐,目前套餐失效了，不影响收款，只是不能发起支付而已
        if(!this.checkBusValidity(bus)){
            //ret.setCode(21);
            //ret.setMsg("商户套餐失效，无法完成支付.");
            //return ret;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        float refee=0.0f;//扣减的费用
        String demo = "手工确认收款，不扣手续费";
        if(checkType==1){
            if(paylog.getProdPrice()<=1){
                demo = "低于1元的订单，不收手续费";
            }else{
                demo = "支付订单手续费";
                float serviceFeeFee = PayTaoCan.getPayTaoCanServiceFeeFee(bus.getBusType());
                refee = paylog.getProdPrice()*serviceFeeFee;
                //手续费最低是0.01,小于0.01按照0.01计算
                if(refee<0.01){
                    refee=0.01f;
                }
            }
            //试用期,3个月内免手续费
            if(bus.getCreatetime()!=null){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH,-3);
                if(format.parse(bus.getCreatetime()).getTime()>cal.getTime().getTime()){
                    //refee=0.0f;
                    //demo = "试用期内，支付手续费全免";
                }
            }
        }
        if(checkType==2){
            demo = "手工确认收款，不扣费";
        }
        if(checkType==3){
            demo = "商户调用接口确认收款，不收手续费";
        }

        //1.扣减费用
        PayBusChange change = new PayBusChange();
        change.setCid(paylog.getRid());
        change.setBusId(bus.getUserid());
        change.setCtype(2);
        change.setEmoney(-refee);
        change.setState(1);
        change.setDemo(demo);
        change.setBizId(paylog.getRid());
        this.busChange(change,null,null);

        //2.更新收款累计
        if(paylog.getSubAid()>0){
            updateSubAmout(paylog.getSubAid(),paylog.getPayImgPrice());
        }

        //2.设置为已收款
        paylogRepository.executeByHql("update PayLog set payState=1,payTime=? where logId=?",new Object[]{format.format(new Date()),paylog.getLogId()});

        //3.释放锁
        delMoneyLock(paylog.getBusId(),paylog.getSubAid(),paylog.getPayType(),paylog.getPayImgPrice());

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
    public void payNotifyThread(PayLog paylog, SysUser bus) throws Exception{
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
    public XJsonInfo payNotify(PayLog paylog, SysUser bus) throws Exception{
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
            //用okhttp发起请求
            MyOkHttp req = new MyOkHttp();
            HttpReturn httpret =  req.post(url,pret.getPostData(bus.getSignKey()));
            if(httpret.code==200){
                //通知成功
                notify.setNotifyState(1);
                notify.setNotifyResult("支付通知成功:"+httpret.body);
                notify.setNotifyEndTime(format.format(new Date()));
                payLogNotifyRepository.saveOrUpdate(notify);
                paylogRepository.executeByHql(uhql,new Object[]{1,paylog.getLogId()});
                ret.setSuccess(true);
                ret.setMsg("支付通知成功:"+httpret.body);
                return ret;
            }else{
                //通知失败
                notify.setNotifyState(2);
                notify.setNotifyResult("支付通知失败:"+httpret.body);
                notify.setNotifyEndTime(format.format(new Date()));
                payLogNotifyRepository.saveOrUpdate(notify);
                paylogRepository.executeByHql(uhql,new Object[]{2,paylog.getLogId()});
                ret.setMsg("支付通知失败:"+httpret.body);
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
        //超过30秒的才进行推送
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND,-30);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String hql = "from PayLog where payState=1 and notifyState!=1 and notifyCount<5 and payTime<'"+format.format(cal.getTime())+"'";
        List<PayLog> list = paylogRepository.query(hql);
        //采用多线程执行,5个线程进行通知调用
        MyThreadPool pool = new MyThreadPool(5,20);
        for(PayLog paylog:list){
            SysUser bus = sysUserRepository.getById(paylog.getBusId());
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
     * 每天4点执行
     */
    @Scheduled(cron = "0 3 4 * * ?")
    public void reChangeTask(){
        logger.info("reChangeTask start");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        //查询套餐过期的商户
        String hql = "from PayBus where autoReFee>0 and busValidity<?";
        List<SysUser> list = sysUserRepository.query(hql,new Long[]{new Long(format.format(new Date()))});
        for(SysUser bus:list){
            try{
                checkBusValidity(bus);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("reChangeTask end");
    }


    /**
     * 支付金额锁
     * 针对并发，进行相关的金额锁定
     */
    public synchronized void  putMoneyLock(Long uid,long subid,int payType,Float lockMoney,int lockMinutes){
        String key = "pl_"+uid+"_"+subid+"_"+payType+"_"+ String.format("%.2f", lockMoney);
        redis.set(key,1,lockMinutes);
    }

    //是否在锁定状态
    public boolean hasMoneyLock(Long uid,long subid,int payType,Float lockMoney){
        String key = "pl_"+uid+"_"+subid+"_"+payType+"_"+ String.format("%.2f", lockMoney);
        Object v = (Object)redis.get(key);
        if(v==null){
            return false;
        }
        return true;
    }

    //成功后，释放锁哦
    public void delMoneyLock(Long uid,long subid,int payType,Float lockMoney){
        String key = "pl_"+uid+"_"+subid+"_"+payType+"_"+ String.format("%.2f", lockMoney);
        redis.delete(key);
    }


}
