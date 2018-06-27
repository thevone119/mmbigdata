package com.bingo.business.sys.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-06-25 00:31:29
 * 对象功能: 系统资源 Model对象
 */
@Entity
@Table(name="T_sys_res")
public class SysRes extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "resid")
	protected Long  resid;//资源ID
	
	
	@Column(name = "resname")
	protected String  resname;//资源名称
	
	
	@Column(name = "presid")
	protected Long  presid;//父节点ID


	
	
	@Column(name = "restype")
	protected Long  restype=0L;//资源类型 0：菜单  1：url访问权限
	
	
	@Column(name = "resurl")
	protected String  resurl;//资源的URL
	
	
	@Column(name = "resstate")
	protected Integer  resstate=1;//状态：0：无效，1有效
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//创建时间
	
	
	@Column(name = "updatetime")
	protected String  updatetime;//更新时间
	
	
	@Column(name = "demo")
	protected String  demo;//备注
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysRes(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
		this.updatetime=format.format(new Date());
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
	
	public void setResname(String resname){
		this.resname = resname;
	}
	/**
	 * 返回 resName
	 * @return
	 */
	public String getResname(){
		return this.resname;
	}
	
	public void setPresid(Long presid){
		this.presid = presid;
	}
	/**
	 * 返回 presId
	 * @return
	 */
	public Long getPresid(){
		return this.presid;
	}
	
	public void setRestype(Long restype){
		this.restype = restype;
	}
	/**
	 * 返回 resType
	 * @return
	 */
	public Long getRestype(){
		return this.restype;
	}
	
	public void setResurl(String resurl){
		this.resurl = resurl;
	}
	/**
	 * 返回 resUrl
	 * @return
	 */
	public String getResurl(){
		return this.resurl;
	}

	public Integer getResstate() {
		return resstate;
	}

	public void setResstate(Integer resstate) {
		this.resstate = resstate;
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
	
	public void setDemo(String demo){
		this.demo = demo;
	}
	/**
	 * 返回 demo
	 * @return
	 */
	public String getDemo(){
		return this.demo;
	}
	
	
}
