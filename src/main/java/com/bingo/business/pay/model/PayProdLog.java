package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-09-02 19:36:28
 * 对象功能:  Model对象,作废哦。
 */
@Entity
@Table(name="T_PAY_PROD_LOG")
public class PayProdLog extends PageModel{

	@Id
	@Column(name = "orderid")
	protected String  orderid;//订单ID
	
	
	@Column(name = "bus_id")
	protected Long  busId;//商户ID
	
	
	@Column(name = "prod_id")
	protected Long  prodId;//商品ID
	
	
	@Column(name = "prod_name")
	protected String  prodName;//商品名称
	
	
	@Column(name = "prod_count")
	protected Long  prodCount;//商品数量
	
	
	@Column(name = "order_state")
	protected Long  orderState;//订单状态 0：未支付，1已支付
	
	@Column(name = "order_price")
	protected String  orderPrice;//订单价格
	
	
	@Column(name = "pay_price")
	protected String  payPrice;//支付价格
	
	
	@Column(name = "pay_id")
	protected Long  payId;//支付订单ID
	
	
	@Column(name = "create_time")
	protected Long  createTime;//创建时间
	
	
	@Column(name = "user_id")
	protected String  userId;//用户ID
	
	
	@Column(name = "user_phone")
	protected String  userPhone;//用户手机号码
	
	
	@Column(name = "user_address")
	protected String  userAddress;//用户配送地址
	
	
	@Column(name = "user_name")
	protected String  userName;//用户姓名
	
	
	@Column(name = "pay_time")
	protected Long  payTime;//支付时间,这个没用，采用支付表的支付时间
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayProdLog(){
	
	}
	
	public void setOrderid(String orderid){
		this.orderid = orderid;
	}
	/**
	 * 返回 orderid
	 * @return
	 */
	public String getOrderid(){
		return this.orderid;
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
	
	public void setProdCount(Long prodCount){
		this.prodCount = prodCount;
	}
	/**
	 * 返回 prod_count
	 * @return
	 */
	public Long getProdCount(){
		return this.prodCount;
	}
	
	public void setOrderState(Long orderState){
		this.orderState = orderState;
	}
	/**
	 * 返回 order_state
	 * @return
	 */
	public Long getOrderState(){
		return this.orderState;
	}
	
	public void setOrderPrice(String orderPrice){
		this.orderPrice = orderPrice;
	}
	/**
	 * 返回 order_price
	 * @return
	 */
	public String getOrderPrice(){
		return this.orderPrice;
	}
	
	public void setPayPrice(String payPrice){
		this.payPrice = payPrice;
	}
	/**
	 * 返回 pay_price
	 * @return
	 */
	public String getPayPrice(){
		return this.payPrice;
	}
	
	public void setPayId(Long payId){
		this.payId = payId;
	}
	/**
	 * 返回 pay_id
	 * @return
	 */
	public Long getPayId(){
		return this.payId;
	}
	
	public void setCreateTime(Long createTime){
		this.createTime = createTime;
	}
	/**
	 * 返回 create_time
	 * @return
	 */
	public Long getCreateTime(){
		return this.createTime;
	}
	
	public void setUserId(String userId){
		this.userId = userId;
	}
	/**
	 * 返回 user_id
	 * @return
	 */
	public String getUserId(){
		return this.userId;
	}
	
	public void setUserPhone(String userPhone){
		this.userPhone = userPhone;
	}
	/**
	 * 返回 user_phone
	 * @return
	 */
	public String getUserPhone(){
		return this.userPhone;
	}
	
	public void setUserAddress(String userAddress){
		this.userAddress = userAddress;
	}
	/**
	 * 返回 user_address
	 * @return
	 */
	public String getUserAddress(){
		return this.userAddress;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	/**
	 * 返回 user_name
	 * @return
	 */
	public String getUserName(){
		return this.userName;
	}
	
	public void setPayTime(Long payTime){
		this.payTime = payTime;
	}
	/**
	 * 返回 pay_time
	 * @return
	 */
	public Long getPayTime(){
		return this.payTime;
	}
	
	
}
