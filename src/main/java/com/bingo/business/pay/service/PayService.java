package com.bingo.business.pay.service;

import com.bingo.business.pay.model.PayBus;
import com.bingo.business.pay.model.PayBusChange;
import com.bingo.business.pay.model.PayLog;
import com.bingo.business.pay.model.PayTaoCan;
import com.bingo.business.pay.repository.PayBusChangeRepository;
import com.bingo.business.pay.repository.PayBusRepository;
import com.bingo.business.pay.repository.PayLogRepository;
import com.bingo.business.pay.repository.PayProdRepository;
import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018-07-26.
 * 1.统一对支付多个业务逻辑进行处理
 * 2.对多个表进行整合处理
 */
@Service
@Transactional
public class PayService {
    @Resource
    private PayBusChangeRepository payBusChangeRepository;
    @Resource
    private PayBusRepository payBusRepository;
    @Resource
    private PayLogRepository paylogRepository;
    @Resource
    private PayProdRepository payProdRepository;


    /**
     * 充值，消费接口
     * @param vo
     * @throws ServiceException
     * @throws DaoException
     */
    public void busChange(PayBusChange vo,Integer  busType,Long  busValidity) throws Exception {
        //1.对商户进行费用加减
        String hql = "update PayBus set eMoney+=? where busId=?";
        //如果有套餐类型和有效期，则同时更新套餐类型和有效期
        if(busType!=null && busValidity!=null){
            hql = "update PayBus set eMoney+=?,busType="+busType+",busValidity="+busValidity+" where busId=?";
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
        if(bus.getBusValidity()<Integer.parseInt(format.format(new Date()))){
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
     * @param checkType 1:系统确认，2：手工确认
     */
    public XJsonInfo checkPay(PayLog paylog, int checkType) throws Exception{
        XJsonInfo ret = new XJsonInfo(false);
        ret.setCode(1);

        if(paylog==null){
            ret.setMsg("没有找到订单");
            return ret;
        }
        if(paylog.getPayState()==1){
            ret.setCode(2);
            ret.setMsg("订单状态已是已支付状态");
            return ret;
        }
        //计算扣减的费用
        PayBus bus = payBusRepository.getById(paylog.getBusId());
        //校验套餐
        if(this.checkBusValidity(bus)){
            ret.setCode(3);
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

        //扣减费用
        PayBusChange change = new PayBusChange();
        change.setBusId(bus.getBusId());
        change.setCtype(2);
        change.setEmoney(-refee);
        change.setState(1);
        change.setDemo(demo);
        change.setLogId(paylog.getLogId());
        this.busChange(change,null,null);

        //2.设置为已收款
        paylogRepository.executeByHql("update PayLog set payState=1 where logId=?",new Long[]{paylog.getLogId()});

        //5.触发通知回调
        this.payNotify(paylog);

        ret.setSuccess(true);
        return ret;
    }

    /**
     * 发起支付通知
     * 采用线程通知，避免通知IO线程阻塞
     * @param paylog
     * @return
     */
    public XJsonInfo payNotify(PayLog paylog){
        XJsonInfo ret = new XJsonInfo(false);



        return ret;
    }


}
