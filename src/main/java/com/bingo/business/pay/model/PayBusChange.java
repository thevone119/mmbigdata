package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huangtw
 * 2018-07-09 09:33:38
 * 对象功能:  商户充值消费记录表
 */
@Entity
@Table(name="T_PAY_BUS_change")
public class PayBusChange extends PageModel{

	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "cid")
	protected Long  cid;//商户ID,直接使用用户表的ID


	@Column(name = "bus_id",updatable = false)
	protected Long  busId;//商户ID

	@Column(name = "ctype",updatable = false)
	protected Integer  ctype;//充值消费类型,1:充值，2：消费

	@Column(name = "state",updatable = false)
	protected Integer  state;//状态0：无效 1：有效


	@Column(name = "emoney",updatable = false)
	protected float  emoney;//金额,充值，则+,消费则-


	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime

	@Column(name = "demo",updatable = false)
	protected String  demo;//备注



	/**
	 * 对象构建方法
	 * @return
	 */
	public PayBusChange(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.createtime=format.format(new Date());
	}
	
	public void setBusId(Long busId){
		this.busId = busId;
	}
	/**
	 * 返回 bus_id
	 * @return
	 */
	public Long getBusId(){
		return this.busId;
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

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Integer getCtype() {
		return ctype;
	}

	public void setCtype(Integer ctype) {
		this.ctype = ctype;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public float getEmoney() {
		return emoney;
	}

	public void setEmoney(float emoney) {
		this.emoney = emoney;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}
}
