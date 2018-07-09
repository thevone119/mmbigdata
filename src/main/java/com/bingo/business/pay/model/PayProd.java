package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  支付产品表Model对象
 */
@Entity
@Table(name="T_PAY_PROD")
public class PayProd extends PageModel{

	@Column(name = "bus_id",updatable = false)
	protected Long  busId;//bus_id
	
	
	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "prod_id")
	protected Long  prodId;//prod_id
	
	
	@Column(name = "prod_name")
	protected String  prodName;//prod_name
	
	
	@Column(name = "prod_momey")
	protected Long  prodMomey;//prod_momey
	
	
	@Column(name = "pay_img_path")
	protected String  payImgPath;//pay_img_path
	
	
	@Column(name = "pay_type")
	protected Long  payType;//pay_type
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayProd(){
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

	public Long getProdMomey() {
		return prodMomey;
	}

	public void setProdMomey(Long prodMomey) {
		this.prodMomey = prodMomey;
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
	
	
}
