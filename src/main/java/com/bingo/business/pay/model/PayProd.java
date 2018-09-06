package com.bingo.business.pay.model;

import com.bingo.common.model.PageModel;
import com.bingo.common.utility.RandomUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author huangtw
 * 2018-07-09 09:34:03
 * 对象功能:  快速支付商品表Model对象,作废
 */
@Entity
@Table(name="T_PAY_PROD")
public class PayProd extends PageModel{

	@Column(name = "bus_id",updatable = false)
	private Long  busId;//商户ID
	
	
	@Id
	@GeneratedValue //相当于native  相当于mysql的表内自增)
	@Column(name = "prod_id")
	private Long  prodId;//产品ID

	@Column(name = "rid")
	private String  rid;//产品的rid

	@Column(name = "prod_name")
	private String  prodName;//产品名称
	
	
	@Column(name = "prod_price")
	private Float  prodPrice;//产品价格

	@Column(name = "address_type")
	private Integer addressType=1;//地址类型，0：无需要配送地址 1：需要配送


	@Column(name = "max_count")
	private Integer maxCount=1;//最多订购商品，1：一次最多只能订购1个


	@Column(name = "state")
	private int state=1;//状态 0无效，1有效
	
	
	@Column(name = "createtime",updatable = false)
	protected String  createtime;//createtime

	@Transient
	private String createtimeStr = "";//日期的格式化输出

	@Transient
	private String prodPriceStr = "";//价格格式输出
	
	
	
	/**
	 * 对象构建方法
	 * @return
	 */
	public PayProd(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		this.rid= RandomUtils.getRandomString(16);
		this.createtime=format.format(new Date());
	}



	public void setProdId(Long prodId){
		this.prodId = prodId;
	}
	/**
	 * 返回 prod_id
	 * @return
	 */
	public Long getProdId(){
		return this.prodId;
	}
	
	public void setProdName(String prodName){
		this.prodName = prodName;
	}
	/**
	 * 返回 prod_name
	 * @return
	 */
	public String getProdName(){
		return this.prodName;
	}

	public Float getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(Float prodPrice) {
		this.prodPrice = prodPrice;
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


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Long getBusId() {
		return busId;
	}

	public void setBusId(Long busId) {
		this.busId = busId;
	}

	public Integer getAddressType() {
		return addressType;
	}

	public void setAddressType(Integer addressType) {
		this.addressType = addressType;
	}

	public Integer getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getCreatetimeStr() throws ParseException {
		if(this.createtime==null){
			return null;
		}
		if(this.createtime.indexOf("-")!=-1){
			return createtime;
		}
		if(this.createtime.length()==14){
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format2.format(format.parse(createtime));
		}
		return createtime;
	}

	public void setCreatetimeStr(String createtimeStr) {
		this.createtimeStr = createtimeStr;
	}

	public String getProdPriceStr() {
		return String.format("%.2f", this.prodPrice) ;
		//return prodPriceStr;
	}

	public void setProdPriceStr(String prodPriceStr) {
		this.prodPriceStr = prodPriceStr;
	}
}
