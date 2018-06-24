package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-06-25 00:31:07
 * 对象功能: ç³»ç»è§è² Model对象
 */
@Entity
@Table(name="T_sys_role")
public class SysRole extends PageModel{

	@Id
	@TableGenerator(name="GENERATOR_ID",table="T_SYS_DB_GENERATOR",allocationSize=10) 
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GENERATOR_ID")
	@Column(name = "roleId")
	protected Long  roleid;//roleId
	
	
	@Column(name = "roleType")
	protected Long  roletype;//roleType
	
	
	@Column(name = "roleName")
	protected String  rolename;//roleName
	
	
	@Column(name = "roleCode")
	protected String  rolecode;//roleCode
	
	
	@Column(name = "roleState")
	protected Long  rolestate;//roleState
	
	
	@Column(name = "createTime")
	protected String  createtime;//createTime
	
	
	@Column(name = "updateTime")
	protected String  updatetime;//updateTime
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysRole(){
	
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
	
	public void setRoletype(Long roletype){
		this.roletype = roletype;
	}
	/**
	 * 返回 roleType
	 * @return
	 */
	public Long getRoletype(){
		return this.roletype;
	}
	
	public void setRolename(String rolename){
		this.rolename = rolename;
	}
	/**
	 * 返回 roleName
	 * @return
	 */
	public String getRolename(){
		return this.rolename;
	}
	
	public void setRolecode(String rolecode){
		this.rolecode = rolecode;
	}
	/**
	 * 返回 roleCode
	 * @return
	 */
	public String getRolecode(){
		return this.rolecode;
	}
	
	public void setRolestate(Long rolestate){
		this.rolestate = rolestate;
	}
	/**
	 * 返回 roleState
	 * @return
	 */
	public Long getRolestate(){
		return this.rolestate;
	}
	
	public void setCreatetime(String createtime){
		this.createtime = createtime;
	}
	/**
	 * 返回 createTime
	 * @return
	 */
	public String getCreatetime(){
		return this.createtime;
	}
	
	public void setUpdatetime(String updatetime){
		this.updatetime = updatetime;
	}
	/**
	 * 返回 updateTime
	 * @return
	 */
	public String getUpdatetime(){
		return this.updatetime;
	}
	
	
}
