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
    private String name;

    private static List<PayTaoCan> list=new ArrayList<PayTaoCan>();

    public PayTaoCan(String name,int busType,float fee,float serviceFee){
        this.name = name;
        this.busType=busType;
        this.fee=fee;
        this.serviceFee=serviceFee;
    }

    static{
        list.add(new PayTaoCan("基础版",1,19.99f,0.003f));
        list.add(new PayTaoCan("高级版",2,39.99f,0.002f));
        list.add(new PayTaoCan("专业版",3,59.99f,0.001f));
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

    public static String getPayTaoCanName(int busType){
        for(PayTaoCan t:list){
            if(t.busType==busType){
                return t.name;
            }
        }
        return "";
    }


}
