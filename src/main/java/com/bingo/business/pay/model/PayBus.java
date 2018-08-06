package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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

	@Column(name = "uuid",updatable = false)
	protected String  uuid;//用户的uuid

	@Column(name = "sign_key",updatable = false)
	protected String  signKey;//用户的签名秘钥
	
	
	@Column(name = "bus_acc",updatable = false)
	protected String  busAcc;//商户账号
	

	@Column(name = "goback_url")
	protected String  gobackUrl;//goback_url
	
	
	@Column(name = "notify_url")
	protected String  notifyUrl;//notify_url
	
	
	@Column(name = "bus_name")
	protected String  busName;//bus_name
	
	
	@Column(name = "bus_type",updatable = false)
	protected Integer  busType;//商户类型,0：默认，无套餐  1：商户基础版套餐，2：商户高级版套餐，3：商户专业版套餐

	@Column(name = "bus_validity",updatable = false)
	protected Long  busValidity;//套餐有效期

	@Column(name = "emoney",updatable = false)
	protected float  eMoney=0.0f;//商户金额，余额

	@Column(name = "auto_re_fee")
	protected Integer  autoReFee=0;//到期自动续费0:不自动续费，1：自动续费套餐1，2自动续费套餐2,3：自动续费套餐3
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime

	@Column(name = "pay_img_content_zfb")
	protected String  payImgContentZfb;//支付宝的收款二维码

	@Column(name = "pay_img_content_wx")
	protected String  payImgContentWx;//微信的收款二维码

	@Column(name = "pay_time_out")
	protected Integer  payTimeOut=5;//支付超时时间。分钟，默认5分钟，可选5分钟，10分钟，15分钟


	@Transient
	private String busTypeStr = "";//商户类型,0：默认，无套餐  1：商户基础版套餐，2：商户高级版套餐，3：商户专业版套餐

	@Transient
	private String autoReFeeStr = "";//到期自动续费0:不自动续费，1：自动续费套餐1，2自动续费套餐2,3：自动续费套餐3

	@Transient
	private String createtimeStr = "";//日期的格式化输出

	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayBus(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		this.uuid= UUID.randomUUID().toString().replace("-", "").toLowerCase();
		this.signKey =  UUID.randomUUID().toString().replace("-", "").toLowerCase();
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
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

	public Integer getBusType() {
		return busType;
	}


	public Integer getPayTimeOut() {
		return payTimeOut;
	}

	public void setPayTimeOut(Integer payTimeOut) {
		this.payTimeOut = payTimeOut;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
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

	public float geteMoney() {
		return eMoney;
	}

	public void seteMoney(float eMoney) {
		this.eMoney = eMoney;
	}

	public Integer getAutoReFee() {
		return autoReFee;
	}

	public void setAutoReFee(Integer autoReFee) {
		this.autoReFee = autoReFee;
	}

	public String getPayImgContentZfb() {
		return payImgContentZfb;
	}

	public void setPayImgContentZfb(String payImgContentZfb) {
		this.payImgContentZfb = payImgContentZfb;
	}

	public String getPayImgContentWx() {
		return payImgContentWx;
	}

	public void setPayImgContentWx(String payImgContentWx) {
		this.payImgContentWx = payImgContentWx;
	}

	public String getBusTypeStr() {
		switch (this.busType){
			case 0:
				return "无";
			case 1:
				return "基础版";
			case 2:
				return "高级版";
			case 3:
				return "专业版";
		}
		return "无";
		//return busTypeStr;
	}

	public void setBusTypeStr(String busTypeStr) {
		this.busTypeStr = busTypeStr;
	}

	public String getAutoReFeeStr() {
		switch (this.autoReFee){
			case 0:
				return "无";
			case 1:
				return "自动续费至<基础版>";
			case 2:
				return "自动续费至<高级版>";
			case 3:
				return "自动续费至<专业版>";
		}
		return autoReFeeStr;
	}

	public void setAutoReFeeStr(String autoReFeeStr) {
		this.autoReFeeStr = autoReFeeStr;
	}

	public String getCreatetimeStr() throws ParseException {
		if(this.createtime==null){
			return null;
		}
		if(this.createtime.indexOf("-")!=-1){
			return createtime;
		}

		if(this.createtime.length()==10){
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format2.format(format.parse(createtime));
		}
		return createtime;
	}

	public void setCreatetimeStr(String createtimeStr) {
		this.createtimeStr = createtimeStr;
	}
}
