package com.bingo.business.map.model;

import com.bingo.common.model.PageModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangtw
 * 2018-03-27 09:49:29
 * 对象功能: 坐标转换数据 Model对象
 */
@Entity
@Table(name="t_map_point_change")
public class MapPointChange extends PageModel{

	@Column(name = "blat")
	protected double  blat;//blat
	
	
	@Column(name = "blng")
	protected double  blng;//blng
	
	
	@Column(name = "glat")
	protected double  glat;//glat
	
	
	@Column(name = "glng")
	protected double  glng;//glng
	
	
	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增
	@Column(name = "id")
	protected Long  id;//id
	
	
	@Column(name = "createtime")
	protected String  createtime;//createtime
	
	
	@Column(name = "batch")
	protected String  batch;//batch
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public MapPointChange(){
	
	}

	public double getBlat() {
		return blat;
	}

	public void setBlat(double blat) {
		this.blat = blat;
	}

	public double getBlng() {
		return blng;
	}

	public void setBlng(double blng) {
		this.blng = blng;
	}

	public double getGlat() {
		return glat;
	}

	public void setGlat(double glat) {
		this.glat = glat;
	}

	public double getGlng() {
		return glng;
	}

	public void setGlng(double glng) {
		this.glng = glng;
	}

	public void setId(Long id){
		this.id = id;
	}
	/**
	 * 返回 id
	 * @return
	 */
	public Long getId(){
		return this.id;
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
	
	public void setBatch(String batch){
		this.batch = batch;
	}
	/**
	 * 返回 batch
	 * @return
	 */
	public String getBatch(){
		return this.batch;
	}


	@Override
	public String toString(){
		return blat+","+blng+":"+glat+","+glng;
	}
	
	
}
