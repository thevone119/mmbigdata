package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
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
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "roleid")
	protected Long  roleid;//roleId
	
	
	@Column(name = "roletype")
	protected Long  roletype;//roleType
	
	
	@Column(name = "rolename")
	protected String  rolename;//roleName
	
	
	@Column(name = "rolecode")
	protected String  rolecode;//roleCode
	
	
	@Column(name = "rolestate")
	protected Long  rolestate;//roleState


	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createTime
	
	
	@Column(name = "updatetime")
	protected String  updatetime;//updateTime
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysRole(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
		this.updatetime=format.format(new Date());
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
