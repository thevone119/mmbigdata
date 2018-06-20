package com.bingo.business.taobao.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-04-03 22:44:45
 * 对象功能: 淘宝类目 Model对象
 */
@Entity
@Table(name="t_tb_category")
public class TBCategory extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "CID")
	protected Long  cid;//CID
	
	
	@Column(name = "name")
	protected String  name;//name
	
	
	@Column(name = "level")
	protected Long  level;//level
	
	
	@Column(name = "PCID")
	protected Long  pcid;//PCID
	
	
	@Column(name = "create_Time")
	protected String  createTime;//create_Time
	
	
	@Column(name = "update_Time")
	protected String  updateTime;//update_Time
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public TBCategory(){
	
	}


	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getPcid() {
		return pcid;
	}

	public void setPcid(Long pcid) {
		this.pcid = pcid;
	}

	public void setName(String name){
		this.name = name;
	}
	/**
	 * 返回 name
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	
	public void setLevel(Long level){
		this.level = level;
	}
	/**
	 * 返回 level
	 * @return
	 */
	public Long getLevel(){
		return this.level;
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
	
	
}
