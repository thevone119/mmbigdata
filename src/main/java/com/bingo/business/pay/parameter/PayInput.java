package com.bingo.business.pay.parameter;

import com.bingo.common.utility.SecurityClass;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
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
    private String price;//这个用字符串，避免传输的时候，有些人用10,有些人用10.0等不同格式的，照常签名的错误
    private String pay_ext1;
    private String pay_ext2;
    private Integer pay_type; //1：支付宝；2：微信支付
    private String pay_name;
    private String pay_demo;
    private String return_url;

    //订单配送信息
    private String  userPhone;//用户手机号码
    private String  userAddress;//用户配送地址
    private String  userName;//用户姓名


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

        return new Float(price);
    }

    public void setPrice(String price) {
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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        System.out.println(stringA.toString());
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


    public static void main(String args[]) throws IllegalAccessException{
        PayInput pay = new PayInput();
        pay.setNonce_str("291794923930000001");
        pay.setOrderid("291794923930000001");
        pay.setPay_name("传奇充值");
        pay.setPay_demo("传奇充值_1");
        pay.setPay_type(1);
        pay.setPrice("10");
        pay.setUid("662b292a41104fb28a0aa9507f22121d");
        System.out.println(pay.MarkSign("5df29ca6f8ee4c12a85e8037dd56675b"));
        float f=10;
        System.out.println("f:"+f);
    }
}
