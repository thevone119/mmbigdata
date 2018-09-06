package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author huangtw
 * 2018-09-06 22:49:44
 * 对象功能:  Model对象
 */
@Entity
@Table(name="t_pay_leave_msg")
public class PayLeaveMsg extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "mid")
	protected Long  mid;//mid
	
	
	@Column(name = "userid")
	protected Long  userid;//userid
	
	
	@Column(name = "createtime")
	protected String  createtime;//createtime
	
	
	@Column(name = "phone")
	protected String  phone;//phone
	
	
	@Column(name = "username")
	protected String  username;//username
	
	
	@Column(name = "leave_msg")
	protected String  leaveMsg;//leave_msg
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayLeaveMsg(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		this.createtime=format.format(new Date());
	}
	
	public void setMid(Long mid){
		this.mid = mid;
	}
	/**
	 * 返回 mid
	 * @return
	 */
	public Long getMid(){
		return this.mid;
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
	
	public void setPhone(String phone){
		this.phone = phone;
	}
	/**
	 * 返回 phone
	 * @return
	 */
	public String getPhone(){
		return this.phone;
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
	
	public void setLeaveMsg(String leaveMsg){
		this.leaveMsg = leaveMsg;
	}
	/**
	 * 返回 leave_msg
	 * @return
	 */
	public String getLeaveMsg(){
		return this.leaveMsg;
	}
	
	
}
