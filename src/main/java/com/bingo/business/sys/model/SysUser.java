package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
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
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "userid",updatable = false)
	protected Long  userid;//userid

	@Column(name = "uuid")
	protected String  uuid;//用户ID

	@Column(name = "sign_key")
	protected String  signKey;//用户的签名秘钥

	@Column(name = "useracc",updatable = false)
	protected String  useracc;//用户名，登录账号
	
	
	@Column(name = "pwd")
	protected String  pwd;//用户密码，MD5加密存储
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//创建时间
	
	
	@Column(name = "usertype")
	protected Integer  usertype=0;//用户类型，0：普通用户，1：管理员 2:支付商户



	
	
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


	@Column(name = "notify_url")
	protected String  notifyUrl;//支付通知地址

	@Column(name = "goback_url")
	protected String  gobackUrl;//支付跳转地址
	
	
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

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getGobackUrl() {
		return gobackUrl;
	}

	public void setGobackUrl(String gobackUrl) {
		this.gobackUrl = gobackUrl;
	}
}
