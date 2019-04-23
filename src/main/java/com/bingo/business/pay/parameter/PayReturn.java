package com.bingo.business.pay.parameter;

import com.bingo.business.pay.model.PayLog;
import com.bingo.common.utility.SecurityClass;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Administrator on 2018-08-06.
 * 创建订单的返回参数定义
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayReturn {
    private Integer ret_code=-1;//返回码  1:才是成功
    private String ret_msg="ok";//消息
    private String nonce_str;//随机字符串


    //支付订单信息
    private String pay_id;//支付平台的订单号
    private String orderid;//订单ID
    private String qrcode;//支付的二维码 http://mobile.qq.com/qrcode?url=
    private Integer pay_type;//支付渠道 1：支付宝  2：微信
    private Integer qrcode_type;//支付二维码类型 1：非定额  2：定额 1：通码(手输金额)  2：固码（固定金额）
    private Float realprice;//实际支付金额
    private String pay_ext1;//扩展字段1
    private String pay_ext2;//扩展字段2
    private String pay_name;
    private String pay_demo;
    private String return_url;
    private String create_time;//订单的创建时间
    private Integer pay_state;//0等待支付  1:已支付

    private String sign;//签名

    public PayReturn(){
        nonce_str = UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 通过log对象，初始化返回对象
     * @param log
     */
    public PayReturn(PayLog log){
        this.nonce_str = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        if(log==null){
            return;
        }
        this.create_time = log.getCreatetime();
        this.orderid = log.getOrderid();
        this.pay_id=log.getRid()+"";
        this.qrcode = log.getPayImgContent();
        this.realprice = log.getPayImgPrice();
        this.pay_type = log.getPayType();
        this.pay_ext1 = log.getPayExt1();
        this.pay_ext2 = log.getPayExt2();
        this.pay_name = log.getPayName();
        this.pay_demo = log.getPayDemo();
        this.pay_state = log.getPayState();
        this.return_url = log.getReturnUrl();
        if(log.getProdImgId()!=null&&log.getProdImgId()>0){
            qrcode_type = 2;
        }else{
            qrcode_type = 1;
        }
        reSetReturnUrl();

    }

    public Integer getRet_code() {
        return ret_code;
    }

    public void setRet_code(Integer ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public void setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
    }

    public Float getRealprice() {
        return realprice;
    }

    public void setRealprice(Float realprice) {
        this.realprice = realprice;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Integer getPay_state() {
        return pay_state;
    }

    public void setPay_state(Integer pay_state) {
        this.pay_state = pay_state;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getQrcode_type() {
        return qrcode_type;
    }

    public void setQrcode_type(Integer qrcode_type) {
        this.qrcode_type = qrcode_type;
    }

    /**
     * 对pay对象进行签名
     * @return
     */
    public String MarkSign(String sign_key) throws IllegalAccessException {
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
        //System.out.println(stringA.toString());
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


    /**
     * 重设sign
     * @param sign_key
     * @throws IllegalAccessException
     */
    public void reSetSign(String sign_key) throws IllegalAccessException {
        this.sign = MarkSign(sign_key);
    }

    /**
     * 重设returnurl
     */
    public void reSetReturnUrl(){
        if(return_url==null || return_url.indexOf("orderid=")!=-1){
            return;
        }
        if(return_url.indexOf("?")==-1){
            return_url = return_url+"?orderid="+orderid+"&nonce_str="+nonce_str;
        }else{
            return_url = return_url+"&orderid="+orderid+"&nonce_str="+nonce_str;
        }
    }

}
