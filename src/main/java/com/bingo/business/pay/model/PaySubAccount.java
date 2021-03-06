package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.bingo.common.utility.RandomUtils;
import com.bingo.common.utility.URIEncoder;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2019-04-17.
 * 支付收款账号，针对子账号
 */
@Entity
@Table(name="T_PAY_SUB_ACCOUNT")
public class PaySubAccount extends PageModel {

    @Id
    @GeneratedValue //相当于native  相当于mysql的表内自增)
    @Column(name = "sid")
    protected long  sid;//sid，主键，无意义

    @Column(name = "bus_id",updatable = false)
    protected long  busId;//商户ID,直接使用用户表的ID



    //收款子账号
    @Column(name = "subaccount",updatable = false)
    protected String  subaccount;//收款子账号



    @Column(name = "pay_img_content")
    protected String  payImgContent;//子账号收款通码


    //账号类型，支付类型
    @Column(name = "pay_type")
    protected Integer  payType=2;//pay_type 1：支付宝；2：微信支付 3，银联？



    //创建时间
    @Column(name = "createtime",updatable = false)
    protected String  createtime;//createtime

    //总收金额(单位，分)
    @Column(name = "pay_amount")
    protected float  payAmout;//总收金额

    //预收金额(单位，分)
    @Column(name = "pay_plan_amount")
    protected float  payPlanAmout;//总收金额

    //收款已收笔数
    @Column(name = "pay_count")
    protected int  payCount;//收款已收笔数

    //收款预收笔数
    @Column(name = "pay_plan_count")
    protected int  payPlanCount;//收款预收笔数


    //账号状态 0：无效 1：有效
    @Column(name = "state")
    protected int  state=1;


    @Transient
    private String payAmoutStr = "";//日期的格式化输出


    @Transient
    private String payPlanAmoutStr = "";//价格格式化输出


    public PaySubAccount(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        this.createtime=format.format(new Date());
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getBusId() {
        return busId;
    }

    public void setBusId(long busId) {
        this.busId = busId;
    }


    public String getSubaccount() {
        return subaccount;
    }

    public void setSubaccount(String subaccount) {
        this.subaccount = subaccount;
    }

    public String getPayImgContent() {
        return payImgContent;
    }

    public void setPayImgContent(String payImgContent) {
        this.payImgContent = payImgContent;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public float getPayAmout() {
        return payAmout;
    }

    public void setPayAmout(float payAmout) {
        this.payAmout = payAmout;
    }

    public float getPayPlanAmout() {
        return payPlanAmout;
    }

    public void setPayPlanAmout(float payPlanAmout) {
        this.payPlanAmout = payPlanAmout;
    }

    public int getPayCount() {
        return payCount;
    }

    public void setPayCount(int payCount) {
        this.payCount = payCount;
    }

    public int getPayPlanCount() {
        return payPlanCount;
    }

    public void setPayPlanCount(int payPlanCount) {
        this.payPlanCount = payPlanCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getPayAmoutStr() {
        return String.format("%.2f", this.payAmout) ;
    }

    public void setPayAmoutStr(String payAmoutStr) {
        this.payAmoutStr = payAmoutStr;
    }

    public String getPayPlanAmoutStr() {
        return String.format("%.2f", this.payPlanAmout) ;
    }

    public void setPayPlanAmoutStr(String payPlanAmoutStr) {
        this.payPlanAmoutStr = payPlanAmoutStr;
    }

    //支付宝的收款连接
    public String getZFBPIDContent(Float imgPrice,String Orderid){
        //String alipayUserId="2088102951972617";
        String alipayUserId=subaccount;
        String alipays = "alipays://platformapi/startapp?appId=20000691&url="; // 2019年04月07日 原appid 20000067 替换成 20000691
        String price = String.format("%.2f", imgPrice);
        String url = "https://www.heimipay.com/pay/flow/alipay.html?u=" + alipayUserId + "&a="+price+"&d="+Orderid;
        //String url = "https://pay.sxhhjc.cn/go?i=4e29d3fbbde9d015d1090246";
        return alipays+ URIEncoder.encodeURIComponent(url);
    }
}
