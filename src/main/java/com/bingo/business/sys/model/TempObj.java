package com.bingo.business.sys.model;

import javax.persistence.*;

/**
 * @author huangtw
 * 2017-08-23 16:34:30
 * 临时的对象存储
 * 1.存储临时cookie
 * 2.存储临时对象
 */
@Entity
@Table(name="t_temp_obj")
public class TempObj {

	@Id
	@Column(name = "obj_key")
	protected String  objKey;//对象key


	@Column(name = "obj_value")
	public String  objValue;//对象值

	@Column(name = "obj_type")
	public String  objType;//对象类型，淘宝临时cookie(TB_TEMP_COOKIE)

	@Column(name = "create_time")
	public long createTime = System.currentTimeMillis();//创建时间(毫秒表示)

	@Column(name = "expire_time")
	public long expireTime = 0;//失效时间，默认0 不失效




	/**
	 * 对象构建方法
	 * @return
	 */
	public TempObj(){
		createTime = System.currentTimeMillis();
	}

	public String getObjKey() {
		return objKey;
	}

	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}

	public String getObjValue() {
		return objValue;
	}

	public void setObjValue(String objValue) {
		this.objValue = objValue;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
}
