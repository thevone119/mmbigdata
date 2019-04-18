package com.bingo.business.sys.model;

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
 * 2018-06-24 23:55:28
 * 对象功能: 用户表 Model对象
 */
@Entity
@Table(name="T_sys_user")
public class SysUser extends PageModel{

	@Id
	@Column(name = "userid",updatable = false)
	protected Long  userid;//userid

	@Column(name = "uuid",updatable = false)
	protected String  uuid;//用户ID

	@Column(name = "sign_key",updatable = false)
	protected String  signKey;//用户的签名秘钥

	@Column(name = "useracc",updatable = false)
	protected String  useracc;//用户名，登录账号
	
	
	@Column(name = "pwd")
	protected String  pwd;//用户密码，MD5加密存储
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//创建时间
	
	
	@Column(name = "usertype")
	protected Integer  usertype=0;//用户类型，0：普通用户，1：管理员 2:支付商户（作废掉？）



	
	
	@Column(name = "nikename")
	protected String  nikename;//昵称，中文名称
	
	
	@Column(name = "email")
	protected String  email;//email
	
	
	@Column(name = "qq")
	protected String  qq;//qq
	
	
	@Column(name = "mobile")
	protected String  mobile;//手机号码

	@Column(name = "state")
	protected Integer  state=0;//状态；0：无效 1：有效

	//以下字段均为添加字段哦,集成支付平台各种字段

	@Column(name = "goback_url")
	protected String  gobackUrl;//goback_url


	@Column(name = "notify_url")
	protected String  notifyUrl;//notify_url

	@Column(name = "bus_type",updatable = false)
	protected Integer  busType;//商户类型,0：默认，无套餐  1：商户基础版套餐，2：商户高级版套餐，3：商户专业版套餐

	@Column(name = "bus_validity",updatable = false)
	protected Long  busValidity;//套餐有效期YYYYMMDD,年月日

	@Column(name = "emoney",updatable = false)
	protected float  eMoney=0.0f;//商户金额，余额

	@Column(name = "auto_re_fee")
	protected Integer  autoReFee=0;//到期自动续费0:不自动续费，1：自动续费套餐1，2自动续费套餐2,3：自动续费套餐3



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
	public SysUser(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());

		this.uuid= UUID.randomUUID().toString().replace("-", "").toLowerCase();
		this.signKey= UUID.randomUUID().toString().replace("-", "").toLowerCase();
		//this.updatetime=format.format(new Date());
	}

	public String getUseracc() {
		return useracc;
	}

	public void setUseracc(String useracc) {
		this.useracc = useracc;
	}

	public void setPwd(String pwd){
		this.pwd = pwd;
	}
	/**
	 * 返回 pwd
	 * @return
	 */
	public String getPwd(){
		return this.pwd;
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
	

	public void setUserid(Long userid){
		this.userid = userid;
	}
	/**
	 * 返回 userid
	 * @return
	 */
	public Long getUserid(){
		return this.userid;
	}
	
	public void setNikename(String nikename){
		this.nikename = nikename;
	}
	/**
	 * 返回 nikename
	 * @return
	 */
	public String getNikename(){
		return this.nikename;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	/**
	 * 返回 email
	 * @return
	 */
	public String getEmail(){
		return this.email;
	}
	
	public void setQq(String qq){
		this.qq = qq;
	}
	/**
	 * 返回 qq
	 * @return
	 */
	public String getQq(){
		return this.qq;
	}
	
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	/**
	 * 返回 mobile
	 * @return
	 */
	public String getMobile(){
		return this.mobile;
	}

	public Integer getUsertype() {
		return usertype;
	}

	public void setUsertype(Integer usertype) {
		this.usertype = usertype;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
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


	public Integer getBusType() {
		return busType;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
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

	public Integer getPayTimeOut() {
		return payTimeOut;
	}

	public void setPayTimeOut(Integer payTimeOut) {
		this.payTimeOut = payTimeOut;
	}

	public String getGobackUrl() {
		return gobackUrl;
	}

	public void setGobackUrl(String gobackUrl) {
		this.gobackUrl = gobackUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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

		if(this.createtime.length()==14){
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
