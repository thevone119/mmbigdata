package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-09-02 19:36:28
 * 对象功能:  Model对象
 */
@Entity
@Table(name="T_PAY_PROD_LOG")
public class PayProdLog extends PageModel{

	@Id
	@TableGenerator(name="GENERATOR_ID",table="T_SYS_DB_GENERATOR",allocationSize=10) 
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GENERATOR_ID")
	@Column(name = "orderid")
	protected String  orderid;//orderid
	
	
	@Column(name = "bus_id")
	protected Long  busId;//bus_id
	
	
	@Column(name = "prod_id")
	protected Long  prodId;//prod_id
	
	
	@Column(name = "prod_name")
	protected String  prodName;//prod_name
	
	
	@Column(name = "prod_count")
	protected Long  prodCount;//prod_count
	
	
	@Column(name = "order_state")
	protected Long  orderState;//order_state
	
	
	@Column(name = "order_price")
	protected String  orderPrice;//order_price
	
	
	@Column(name = "pay_price")
	protected String  payPrice;//pay_price
	
	
	@Column(name = "pay_id")
	protected Long  payId;//pay_id
	
	
	@Column(name = "create_time")
	protected Long  createTime;//create_time
	
	
	@Column(name = "user_id")
	protected String  userId;//user_id
	
	
	@Column(name = "user_phone")
	protected String  userPhone;//user_phone
	
	
	@Column(name = "user_address")
	protected String  userAddress;//user_address
	
	
	@Column(name = "user_name")
	protected String  userName;//user_name
	
	
	@Column(name = "pay_time")
	protected Long  payTime;//pay_time
	
	
	
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
