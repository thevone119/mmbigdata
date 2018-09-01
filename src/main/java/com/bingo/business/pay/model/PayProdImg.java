package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  支付二维码表
 */
@Entity
@Table(name="T_PAY_PROD_IMG")
public class PayProdImg extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "cid",updatable = false)
	protected Long  cid;//主键ID


	@Column(name = "user_id",updatable = false)
	protected Long  userId;//用户ID


	@Column(name = "img_price")
	protected Float  imgPrice;//二维码支付价格



	@Column(name = "img_content")
	protected String  imgContent;//图片二维码内容


	@Column(name = "pay_type")
	protected Integer  payType;//pay_type 1：支付宝；2：微信支付


	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime

	@Transient
	private String fitPrice = "";//适配的价格访问



	/**
	 * 对象构建方法
	 * @return
	 */
	public PayProdImg(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Float getImgPrice() {
		return imgPrice;
	}

	public void setImgPrice(Float imgPrice) {
		this.imgPrice = imgPrice;
	}

	public String getImgContent() {
		return imgContent;
	}

	public void setImgContent(String imgContent) {
		this.imgContent = imgContent;
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


	public String getFitPrice() {

		return String.format("%.2f", this.imgPrice) +" - " + String.format("%.2f", this.imgPrice+0.1f);
		//return fitPrice;
	}

	public void setFitPrice(String fitPrice) {
		this.fitPrice = fitPrice;
	}
}
