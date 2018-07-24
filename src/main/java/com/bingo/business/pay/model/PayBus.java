package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:33:38
 * 对象功能:  商户表Model对象
 */
@Entity
@Table(name="T_PAY_BUS")
public class PayBus extends PageModel{

	@Id
	@Column(name = "bus_id")
	protected Long  busId;//商户ID,直接使用用户表的ID
	
	
	@Column(name = "bus_acc",updatable = false)
	protected String  busAcc;//商户账号
	

	@Column(name = "goback_url")
	protected String  gobackUrl;//goback_url
	
	
	@Column(name = "notify_url")
	protected String  notifyUrl;//notify_url
	
	
	@Column(name = "bus_name")
	protected String  busName;//bus_name
	
	
	@Column(name = "bus_type")
	protected Long  busType;//商户类型,0：默认，无套餐  1：商户基础版套餐，2：商户高级版套餐，3：商户专业版套餐

	@Column(name = "bus_validity")
	protected Long  busValidity;//套餐有效期
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayBus(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
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
	

	
	public void setGobackUrl(String gobackUrl){
		this.gobackUrl = gobackUrl;
	}
	/**
	 * 返回 goback_url
	 * @return
	 */
	public String getGobackUrl(){
		return this.gobackUrl;
	}
	
	public void setNotifyUrl(String notifyUrl){
		this.notifyUrl = notifyUrl;
	}
	/**
	 * 返回 notify_url
	 * @return
	 */
	public String getNotifyUrl(){
		return this.notifyUrl;
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
	
	public void setBusType(Long busType){
		this.busType = busType;
	}
	/**
	 * 返回 bus_type
	 * @return
	 */
	public Long getBusType(){
		return this.busType;
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

	public Long getBusValidity() {
		return busValidity;
	}

	public void setBusValidity(Long busValidity) {
		this.busValidity = busValidity;
	}
}
