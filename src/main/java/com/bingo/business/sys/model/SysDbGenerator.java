package com.bingo.business.sys.model;

import javax.persistence.*;

/**
 * @author huangtw
 * 2017-08-23 16:34:30
 * 对象功能: 系统系列号生成 Model对象
 */
@Entity
@Table(name="T_SYS_DB_GENERATOR")
public class SysDbGenerator {

	@Id
	@Column(name = "SEQUENCE_NAME")
	protected String  sequenceName;//SEQUENCE_NAME
	
	
	@Column(name = "SEQUENCE_NEXT_HI_VALUE")
	public Long  sequenceNextHiValue;//SEQUENCE_NEXT_HI_VALUE

	@Transient
	public long currValue = 0;

	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public SysDbGenerator(){
	
	}
	
	public void setSequenceName(String sequenceName){
		this.sequenceName = sequenceName;
	}
	/**
	 * 返回 SEQUENCE_NAME
	 * @return
	 */
	public String getSequenceName(){
		return this.sequenceName;
	}
	
	public void setSequenceNextHiValue(Long sequenceNextHiValue){
		this.sequenceNextHiValue = sequenceNextHiValue;
	}
	/**
	 * 返回 SEQUENCE_NEXT_HI_VALUE
	 * @return
	 */
	public Long getSequenceNextHiValue(){
		return this.sequenceNextHiValue;
	}


	public long getCurrValue() {
		return currValue;
	}

	public void setCurrValue(long currValue) {
		this.currValue = currValue;
	}

}
