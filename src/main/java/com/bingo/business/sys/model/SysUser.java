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
 * 对象功能: 用户表 Model对象
 */
@Entity
@Table(name="T_sys_user")
public class SysUser extends PageModel{

	@Column(name = "username")
	protected String  username;//用户名，登录账号
	
	
	@Column(name = "pwd")
	protected String  pwd;//用户密码，MD5加密存储
	
	
	@Column(name = "createtime")
	protected String  createtime;//创建时间
	
	
	@Column(name = "usertype")
	protected Integer  usertype=0;//用户类型，0：普通用户，1：管理员


	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "userid")
	protected Long  userid;//userid
	
	
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

}
