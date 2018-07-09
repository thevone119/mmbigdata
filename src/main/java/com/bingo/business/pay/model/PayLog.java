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
	protected Long  logId;//log_id
	
	
	@Column(name = "bus_id",updatable = false)
	protected Long  busId;//bus_id
	
	
	@Column(name = "bus_acc",updatable = false)
	protected String  busAcc;//bus_acc
	
	
	@Column(name = "bus_name",updatable = false)
	protected String  busName;//bus_name
	
	
	@Column(name = "prod_id",updatable = false)
	protected Long  prodId;//prod_id
	
	
	@Column(name = "prod_name",updatable = false)
	protected String  prodName;//prod_name
	
	
	@Column(name = "pay_money",updatable = false)
	protected Long  payMoney;//pay_money
	
	
	@Column(name = "pay_img_path",updatable = false)
	protected String  payImgPath;//pay_img_path
	
	
	@Column(name = "pay_demo",updatable = false)
	protected String  payDemo;//pay_demo
	
	
	@Column(name = "pay_type",updatable = false)
	protected Long  payType;//pay_type
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime
	
	
	@Column(name = "pay_time",updatable = false)
	protected String  payTime;//pay_time
	
	
	@Column(name = "pay_state")
	protected Long  payState;//pay_state
	
	
	@Column(name = "pay_ext2",updatable = false)
	protected String  payExt2;//pay_ext2
	
	
	@Column(name = "pay_ext1",updatable = false)
	protected String  payExt1;//pay_ext1
	
	
	@Column(name = "notify_state",updatable = false)
	protected Long  notifyState;//notify_state
	
	
	@Column(name = "notify_count",updatable = false)
	protected Long  notifyCount;//notify_count
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayLog(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	
	public void setPayMoney(Long payMoney){
		this.payMoney = payMoney;
	}
	/**
	 * 返回 pay_money
	 * @return
	 */
	public Long getPayMoney(){
		return this.payMoney;
	}
	
	public void setPayImgPath(String payImgPath){
		this.payImgPath = payImgPath;
	}
	/**
	 * 返回 pay_img_path
	 * @return
	 */
	public String getPayImgPath(){
		return this.payImgPath;
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
	
	public void setPayType(Long payType){
		this.payType = payType;
	}
	/**
	 * 返回 pay_type
	 * @return
	 */
	public Long getPayType(){
		return this.payType;
	}
	
	public void setCreatetime(String createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 createtime
	 * @return
	 */
	public String getCreatetime(){
		return this.createtime;
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
	
	public void setPayState(Long payState){
		this.payState = payState;
	}
	/**
	 * 返回 pay_state
	 * @return
	 */
	public Long getPayState(){
		return this.payState;
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
	
	public void setNotifyState(Long notifyState){
		this.notifyState = notifyState;
	}
	/**
	 * 返回 notify_state
	 * @return
	 */
	public Long getNotifyState(){
		return this.notifyState;
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
	
	
}
