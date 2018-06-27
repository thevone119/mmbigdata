package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-06-24 23:55:28
 * 对象功能: 淘宝用户表 Model对象
 */
@Entity
@Table(name="T_sys_user")
public class SysUser extends PageModel{

	@Column(name = "username")
	protected String  username;//username
	
	
	@Column(name = "pwd")
	protected String  pwd;//pwd
	
	
	@Column(name = "createtime")
	protected String  createtime;//createtime
	
	
	@Column(name = "usertype")
	protected Long  usertype;//usertype
	
	
	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "userid")
	protected Long  userid;//userid
	
	
	@Column(name = "nikename")
	protected String  nikename;//nikename
	
	
	@Column(name = "email")
	protected String  email;//email
	
	
	@Column(name = "qq")
	protected String  qq;//qq
	
	
	@Column(name = "mobile")
	protected String  mobile;//mobile

	@Column(name = "state")
	protected Long  state;//状态；0：无效 1：有效
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysUser(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
		//this.updatetime=format.format(new Date());
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	/**
	 * 返回 username
	 * @return
	 */
	public String getUsername(){
		return this.username;
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
	
	public void setUsertype(Long usertype){
		this.usertype = usertype;
	}
	/**
	 * 返回 usertype
	 * @return
	 */
	public Long getUsertype(){
		return this.usertype;
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

	public Long getState() {
		return state;
	}

	public void setState(Long state) {
		this.state = state;
	}
}
