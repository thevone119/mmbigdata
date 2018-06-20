package com.bingo.business.taobao.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-04-07 23:45:50
 * 对象功能: 淘宝用户表 Model对象
 */
@Entity
@Table(name="T_TB_user")
public class TBUser extends PageModel{

	@Id
	@Column(name = "uid")
	protected Long  uid;//uId


	@Column(name = "user_account")
	protected String  userAccount;//用户账号


	
	@Column(name = "nick")
	protected String  nick;//nick

	@Column(name = "loc")
	protected String  loc;//归属地(城市)


	@Column(name = "main_cname")
	protected String  mainCname;//主营业务分类名称

	@Column(name = "main_cid")
	protected Long  mainCid;//主营业务分类id

	@Column(name = "sex")
	protected String  sex;//sex
	
	
	@Column(name = "userpage")
	protected String  userpage;//userpage
	
	
	@Column(name = "seller_credit")
	protected Long  sellerCredit;//seller_credit
	
	
	@Column(name = "buy_credit")
	protected Long  buyCredit;//buy_credit
	
	
	@Column(name = "create_Time",updatable = false)
	protected String  createTime;//create_Time
	
	
	@Column(name = "update_Time")
	protected String  updateTime;//update_Time
	
	
	@Column(name = "register_time")
	protected String  registerTime;//register_time

	@Column(name = "user_rate_url")
	protected String  userRateUrl;//卖家主页,相对路径，如：//rate.taobao.com/user-rate-UOmgWMCQ0OmIb.htm
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBUser(){
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMdd");
		String now=dtf2.format(LocalDate.now());
		createTime = now;
		updateTime = now;
	}
	
	public void setUid(Long uid){
		this.uid = uid;
	}
	/**
	 * 返回 uId
	 * @return
	 */
	public Long getUid(){
		return this.uid;
	}
	
	public void setNick(String nick){
		this.nick = nick;
	}
	/**
	 * 返回 nick
	 * @return
	 */
	public String getNick(){
		return this.nick;
	}
	
	public void setSex(String sex){
		this.sex = sex;
	}
	/**
	 * 返回 sex
	 * @return
	 */
	public String getSex(){
		return this.sex;
	}
	
	public void setUserpage(String userpage){
		this.userpage = userpage;
	}
	/**
	 * 返回 userpage
	 * @return
	 */
	public String getUserpage(){
		return this.userpage;
	}
	
	public void setSellerCredit(Long sellerCredit){
		this.sellerCredit = sellerCredit;
	}
	/**
	 * 返回 seller_credit
	 * @return
	 */
	public Long getSellerCredit(){
		return this.sellerCredit;
	}
	
	public void setBuyCredit(Long buyCredit){
		this.buyCredit = buyCredit;
	}
	/**
	 * 返回 buy_credit
	 * @return
	 */
	public Long getBuyCredit(){
		return this.buyCredit;
	}
	
	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
	/**
	 * 返回 create_Time
	 * @return
	 */
	public String getCreateTime(){
		return this.createTime;
	}
	
	public void setUpdateTime(String updateTime){
		this.updateTime = updateTime;
	}
	/**
	 * 返回 update_Time
	 * @return
	 */
	public String getUpdateTime(){
		return this.updateTime;
	}
	
	public void setRegisterTime(String registerTime){
		this.registerTime = registerTime;
	}
	/**
	 * 返回 register_time
	 * @return
	 */
	public String getRegisterTime(){
		return this.registerTime;
	}


	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getMainCname() {
		return mainCname;
	}

	public void setMainCname(String mainCname) {
		this.mainCname = mainCname;
	}

	public Long getMainCid() {
		return mainCid;
	}

	public void setMainCid(Long mainCid) {
		this.mainCid = mainCid;
	}

	public String getUserRateUrl() {
		return userRateUrl;
	}

	public void setUserRateUrl(String userRateUrl) {
		this.userRateUrl = userRateUrl;
	}
}
