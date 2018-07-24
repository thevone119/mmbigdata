package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  支付产品表Model对象
 */
@Entity
@Table(name="T_PAY_PROD")
public class PayProd extends PageModel{

	@Column(name = "user_id",updatable = false)
	protected Long  userId;//用户ID
	
	
	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "prod_id")
	protected Long  prodId;//产品ID
	
	
	@Column(name = "prod_name")
	protected String  prodName;//产品名称
	
	
	@Column(name = "prod_price")
	protected Float  prodPrice;//产品价格

	@Column(name = "pay_img_price")
	protected Float  payImgPrice;//二维码支付价格
	
	
	@Column(name = "pay_img_path")
	protected String  payImgPath;//图片路径,uuid作为图片路径

	@Column(name = "pay_img_type")
	protected String  payImgType;//图片类型，png,jpg等

	@Column(name = "pay_img_content")
	protected String  payImgContent;//图片二维码内容
	
	
	@Column(name = "pay_type")
	protected Long  payType;//pay_type 1：支付宝；2：微信支付

	@Column(name = "state")
	private int state=1;//状态 0无效，1有效
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayProd(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
		this.payImgPath= UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Float getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(Float prodPrice) {
		this.prodPrice = prodPrice;
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

	public String getPayImgContent() {
		return payImgContent;
	}

	public void setPayImgContent(String payImgContent) {
		this.payImgContent = payImgContent;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPayImgType() {
		return payImgType;
	}

	public void setPayImgType(String payImgType) {
		this.payImgType = payImgType;
	}

	public Float getPayImgPrice() {
		return payImgPrice;
	}

	public void setPayImgPrice(Float payImgPrice) {
		this.payImgPrice = payImgPrice;
	}
}
