package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:34:30
 * 对象功能:  支付记录表Model对象
 */
@Entity
@Table(name="T_PAY_LOG")
public class PayLog extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "log_id")
	protected Long  logId;//日志ID
	
	
	@Column(name = "bus_id",updatable = false)
	protected Long  busId;//商户ID

	@Column(name = "bus_type",updatable = false)
	protected Long  busType;//商户类型
	
	
	@Column(name = "bus_acc",updatable = false)
	protected String  busAcc;//商户账号

	@Column(name = "uid",updatable = false)
	protected String  uid;//商户uid
	
	
	@Column(name = "bus_name",updatable = false)
	protected String  busName;//商户姓名

	@Column(name = "orderid",updatable = false)
	protected String  orderid;//订单ID，外部传过来的订单ID
	
	@Column(name = "prod_id",updatable = false)
	protected Long  prodId;//支付商品ID
	
	
	@Column(name = "prod_name",updatable = false)
	protected String  prodName;//支付商品名称

	@Column(name = "prod_price",updatable = false)
	protected Float  prodPrice;//支付商品价格


	@Column(name = "pay_img_price",updatable = false)
	protected Float  payImgPrice;//二维码支付价格

	@Column(name = "pay_service_change",updatable = false)
	protected Float  payServiceChange;//支付平台服务费，手续费




	@Column(name = "pay_img_content",updatable = false)
	protected String  payImgContent;//支付二维码图片内容



	@Column(name = "pay_demo",updatable = false)
	protected String  payDemo;//支付订单备注

	@Column(name = "pay_name",updatable = false)
	protected String  payName;//支付订单名称
	
	
	@Column(name = "pay_type",updatable = false)
	protected Integer  payType;//pay_type
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//创建时间


	
	
	@Column(name = "pay_time",updatable = false)
	protected String  payTime;//pay_time
	
	
	@Column(name = "pay_state",updatable = false)
	protected Integer  payState;//支付状态 -1：未知状态，0：等待支付 1：支付成功，2：支付失败，11：账户余额不足，12：账户套餐过期，13：支付超时
	
	
	@Column(name = "pay_ext2",updatable = false)
	protected String  payExt2;//pay_ext2
	
	
	@Column(name = "pay_ext1",updatable = false)
	protected String  payExt1;//pay_ext1
	
	
	@Column(name = "notify_state",updatable = false)
	protected Integer  notifyState;//notify_state 0：未通知，1：已成功通知 2：支付通知失败
	
	
	@Column(name = "notify_count",updatable = false)
	protected Long  notifyCount;//notify_count 支付通知次数

	@Transient
	private String payStateStr = "";//支付状态 -1：未知状态，0：等待支付 1：支付成功，2：支付失败，11：账户余额不足，12：账户套餐过期，13：支付超时

	@Transient
	private String notifyStateStr = "";//0：未通知，1：已成功通知 2：支付通知失败

	@Transient
	private String payTypeStr = "";//0：未知，1：微信支付 2：支付宝支付
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayLog(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		this.createtime=format.format(new Date());
	}
	
	public void setLogId(Long logId){
		this.logId = logId;
	}
	/**
	 * 返回 log_id
	 * @return
	 */
	public Long getLogId(){
		return this.logId;
	}
	
	public void setBusId(Long busId){
		this.busId = busId;
	}
	/**
	 * 返回 bus_id
	 * @return
	 */
	public Long getBusId(){
		return this.busId;
	}
	
	public void setBusAcc(String busAcc){
		this.busAcc = busAcc;
	}
	/**
	 * 返回 bus_acc
	 * @return
	 */
	public String getBusAcc(){
		return this.busAcc;
	}
	
	public void setBusName(String busName){
		this.busName = busName;
	}
	/**
	 * 返回 bus_name
	 * @return
	 */
	public String getBusName(){
		return this.busName;
	}
	
	public void setProdId(Long prodId){
		this.prodId = prodId;
	}
	/**
	 * 返回 prod_id
	 * @return
	 */
	public Long getProdId(){
		return this.prodId;
	}
	
	public void setProdName(String prodName){
		this.prodName = prodName;
	}
	/**
	 * 返回 prod_name
	 * @return
	 */
	public String getProdName(){
		return this.prodName;
	}

	public Long getBusType() {
		return busType;
	}

	public void setBusType(Long busType) {
		this.busType = busType;
	}

	public Float getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(Float prodPrice) {
		this.prodPrice = prodPrice;
	}

	public Float getPayImgPrice() {
		return payImgPrice;
	}

	public void setPayImgPrice(Float payImgPrice) {
		this.payImgPrice = payImgPrice;
	}

	public Float getPayServiceChange() {
		return payServiceChange;
	}

	public void setPayServiceChange(Float payServiceChange) {
		this.payServiceChange = payServiceChange;
	}

	public String getPayImgContent() {
		return payImgContent;
	}

	public void setPayImgContent(String payImgContent) {
		this.payImgContent = payImgContent;
	}

	public void setPayDemo(String payDemo){
		this.payDemo = payDemo;
	}
	/**
	 * 返回 pay_demo
	 * @return
	 */
	public String getPayDemo(){
		return this.payDemo;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getNotifyStateStr() {
		//0：未通知，1：已成功通知 2：支付通知失败
		switch (this.notifyState){
			case 0:
				return "未通知";
			case 1:
				return "已成功通知";
			case 2:
				return "支付通知失败";
		}
		return notifyStateStr;
	}

	public void setNotifyStateStr(String notifyStateStr) {
		this.notifyStateStr = notifyStateStr;
	}

	public String getPayTypeStr() {
		//0：未知，1：微信支付 2：支付宝支付
		switch (this.payState){
			case -1:
				return "未知";
			case 0:
				return "未知";
			case 1:
				return "微信支付";
			case 2:
				return "支付宝支付";
		}
		return payTypeStr;
	}

	public void setPayTypeStr(String payTypeStr) {

		this.payTypeStr = payTypeStr;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public void setPayTime(String payTime){
		this.payTime = payTime;
	}
	/**
	 * 返回 pay_time
	 * @return
	 */
	public String getPayTime(){
		return this.payTime;
	}

	public Integer getPayState() {
		return payState;
	}

	public void setPayState(Integer payState) {
		this.payState = payState;
	}

	public void setPayExt2(String payExt2){
		this.payExt2 = payExt2;
	}
	/**
	 * 返回 pay_ext2
	 * @return
	 */
	public String getPayExt2(){
		return this.payExt2;
	}
	
	public void setPayExt1(String payExt1){
		this.payExt1 = payExt1;
	}
	/**
	 * 返回 pay_ext1
	 * @return
	 */
	public String getPayExt1(){
		return this.payExt1;
	}

	public Integer getNotifyState() {
		return notifyState;
	}

	public void setNotifyState(Integer notifyState) {
		this.notifyState = notifyState;
	}

	public void setNotifyCount(Long notifyCount){
		this.notifyCount = notifyCount;
	}
	/**
	 * 返回 notify_count
	 * @return
	 */
	public Long getNotifyCount(){
		return this.notifyCount;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getPayStateStr() {
		//支付状态 -1：未知状态，0：等待支付 1：支付成功，2：支付失败，11：账户余额不足，12：账户套餐过期，13：支付超时
		switch (this.payState){
			case -1:
				return "未知状态";
			case 0:
				return "等待支付";
			case 1:
				return "支付成功";
			case 2:
				return "支付失败";
			case 11:
				return "账户余额不足";
			case 12:
				return "账户套餐过期";
			case 13:
				return "支付超时";
		}
		return payStateStr;
	}

	public void setPayStateStr(String payStateStr) {
		this.payStateStr = payStateStr;
	}
}
