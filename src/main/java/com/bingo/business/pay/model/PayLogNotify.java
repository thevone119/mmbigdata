package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:28:31
 * 对象功能:  记录通知表Model对象
 */
@Entity
@Table(name="T_PAY_LOG_notify")
public class PayLogNotify extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "notify_id")
	protected Long  notifyId;//notify_id
	
	
	@Column(name = "log_id",updatable = false)
	protected Long  logId;//log_id
	
	
	@Column(name = "notify_state",updatable = false)
	protected Long  notifyState;//notify_state
	
	
	@Column(name = "notify_count",updatable = false)
	protected Long  notifyCount;//notify_count
	
	
	@Column(name = "notify_start_time",updatable = false)
	protected String  notifyStartTime;//notify_start_time
	
	
	@Column(name = "notify_end_time",updatable = false)
	protected String  notifyEndTime;//notify_end_time
	
	
	@Column(name = "notify_Result",updatable = false)
	protected String  notifyResult;//notify_Result
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayLogNotify(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.notifyStartTime=format.format(new Date());
	}
	
	public void setNotifyId(Long notifyId){
		this.notifyId = notifyId;
	}
	/**
	 * 返回 notify_id
	 * @return
	 */
	public Long getNotifyId(){
		return this.notifyId;
	}
	
	public void setLogId(Long logId){
		this.logId = logId;
	}
	/**
	 * 返回 log_id
	 * @return
	 */
	public Long getLogId(){
		return this.logId;
	}
	
	public void setNotifyState(Long notifyState){
		this.notifyState = notifyState;
	}
	/**
	 * 返回 notify_state
	 * @return
	 */
	public Long getNotifyState(){
		return this.notifyState;
	}
	
	public void setNotifyCount(Long notifyCount){
		this.notifyCount = notifyCount;
	}
	/**
	 * 返回 notify_count
	 * @return
	 */
	public Long getNotifyCount(){
		return this.notifyCount;
	}
	
	public void setNotifyStartTime(String notifyStartTime){
		this.notifyStartTime = notifyStartTime;
	}
	/**
	 * 返回 notify_start_time
	 * @return
	 */
	public String getNotifyStartTime(){
		return this.notifyStartTime;
	}
	
	public void setNotifyEndTime(String notifyEndTime){
		this.notifyEndTime = notifyEndTime;
	}
	/**
	 * 返回 notify_end_time
	 * @return
	 */
	public String getNotifyEndTime(){
		return this.notifyEndTime;
	}
	
	public void setNotifyResult(String notifyResult){
		this.notifyResult = notifyResult;
	}
	/**
	 * 返回 notify_Result
	 * @return
	 */
	public String getNotifyResult(){
		return this.notifyResult;
	}
	
	
}
