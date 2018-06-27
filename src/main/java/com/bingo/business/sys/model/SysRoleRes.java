package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-06-25 00:30:49
 * 对象功能: ç³»ç»è§è²èµæºå³è Model对象
 */
@Entity
@Table(name="T_sys_role_res")
public class SysRoleRes extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "sid")
	protected Long  sid;//sid
	
	
	@Column(name = "resid")
	protected Long  resid;//resId
	
	
	@Column(name = "roleid")
	protected Long  roleid;//roleId
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysRoleRes(){
	
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
	
	public void setResid(Long resid){
		this.resid = resid;
	}
	/**
	 * 返回 resId
	 * @return
	 */
	public Long getResid(){
		return this.resid;
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
