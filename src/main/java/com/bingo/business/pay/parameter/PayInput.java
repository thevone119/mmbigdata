package com.bingo.business.pay.parameter;

import com.bingo.common.utility.SecurityClass;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-08-01.
 * 创建订单的表单参数
 * 支付订单内容录入参数，在这里定义
 */
public class PayInput {
    //针对订单创建
    private String uid;
    private String orderid;
    private String nonce_str;//随机字符串
    private Float price;
    private String pay_ext1;
    private String pay_ext2;
    private Integer pay_type; //1：支付宝；2：微信支付
    private String pay_name;
    private String pay_demo;
    private String return_url;

    private String sign;




    public PayInput(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPay_ext1() {
        return pay_ext1;
    }

    public void setPay_ext1(String pay_ext1) {
        this.pay_ext1 = pay_ext1;
    }

    public String getPay_ext2() {
        return pay_ext2;
    }

    public void setPay_ext2(String pay_ext2) {
        this.pay_ext2 = pay_ext2;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public void setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public String getPay_demo() {
        return pay_demo;
    }

    public void setPay_demo(String pay_demo) {
        this.pay_demo = pay_demo;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }



    /**
     * 对pay对象进行签名
     * @return
     */
    public String MarkSign(String sign_key) throws IllegalAccessException {
        //所有参数加入list
        List<String> kvlist = new ArrayList<String>();
        //采用反射获取所有的属性，并计算签名
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            //要设置属性可达，不然会抛出IllegalAccessException异常
            field.setAccessible(true);
            String key = field.getName();
            if(key.equals("sign")){
                continue;
            }
            Object value = field.get(this);
            if(value==null||value.toString().length()==0){
                continue;
            }
            kvlist.add(key+"="+value);
        }

        //对所有的参数进行排序
        kvlist.sort((h1, h2) -> h1.compareTo(h2));

        //组成相关的
        StringBuffer stringA =new  StringBuffer();
        for(String kv:kvlist){
            stringA.append(kv+"&");
        }
        stringA.append("sign_key="+sign_key);

        //签名
        String _sign =  SecurityClass.encryptMD5(stringA.toString()).toUpperCase();
        return _sign;
    }

    /**
     * 返回所有的参数map
     * @param sign_key
     * @return
     * @throws IllegalAccessException
     */
    public Map<String,String> getPostData(String sign_key) throws IllegalAccessException {
        Map<String,String> kvmap = new HashMap<String,String>();
        List<String> kvlist = new ArrayList<String>();
        //采用反射获取所有的属性，并计算签名

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            //要设置属性可达，不然会抛出IllegalAccessException异常
            field.setAccessible(true);
            String key = field.getName();
            if(key.equals("sign")){
                continue;
            }
            Object value = field.get(this);
            if(value==null||value.toString().length()==0){
                continue;
            }
            kvmap.put(key,value.toString());
            kvlist.add(key+"="+value);
        }
        //对所有的参数进行排序
        kvlist.sort((h1, h2) -> h1.compareTo(h2));
        //组成相关的
        StringBuffer stringA =new  StringBuffer();
        for(String kv:kvlist){
            stringA.append(kv+"&");
        }
        stringA.append("sign_key="+sign_key);
        //System.out.println(stringA.toString());
        //签名
        String _sign =  SecurityClass.encryptMD5(stringA.toString()).toUpperCase();
        kvmap.put("sign",_sign);
        return kvmap;
    }
}
