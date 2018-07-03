package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;

import javax.persistence.*;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: 角色用户关联 Model对象
 */
@Entity
@Table(name="T_sys_role_res")
public class SysRoleUser extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "sid")
	protected Long  sid;//sid


	@Column(name = "userid")
	protected Long  userid;//用户ID


	@Column(name = "roleid")
	protected Long  roleid;//角色ID



	/**
	 * 对象构建方法
	 * @return
	 */
	public SysRoleUser(){
	
	}
	
	public void setSid(Long sid){
		this.sid = sid;
	}
	/**
	 * 返回 sid
	 * @return
	 */
	public Long getSid(){
		return this.sid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public void setRoleid(Long roleid){
		this.roleid = roleid;
	}
	/**
	 * 返回 roleId
	 * @return
	 */
	public Long getRoleid(){
		return this.roleid;
	}
	
	
}
