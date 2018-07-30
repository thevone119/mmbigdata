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
	protected Long  logId;//支付记录ID
	
	
	@Column(name = "notify_state",updatable = false)
	protected Integer  notifyState;//通知状态,1成功，2失败
	
	
	@Column(name = "notify_count",updatable = false)
	protected Long  notifyCount;//通知次数，无效，使用log表的次数
	
	
	@Column(name = "notify_start_time",updatable = false)
	protected String  notifyStartTime;//通知开始时间
	
	
	@Column(name = "notify_end_time",updatable = false)
	protected String  notifyEndTime;//通知结束时间
	
	
	@Column(name = "notify_Result",updatable = false)
	protected String  notifyResult;//通知结果
	
	
	
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

	public Integer getNotifyState() {
		return notifyState;
	}

	public void setNotifyState(Integer notifyState) {
		this.notifyState = notifyState;
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
		if(notifyResult!=null && notifyResult.length()>128){
			notifyResult = notifyResult.substring(0,128);
		}
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
