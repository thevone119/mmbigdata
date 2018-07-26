package com.bingo.business.pay.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-07-26.
 * 支付平台套餐配置
 */
public class PayTaoCan {
    private int busType;//套餐ID
    private float fee;//套餐价格
    private float serviceFee;//手续费

    private static List<PayTaoCan> list=new ArrayList<PayTaoCan>();

    public PayTaoCan(int busType,float fee,float serviceFee){
        this.busType=busType;
        this.fee=fee;
        this.serviceFee=serviceFee;
    }

    static{
        list.add(new PayTaoCan(1,19.9f,0.003f));
        list.add(new PayTaoCan(2,39.9f,0.002f));
        list.add(new PayTaoCan(3,59.9f,0.001f));
    }

    public static float getPayTaoCanFee(int busType){
        for(PayTaoCan t:list){
            if(t.busType==busType){
                return t.fee;
            }
        }
        return 0f;
    }

    public static float getPayTaoCanServiceFeeFee(int busType){
        for(PayTaoCan t:list){
            if(t.busType==busType){
                return t.serviceFee;
            }
        }
        return 0f;
    }

}
