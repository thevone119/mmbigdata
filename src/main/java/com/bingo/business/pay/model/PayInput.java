package com.bingo.business.pay.model;

import com.bingo.common.utility.SecurityClass;

/**
 * Created by Administrator on 2018-08-01.
 * 支付订单内容录入参数，在这里定义
 */
public class PayInput {
    private String uid;
    private String orderid;
    private String r;//随机字符串
    private Float price;
    private String pay_ext1;
    private String pay_ext2;
    private Integer pay_type;
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

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    /**
     * 对pay对象进行签名
     * @return
     */
    public String MarkSign(String signKey){
        //签名
        StringBuffer signstr = new StringBuffer();
        signstr.append(uid+"");
        signstr.append(orderid+"");
        signstr.append(r+"");
        signstr.append(signKey);
        String _sign =  SecurityClass.encryptMD5(signstr.toString());
        return _sign;
    }
}
